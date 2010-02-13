package org.freebus.fts.persistence.vdx;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.vdx.internal.VdxAssociation;
import org.freebus.fts.persistence.vdx.internal.VdxEntityInfo;
import org.freebus.fts.persistence.vdx.internal.VdxEntityInspector;

/**
 * A simple entity manager that holds the entities of a VD_ file. Access methods
 * are {@link #fetch(Class, Object)} to fetch a single entity by key, and
 * {@link #fetchAll(Class)} to fetch all entities of one type.
 * 
 * Restrictions:
 * 
 * Currently only field annotations are supported. Method annotations will not
 * be processed.
 */
public final class VdxEntityManager
{
   private final VdxFileReader reader;
   private final VdxEntityInspector inspector;

   /**
    * Create an entity-manager object that works on the VD_ file fileName
    * 
    * @param file - The VD_ file that is processed.
    * @param persistenceUnitName -The name of the persistence unit in
    *           persistence.xml
    * 
    * @throws PersistenceException
    */
   public VdxEntityManager(File file, String persistenceUnitName) throws PersistenceException
   {
      inspector = new VdxEntityInspector(persistenceUnitName);
      try
      {
         reader = new VdxFileReader(file);
      }
      catch (IOException e)
      {
         throw new PersistenceException("Failed to read file " + file.getName(), e);
      }
   }

   /**
    * Create an entity-manager object that works on the VD_ file fileName and
    * uses the default persistence-unit of persistence.xml.
    * 
    * @param file - The VD_ file that is processed.
    * 
    * @throws PersistenceException
    */
   public VdxEntityManager(File file) throws PersistenceException
   {
      this(file, "default");
   }

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
      this(new File(fileName), persistenceUnitName);
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
      this(new File(fileName));
   }

   /**
    * @return the internal VDX file reader of the entity manager.
    */
   public VdxFileReader getReader()
   {
      return reader;
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
      final VdxEntityInfo info = inspector.getInfo(entityClass);

      if (info.getObjs() == null)
         loadEntities(info);

      return info.getObjs();
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
      final VdxEntityInfo info = inspector.getInfo(entityClass);

      if (info.getObjs() == null)
      {
         loadEntities(info);
         if (info.getObjs() == null)
            return null;
      }

      @SuppressWarnings("unchecked")
      T result = (T) info.getIds().get(id.toString());

      return result;
   }

   /**
    * Load all entities for the entity-info info from the VD_ file and store
    * them in the entity-info's objects vector.
    * 
    * @param info - The entity-info for which the entities are loaded.
    * @throws PersistenceException
    */
   private void loadEntities(final VdxEntityInfo info) throws PersistenceException
   {
      final Class<?> entityClass = info.getClazz();
      VdxSection section;

      try
      {
         section = reader.getSection(info.getName());
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

      info.setObjs(objs);
      info.setIds(ids);

      try
      {
         //
         // Create the objects and set all non-entity values
         //
         for (int i = 0; i < num; ++i)
         {
            obj = entityClass.newInstance();
            id = materializeObject(obj, info, section, i);

            objs.add(obj);

            if (id != null && !id.isEmpty())
               ids.put(id, obj);
         }

         //
         // Set all entity values
         //
         final Vector<VdxAssociation> assocs = info.getAssociations();
         if (assocs != null && !assocs.isEmpty())
         {
            for (VdxAssociation assoc : assocs)
            {
               final Class<?> targetClass = assoc.getTargetClass();
               final VdxEntityInfo targetInfo = inspector.getInfo(targetClass);
               if (targetInfo == null)
               {
                  throw new PersistenceException("Class " + targetClass.getCanonicalName()
                        + " is missing in persistence.xml");
               }

               final int vdxFieldId = section.getHeader().getIndexOf(assoc.getVdxFieldName());
               if (vdxFieldId < 0)
               {
                  throw new PersistenceException(info.getClazz() + ": VDX section " + info.getName() + " has no field "
                        + assoc.getVdxFieldName());
               }

               for (int i = 0; i < num; ++i)
               {
                  obj = objs.get(i);
                  final String value = section.getValue(i, vdxFieldId);

                  final Annotation a = assoc.getType();
                  if (a instanceof ManyToOne || a instanceof OneToOne)
                  {
                     final Object valueObj = fetch(assoc.getTargetClass(), value);
                     final Field f = assoc.getField();
                     final boolean accessible = f.isAccessible();
                     if (!accessible)
                        f.setAccessible(true);
                     f.set(obj, valueObj);
                     if (!accessible)
                        f.setAccessible(false);
                  }
                  else if (a instanceof OneToMany)
                  {
                     final Field f = assoc.getField();
                     final boolean accessible = f.isAccessible();
                     if (!accessible) f.setAccessible(true);

                     @SuppressWarnings("unchecked")
                     Collection<Object> coll = (Collection<Object>) f.get(obj);

                     if (coll == null)
                     {
                        final Class<?> type = (Class<?>) f.getType();
                        if (Set.class.isAssignableFrom(type))
                           coll = new HashSet<Object>();
                        else if (List.class.isAssignableFrom(type))
                           coll = new LinkedList<Object>();
                        else
                           throw new PersistenceException("Sorry, but the collection type is not implemented: " + type);

                        f.set(obj, coll);
                     }
                     else
                     {
                        coll.clear();
                     }

                     for (Object assocObj : fetchAll(assoc.getTargetClass()))
                     {
                        final Field targetField = assoc.getTargetField();
                        final boolean targetAccessible = targetField.isAccessible();
                        if (!targetAccessible) targetField.setAccessible(true);

                        if (targetField.get(assocObj) == obj)
                           coll.add(assocObj);

                        if (!targetAccessible) targetField.setAccessible(false);
                     }

                     if (!accessible)
                        f.setAccessible(false);
                  }
                  else
                  {
                     throw new PersistenceException("Sorry, this annotation type is not implemented: " + a);
                  }
               }
            }
         }
      }
      catch (InstantiationException e)
      {
         throw new PersistenceException("Could not create an instance of " + entityClass.getName(), e);
      }
      catch (IllegalAccessException e)
      {
         throw new PersistenceException("Could not create an instance of " + entityClass.getName(), e);
      }
   }

   /**
    * Set the fields of the object obj with the idx-th record of the given
    * section. The fields that are entity objects or OneToMany/ManyToOne/...
    * associations are not set by this method.
    * 
    * @return The contents of the object's @{@link Id} field as {@link String}.
    * @throws PersistenceException
    */
   private String materializeObject(Object obj, VdxEntityInfo info, VdxSection section, int idx)
         throws PersistenceException
   {
      final String[] fieldNames = section.getHeader().fields;
      String fieldName = null;
      String idValue = null;
      String value;

      try
      {
         for (int i = 0; i < fieldNames.length; ++i)
         {
            fieldName = fieldNames[i];
            value = section.getValue(idx, i);

            final Field field = info.getField(fieldName);
            if (field == null)
               continue;

            final boolean accessible = field.isAccessible();
            if (!accessible)
               field.setAccessible(true);

            final Class<?> type = field.getType();

            if (type == Integer.class)
            {
               if (value.isEmpty())
                  field.set(obj, null);
               else if (value.indexOf('.') >= 0)
                  field.set(obj, new Integer((int) Double.parseDouble(value)));
               else field.set(obj, new Integer(Integer.parseInt(value)));
            }
            else if (type == int.class)
            {
               if (value.isEmpty())
                  field.setInt(obj, 0);
               else if (value.indexOf('.') >= 0)
                  field.setInt(obj, (int) Double.parseDouble(value));
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
               else field.setBoolean(obj, Integer.parseInt(value) != 0);
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
            else
            {
               final Class<?> fieldClass = (Class<?>) type;
               final Class<?> componentType = fieldClass.getComponentType();

               if (fieldClass.isArray() && componentType == byte.class)
               {
                  // Assume hex data string
                  final int len = value.length() >> 1;
                  final byte[] data = new byte[len];
                  for (int k = 0, l = 0; k < len; ++k, l += 2)
                     data[k] = (byte) (Integer.parseInt(value.substring(l, l + 2), 16));
                  field.set(obj, data);
               }
               else if (fieldClass.isEnum())
               {
                  @SuppressWarnings("unchecked")
                  Class<? extends Enum> enumClass = (Class<? extends Enum>) type;

                  if (value.isEmpty())
                  {
                     field.set(obj, null);
                  }
                  else if (value.matches("\\d*"))
                  {
                     final int ordinal = Integer.parseInt(value);
                     Enum<?> enumVal = null;

                     
                     for (Enum<?> e: enumClass.getEnumConstants())
                     {
                        if (e.ordinal() == ordinal)
                        {
                           enumVal = e;
                           break;
                        }
                     }

                     if (enumVal != null)
                        field.set(obj, enumVal);
                     else throw new IllegalArgumentException("Could not initialize enum of type " + type + " with value: " + value);
                  }
                  else
                  {
                     @SuppressWarnings("unchecked")
                     Enum<?> enumVal = Enum.valueOf(enumClass, value.toUpperCase().replace(' ', '_'));
                     field.set(obj, enumVal);
                  }
               }
               else
               {
                  throw new Exception("Unsupported field class: " + field.getType().getName());
               }
            }

            if (field == info.getId())
               idValue = value;

            if (!accessible)
               field.setAccessible(false);
         }
      }
      catch (Exception e)
      {
         throw new PersistenceException("Could not initialize object of type " + obj.getClass().getCanonicalName()
               + " field " + fieldName + " with data from the VD_ file", e);
      }

      return idValue;
   }
}
