package org.freebus.fts.common.vdx;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A simple entity manager that holds the entities of a VD_ file.
 * 
 * Restrictions:
 * 
 * Currently only field annotations are supported. Method annotations will not
 * be processed.
 */
public final class VdxEntityManager
{
   private class EntityInfo
   {
      // The VD_ section name of the entity
      public String name;

      // The class of the entity
      public Class<?> clazz;

      // The @Id field, if any
      public Field id;

      // All annotated fields that we will process
      public final Map<String, Field> fields = new HashMap<String, Field>();

      // All objects of this entity type
      public Vector<Object> objs;

      // The @Id's of the objects in objs
      public Map<String, Object> ids;
   }

   private final VdxFileReader reader;
   private final Map<String, EntityInfo> nameInfos = new HashMap<String, EntityInfo>();
   private final Map<Class<?>, EntityInfo> classInfos = new HashMap<Class<?>, EntityInfo>();

   /**
    * Create an entity-manager object that works on the VD_ file fileName
    * 
    * @param fileName - The name of the VD_ file that is processed.
    * @param persistenceUnitName -The name of the persistence unit in
    *           persistence.xml
    * 
    * @throws PersistenceException
    */
   public VdxEntityManager(String fileName, String persistenceUnitName) throws PersistenceException
   {
      scanClasses(persistenceUnitName);
      try
      {
         reader = new VdxFileReader(fileName);
      }
      catch (IOException e)
      {
         throw new PersistenceException("failed to read file " + fileName, e);
      }
   }

   /**
    * Create an entity-manager object that works on the VD_ file fileName and
    * uses the default persistence-unit of persistence.xml.
    * 
    * @param fileName - The name of the VD_ file that is processed.
    * 
    * @throws PersistenceException
    */
   public VdxEntityManager(String fileName) throws PersistenceException
   {
      this(fileName, "default");
   }

   /**
    * Fetch all entities of the class entityClass from the VD_ file. The
    * returned list is unsorted.
    * 
    * @return The found entities, or null if the VD_ file contains no matching
    *         section.
    */
   public List<?> fetchAll(Class<?> entityClass) throws PersistenceException
   {
      final EntityInfo info = classInfos.get(entityClass);
      if (info == null)
         throw new PersistenceException("Class " + entityClass.getCanonicalName() + " is missing in persistence.xml");

      if (info.objs == null)
      {
         loadEntities(info);
         if (info.objs == null)
            return null;
      }

      final int num = info.objs.size();
      if (num <= 0)
         return null;

      return info.objs;
   }

   /**
    * Fetch an entity object by it's key.
    * 
    * @param entityClass - The class of the searched entity.
    * @param id - The key for the entities' @{@link Id} annotated field.
    * 
    * @return The found object, or null if the object was not found.
    * @throws PersistenceException
    */
   public <T extends Object> T fetch(Class<T> entityClass, Object id) throws PersistenceException
   {
      final EntityInfo info = classInfos.get(entityClass);
      if (info == null)
         throw new PersistenceException("Class " + entityClass.getCanonicalName() + " is missing in persistence.xml");

      if (info.objs == null)
      {
         loadEntities(info);
         if (info.objs == null)
            return null;
      }

      @SuppressWarnings("unchecked")
      final T result = (T) info.ids.get(id.toString());

      return result;
   }

   /**
    * Load all entities for the entity-info info from the VD_ file and store
    * them in the class-info's objects vector.
    * 
    * @param info - The entity-info for which the entities are loaded.
    * @throws PersistenceException
    */
   private void loadEntities(final EntityInfo info) throws PersistenceException
   {
      final Class<?> entityClass = info.clazz;
      VdxSection section;

      try
      {
         section = reader.getSection(info.name);
         if (section == null)
            return;
      }
      catch (IOException e)
      {
         throw new PersistenceException(e);
      }

      final int num = section.getNumElements();
      final Vector<Object> objs = new Vector<Object>(num);
      final Map<String, Object> ids = new HashMap<String, Object>();
      Object obj;
      String id;

      try
      {
         for (int i = 0; i < num; ++i)
         {
            obj = entityClass.newInstance();
            id = initObject(obj, info, section, i);

            objs.add(obj);

            if (id != null && !id.isEmpty())
               ids.put(id, obj);
         }
      }
      catch (InstantiationException e)
      {
         throw new PersistenceException("Could not create an instance of " + entityClass.getName());
      }
      catch (IllegalAccessException e)
      {
         throw new PersistenceException("Could not create an instance of " + entityClass.getName());
      }

      info.objs = objs;
      info.ids = ids;
   }

   /**
    * Initialize the object obj with the idx-th record of the given section.
    * 
    * @return The contents of the object's @{@link Id} field as {@link String}.
    * @throws PersistenceException
    */
   private String initObject(Object obj, EntityInfo info, VdxSection section, int idx) throws PersistenceException
   {
      final String[] fieldNames = section.getHeader().fields;
      String fieldName, value;
      String idValue = null;

      try
      {
         for (int i = 0; i < fieldNames.length; ++i)
         {
            fieldName = fieldNames[i];
            value = section.getValue(idx, i);

            final Field field = info.fields.get(fieldName);
            if (field == null)
               continue;

            final boolean accessible = field.isAccessible();
            if (!accessible)
               field.setAccessible(true);

            final Class<?> type = field.getType();

            if (type == Integer.class || type == int.class)
            {
               if (value.isEmpty())
                  field.setInt(obj, 0);
               else field.setInt(obj, Integer.parseInt(value));
            }
            else if (type == String.class)
            {
               field.set(obj, value);
            }
            else if (type == Boolean.class || type == boolean.class)
            {
               if (value.isEmpty())
                  field.setBoolean(obj, false);
               else field.setBoolean(obj, Boolean.parseBoolean(value));
            }
            else if (type == Float.class || type == float.class)
            {
               if (value.isEmpty())
                  field.setFloat(obj, 0.0f);
               else field.setFloat(obj, Float.parseFloat(value));
            }
            else if (type == Double.class || type == double.class)
            {
               if (value.isEmpty())
                  field.setDouble(obj, 0.0);
               else field.setDouble(obj, Double.parseDouble(value));
            }
            else if (classInfos.containsKey(type))
            {
               if (value.isEmpty())
               {
                  field.set(obj, null);
               }
               else
               {
                  // Need to fetch the entity of another section/table
                  final Object valueObj = fetch(type, value);
                  field.set(obj, valueObj);
               }
            }
            else
            {
               throw new Exception("Unsupported field class: " + field.getType().getName());
            }

            if (field == info.id)
               idValue = value;

            if (!accessible)
               field.setAccessible(false);
         }
      }
      catch (Exception e)
      {
         throw new PersistenceException("Could not initialize object of type " + obj.getClass().getCanonicalName()
               + " with VD_ file data", e);
      }

      return idValue;
   }

   /**
    * Scan all classes from the persistence.xml file.
    * 
    * @throws Exception
    */
   private synchronized void scanClasses(String persistenceUnitName) throws PersistenceException
   {
      final Node persistenceUnitNode = loadPersistenceUnit("META-INF/persistence.xml", persistenceUnitName);

      nameInfos.clear();

      final NodeList classNodes = persistenceUnitNode.getChildNodes();
      for (int i = 0; i < classNodes.getLength(); ++i)
      {
         final Node classNode = classNodes.item(i);
         if (!classNode.getNodeName().equals("class"))
            continue;

         scanClass(classNode.getTextContent().trim());
      }
   }

   /**
    * Scan the class with the given class-name.
    * 
    * @param className - The name of the class that is to be scanned.
    * 
    * @throws Exception
    */
   private void scanClass(String className) throws PersistenceException
   {
      Class<?> clazz;
      try
      {
         clazz = getClass().getClassLoader().loadClass(className);
      }
      catch (Exception e)
      {
         throw new PersistenceException(e);
      }

      final String entityName = getEntityNameOf(clazz).toLowerCase();
      if (nameInfos.containsKey(entityName))
      {
         throw new PersistenceException("Entity name " + entityName + " is not unique. Used for " + className
               + " and for " + nameInfos.get(entityName).clazz.getCanonicalName());
      }

      final EntityInfo info = new EntityInfo();
      info.name = entityName;
      info.clazz = clazz;

      scanClassFields(clazz, info);

      nameInfos.put(entityName, info);
      classInfos.put(clazz, info);
   }

   /**
    * Scan the fields of the class clazz and put the annotated fields into the
    * info's fields map.
    */
   private void scanClassFields(final Class<?> clazz, final EntityInfo info)
   {
      final Class<?> superClazz = clazz.getSuperclass();
      if (superClazz != null && superClazz.getAnnotation(Entity.class) != null)
         scanClassFields(superClazz, info);

      for (Field field : clazz.getDeclaredFields())
      {
         final Id idField = field.getAnnotation(Id.class);
         if (idField != null)
            info.id = field;

         final VdxField vdxField = field.getAnnotation(VdxField.class);
         if (vdxField != null)
         {
            info.fields.put(vdxField.name(), field);
            continue;
         }

         final JoinColumn jcolField = field.getAnnotation(JoinColumn.class);
         if (jcolField != null && !jcolField.name().isEmpty())
         {
            info.fields.put(jcolField.name(), field);
         }

         final Column colField = field.getAnnotation(Column.class);
         if (colField != null && !colField.name().isEmpty())
         {
            info.fields.put(colField.name(), field);
         }
      }
   }

   /**
    * @return the entity name of the class clazz.
    * 
    * @throws PersistenceException If the class does not have a proper
    *            annotation.
    */
   private String getEntityNameOf(Class<?> clazz) throws PersistenceException
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
   private Node loadPersistenceUnit(String fileName, String persistenceUnitName) throws PersistenceException
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

            if (persistenceUnitName.equals(name))
               return node;
         }
      }
      catch (Exception e)
      {
         throw new PersistenceException(e);
      }

      throw new PersistenceException("No persistence-unit named " + persistenceUnitName + " found in " + fileName);
   }
}
