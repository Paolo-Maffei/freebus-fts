package org.freebus.fts.persistence.vdx;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.log4j.Logger;

public class VdxNewEntityManager
{
   private final VdxFileReader reader;
   private final EntityManager entityManager;
   private final Map<Class<?>, HashMap<?, ?>> entityCache = new HashMap<Class<?>, HashMap<?, ?>>();

   /**
    * Create an entity-manager object that works on the VD_ file fileName. The
    * given entity manager is used to detect already persisted objects.
    *
    * @param file - The VD_ file that is processed.
    * @param entityManager -The entity manager to use.
    *
    * @throws PersistenceException
    */
   public VdxNewEntityManager(File file, EntityManager entityManager) throws PersistenceException
   {
      this.entityManager = entityManager;

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
    * @return the VDX file reader that is used.
    */
   public VdxFileReader getReader()
   {
      return reader;
   }

   /**
    * @return the entity manager that is used.
    */
   public EntityManager getEntityManager()
   {
      return entityManager;
   }

   /**
    * Fetch all entities of the class entityClass from the VD_ file. The
    * returned list is unsorted.
    *
    * @return The found entities, or null if the VD_ file contains no matching
    *         section.
    */
   public Collection<?> fetchAll(Class<?> entityClass) throws PersistenceException
   {
      if (!entityCache.containsKey(entityClass))
         loadEntities(entityClass);

      final HashMap<?, ?> entitiesMap = entityCache.get(entityClass);
      return entitiesMap.values();
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
      if (!entityCache.containsKey(entityClass))
         loadEntities(entityClass);

      final HashMap<?, ?> entitiesMap = entityCache.get(entityClass);

      @SuppressWarnings("unchecked")
      T result = (T) entitiesMap.get(id);

      return result;
   }

   /**
    * Load all entities of a class from the VD_ file. The loaded entities are
    * stored in the <code>entityCache</code>.
    *
    * @param entityClass - The entity class of the entities that are to be
    *           loaded.
    * @return a map with id to entity mappings. Key is the id, value is the
    *         entity. Returns null if the VD_ file contains no table for the
    *         entity class.
    * @throws PersistenceException
    */
   public HashMap<?, ?> loadEntities(final Class<?> entityClass) throws PersistenceException
   {
      final HashMap<Object, Object> result = new HashMap<Object, Object>();
      final Metamodel metaModel = entityManager.getMetamodel();
      final EntityType<?> entityType = metaModel.entity(entityClass);
      final String vdxTableName = vdxTableNameOf(entityClass);
      VdxSection table;

      final Attribute<?, ?> idAttr = entityType.getId(Object.class);
      if (idAttr == null)
         throw new PersistenceException("entity has no @Id: " + entityClass);

      try
      {
         final Field idField = entityType.getJavaType().getDeclaredField(idAttr.getName());
         if (idField == null)
            throw new PersistenceException("@Id must annotate a field, not a getter/setter; in entity " + entityClass);

         table = reader.getSection(vdxTableName);
         if (table == null)
            return null;

         final List<Attribute<?, ?>> fieldMap = createFieldMap(entityType, table.getHeader());

         final int num = table.getNumElements();
         for (int idx = 0; idx < num; ++idx)
         {
            Object obj = materialize(entityType, fieldMap, table, idx);
            final Object idObj = idField.get(obj);

            final Object existingObj = entityManager.find(entityType.getJavaType(), idObj);

            if (existingObj != null)
            {
               Logger.getLogger(getClass()).debug(
                     entityType.getJavaType() + " entity with key \"" + idObj + "\" exists");
               obj = existingObj;
            }

            result.put(idObj, obj);
         }
      }
      catch (Exception e)
      {
         if (e instanceof PersistenceException)
            throw (PersistenceException) e;

         throw new PersistenceException(e);
      }

      return result;
   }

   /**
    * Create an object from a record of the VDX table. Only loads the entity's
    * variables. References are not resolved.
    *
    * @param entityType - the entity type of the entity class to create
    * @param table - the VDX table from where to get the fields
    * @param idx - the index within the VDX table
    *
    * @return the created object
    */
   public Object materialize(EntityType<?> entityType, List<Attribute<?, ?>> fieldMap, VdxSection table, int idx)
   {
      try
      {
         final Object obj = newEntity(entityType);

         for (int fieldIdx = fieldMap.size() - 1; fieldIdx >= 0; --fieldIdx)
         {
            final Attribute<?, ?> attr = fieldMap.get(fieldIdx);
            if (attr == null || attr.isCollection() || attr.isAssociation())
               continue;

            final Field field = entityType.getJavaType().getDeclaredField(attr.getName());
            assign(obj, field, table.getValue(idx, fieldIdx));
         }

         return obj;
      }
      catch (SecurityException e)
      {
         throw new PersistenceException(e);
      }
      catch (NoSuchFieldException e)
      {
         throw new PersistenceException(e);
      }
   }

   /**
    * Assign a value to an entities' field. The value is converted to the proper
    * type.
    *
    * @param obj - the object to assign into.
    * @param field - the attribute that points to the entities' member.
    * @param value - the value that is assigned.
    */
   public void assign(final Object obj, final Field field, final String value)
   {
      try
      {
         final boolean accessible = field.isAccessible();
         if (!accessible)
            field.setAccessible(true);

         final Class<?> type = field.getType();

         if (type == Integer.class)
         {
            if (value.isEmpty())
               field.set(obj, null);
            else if (value.indexOf('.') >= 0)
               field.set(obj, (int) Double.parseDouble(value));
            else field.set(obj, Integer.valueOf(value));
         }
         else if (type == int.class)
         {
            if (value.isEmpty())
               field.setInt(obj, 0);
            else if (value.indexOf('.') >= 0)
               field.setInt(obj, (int) Double.parseDouble(value));
            else field.setInt(obj, Integer.valueOf(value));
         }
         else if (type == String.class)
         {
            field.set(obj, value);
         }
         else if (type == Boolean.class || type == boolean.class)
         {
            if (value.isEmpty())
               field.setBoolean(obj, false);
            else field.setBoolean(obj, Integer.valueOf(value) != 0);
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
            final Class<?> fieldClass = type;
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
               @SuppressWarnings({ "unchecked", "rawtypes" })
               Class<? extends Enum> enumClass = (Class<? extends Enum>) type;

               if (value.isEmpty())
               {
                  field.set(obj, null);
               }
               else if (value.matches("\\d*"))
               {
                  final int ordinal = Integer.parseInt(value);
                  Enum<?> enumVal = null;

                  for (Enum<?> e : enumClass.getEnumConstants())
                  {
                     if (e.ordinal() == ordinal)
                     {
                        enumVal = e;
                        break;
                     }
                  }

                  if (enumVal != null)
                     field.set(obj, enumVal);
                  else throw new IllegalArgumentException("Could not initialize enum of type " + type + " with value: "
                        + value);
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
               throw new PersistenceException("Unsupported field class: " + field.getType().getName());
            }
         }

         if (!accessible)
            field.setAccessible(false);
      }
      catch (IllegalAccessException e)
      {
         throw new PersistenceException(e);
      }
   }

   /**
    * Create VDX field to entity class mappings for the given entity type and
    * VDX table.
    * <p>
    * A vector is returned, which contains an element for every field of the VDX
    * table. For the idx-th field of the VDX table, the vector contains the
    * {@link Attribute attribute} that represents this field in the entity type.
    * If the field is not used in the entity type, the index in the vector
    * contains null.
    *
    * @param entityType - the entity type to inspect.
    * @param tableHeader - the VDX table header to create the mappings for.
    *
    * @return a vector of attributes for the VDX fields. A null at a position
    *         means that the field with that index is not used in the entity
    *         type.
    */
   public List<Attribute<?, ?>> createFieldMap(EntityType<?> entityType, VdxSectionHeader tableHeader)
   {
      try
      {
         final Vector<Attribute<?, ?>> result = new Vector<Attribute<?, ?>>(tableHeader.fields.length);
         result.setSize(tableHeader.fields.length);

         for (final Attribute<?, ?> attr : entityType.getAttributes())
         {
            final Field field = entityType.getJavaType().getDeclaredField(attr.getName());

            final VdxField vdxField = field.getAnnotation(VdxField.class);
            final JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            final Column column = field.getAnnotation(Column.class);
            String name = null;

            if (vdxField != null && vdxField.name() != null)
               name = vdxField.name();
            else if (column != null && column.name() != null)
               name = column.name();
            else if (joinColumn.name() != null)
               name = joinColumn.name();
            else name = attr.getName();

            final int idx = tableHeader.getIndexOf(name);
            if (idx >= 0)
               result.set(idx, attr);
         }

         return result;
      }
      catch (NoSuchFieldException e)
      {
         throw new PersistenceException(e);
      }
      catch (SecurityException e)
      {
         throw new PersistenceException(e);
      }
   }

   /**
    * Create an object of the entity type's Java type.
    *
    * @param entityType - the entity type of the entity class to create
    * @return the created object.
    * @throw PersistenceException if anything goes wrong.
    */
   public Object newEntity(final EntityType<?> entityType) throws PersistenceException
   {
      try
      {
         return entityType.getJavaType().newInstance();
      }
      catch (Exception e)
      {
         throw new PersistenceException("Could not create an instance of " + entityType.getJavaType().getName(), e);
      }
   }

   /**
    * Lookup the VDX table name of an entity class.
    *
    * @param entityClass - the class whose VDX name is requested
    * @return the name of the VD_ table/section of the entity class.
    *
    * @throws IllegalArgumentException if the class has no {@link Entity}
    *            annotation.
    */
   public String vdxTableNameOf(final Class<?> entityClass)
   {
      String name = null;

      final Entity entity = entityClass.getAnnotation(Entity.class);
      final VdxEntity vdxEntity = entityClass.getAnnotation(VdxEntity.class);
      final Table table = entityClass.getAnnotation(Table.class);

      if (entity == null)
         throw new IllegalArgumentException("class is not an entity: " + entityClass);

      if (vdxEntity != null && vdxEntity.name() != null)
         name = vdxEntity.name();
      else if (table != null && table.name() != null)
         name = table.name();
      else if (entity.name() != null)
         name = entity.name();
      else name = entityClass.getSimpleName();

      return name;
   }
}
