package org.freebus.fts.common.vdx.internal;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.freebus.fts.common.vdx.VdxEntity;
import org.freebus.fts.common.vdx.VdxEntityManager;
import org.freebus.fts.common.vdx.VdxField;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The entity-inspector scans entity classes for VDX/JPA annotations and stores
 * the found information in its internal collections, for later use by the
 * {@link VdxEntityManager}.
 */
public final class VdxEntityInspector
{
   private final Map<String, VdxEntityInfo> nameInfos = new HashMap<String, VdxEntityInfo>();
   private final Map<Class<?>, VdxEntityInfo> classInfos = new HashMap<Class<?>, VdxEntityInfo>();
   private final String persistenceUnit;

   /**
    * Create an entity-inspector that processes the classes of the default
    * persistence unit.
    */
   public VdxEntityInspector()
   {
      this("default");
   }

   /**
    * Create an entity-inspector that processes the classes of a persistence
    * unit.
    * 
    * @param persistenceUnit - The name of the persistence unit to be processed.
    */
   public VdxEntityInspector(String persistenceUnit)
   {
      this.persistenceUnit = persistenceUnit;
      readClasses(persistenceUnit);
   }

   /**
    * @return The classes that are defined in the persistence unit.
    */
   public Set<Class<?>> getClasses()
   {
      return classInfos.keySet();
   }

   /**
    * Get the information for a class. The class is inspected if required.
    * 
    * @param clazz - The class which information is requested.
    * 
    * @return The information about the class.
    * @throws PersistenceException - If the class is not found in the
    *            persistence unit of persistence.xml
    */
   public VdxEntityInfo getInfo(Class<?> clazz)
   {
      final VdxEntityInfo info = classInfos.get(clazz);
      if (info == null)
         throw new PersistenceException(clazz + " not found in persistence unit \"" + persistenceUnit + "\"");

      if (!info.isInspected())
         inspect(info);

      return info;
   }

   /**
    * @return True if information about the given class is available.
    */
   public boolean hasClass(Class<?> clazz)
   {
      return classInfos.containsKey(clazz);
   }

   /**
    * Inspect the class that the info contains.
    */
   private void inspect(final VdxEntityInfo info)
   {
      final Class<?> clazz = info.getClazz();
      scanClassFields(clazz, info);

      info.setInspected(true);
   }

   /**
    * Scan the fields of the class clazz and put the annotated fields into the
    * info's fields map.
    */
   private void scanClassFields(final Class<?> clazz, final VdxEntityInfo info)
   {
      final Class<?> superClazz = clazz.getSuperclass();
      if (superClazz != null && superClazz.getAnnotation(Entity.class) != null)
         scanClassFields(superClazz, info);

      for (Field field : clazz.getDeclaredFields())
      {
         String fieldName = null;

         final VdxField annoVdxField = field.getAnnotation(VdxField.class);
         if (annoVdxField != null)
         {
            fieldName = annoVdxField.name();
         }
         else
         {
            final JoinColumn annoJoinColumn = field.getAnnotation(JoinColumn.class);
            if (annoJoinColumn != null)
            {
               fieldName = annoJoinColumn.name();
            }
            else
            {
               final Column annoColumn = field.getAnnotation(Column.class);
               if (annoColumn != null)
               {
                  fieldName = annoColumn.name();
               }
            }
         }

         if (fieldName == null)
            continue;

         for (Annotation a : field.getAnnotations())
         {
            if (a instanceof Id)
            {
               info.setId(field);
            }
            else if (a instanceof OneToMany)
            {
               final OneToMany aa = (OneToMany) a;
               final Class<?> entityClass = getAssociationEntityClass(field.getType(), field.getGenericType());
               final VdxAssociation assoc = new VdxAssociation(aa, field, fieldName, entityClass);
               assoc.setTargetField(findTargetField(assoc.getTargetClass(), clazz));
               info.addAssociation(assoc);
               fieldName = null;
            }
            else if (a instanceof ManyToMany)
            {
               // Warning: ManyToMany is not properly implemented

               final ManyToMany aa = (ManyToMany) a;
               final Class<?> entityClass = getAssociationEntityClass(field.getType(), field.getGenericType());
               final VdxAssociation assoc = new VdxAssociation(aa, field, fieldName, entityClass);
               assoc.setTargetField(findTargetField(assoc.getTargetClass(), clazz));
               info.addAssociation(assoc);
               fieldName = null;
            }
            else if (a instanceof ManyToOne)
            {
               final ManyToOne aa = (ManyToOne) a;
               final VdxAssociation assoc = new VdxAssociation(aa, field, fieldName, field.getType());
               info.addAssociation(assoc);
               fieldName = null;
            }
            else if (a instanceof OneToOne)
            {
               final OneToOne aa = (OneToOne) a;
               final VdxAssociation assoc = new VdxAssociation(aa, field, fieldName, field.getType());
               info.addAssociation(assoc);
               fieldName = null;
            }
         }

         if (fieldName != null)
            info.addField(fieldName, field);
      }
   }

   /**
    * @return The field in the class clazz that is of the type fieldClazz.
    */
   private Field findTargetField(Class<?> clazz, Class<?> fieldClazz)
   {
      for (Field field : clazz.getDeclaredFields())
      {
         if (field.getGenericType() == fieldClazz)
            return field;
      }

      return null;
   }
   
   /**
    * @return The entity class for an association.
    */
   private Class<?> getAssociationEntityClass(Class<?> type, Type genericType)
   {
      // See
      // http://tutorials.jenkov.com/java-reflection/generics.html#fieldtypes

      if (genericType instanceof ParameterizedType)
      {
         final ParameterizedType paramType = (ParameterizedType) genericType;
         final Type[] argTypes = paramType.getActualTypeArguments();
         return (Class<?>) argTypes[argTypes.length - 1];
      }
      return type;
   }

   /**
    * Read the classes that shall be inspected from the persistence.xml file.
    * Only loads the classes - the classes are inspected later on demand.
    * 
    * Called automatically by the constructor.
    * 
    * @param persistenceUnit - The name of the persistence unit to be processed.
    */
   private void readClasses(String persistenceUnit)
   {
      final Node persistenceUnitNode = loadPersistenceUnit("META-INF/persistence.xml", persistenceUnit);
      Class<?> clazz;

      nameInfos.clear();
      classInfos.clear();

      final NodeList classNodes = persistenceUnitNode.getChildNodes();
      for (int i = 0; i < classNodes.getLength(); ++i)
      {
         final Node classNode = classNodes.item(i);
         if (!classNode.getNodeName().equals("class"))
            continue;

         final String className = classNode.getTextContent().trim();

         try
         {
            clazz = getClass().getClassLoader().loadClass(className);
         }
         catch (Exception e)
         {
            throw new PersistenceException(e);
         }

         final String entityName = getVdxEntityNameOf(clazz).toLowerCase();
         if (nameInfos.containsKey(entityName))
         {
            throw new PersistenceException("Entity name " + entityName + " is not unique. Used for " + className
                  + " and for " + nameInfos.get(entityName).getClazz().getCanonicalName());
         }

         final VdxEntityInfo info = new VdxEntityInfo(entityName, clazz);
         nameInfos.put(entityName, info);
         classInfos.put(clazz, info);
      }

   }

   /**
    * @return The VDX entity name of the class clazz.
    * 
    * @throws PersistenceException If the class does not have a proper
    *            annotation.
    */
   private String getVdxEntityNameOf(Class<?> clazz) throws PersistenceException
   {
      final Entity entity = clazz.getAnnotation(Entity.class);
      if (entity == null)
         throw new PersistenceException("Class " + clazz.getCanonicalName() + " contains no entity annotation");

      final VdxEntity vdxEntity = clazz.getAnnotation(VdxEntity.class);
      if (vdxEntity != null)
         return vdxEntity.name();

      if (entity != null && !entity.name().isEmpty())
         return entity.name();

      final Table table = clazz.getAnnotation(Table.class);
      if (table != null && !table.name().isEmpty())
         return table.name();

      return clazz.getSimpleName();
   }

   /**
    * Load the persistence unit DOM nodes from the file fileName.
    * 
    * @throws PersistenceException
    */
   private Node loadPersistenceUnit(String fileName, String persistenceUnit) throws PersistenceException
   {
      try
      {
         final InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
         final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
         final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
         final Document doc = docBuilder.parse(in);
         final Element root = doc.getDocumentElement();

         final NodeList persUnits = root.getElementsByTagName("persistence-unit");
         for (int i = 0; i < persUnits.getLength(); ++i)
         {
            final Node node = persUnits.item(i);
            final String name = node.getAttributes().getNamedItem("name").getNodeValue();

            if (persistenceUnit.equals(name))
               return node;
         }
      }
      catch (Exception e)
      {
         throw new PersistenceException(e);
      }

      throw new PersistenceException("No persistence-unit named " + persistenceUnit + " found in " + fileName);
   }
}
