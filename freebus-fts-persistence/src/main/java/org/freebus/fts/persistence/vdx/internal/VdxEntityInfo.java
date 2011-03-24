package org.freebus.fts.persistence.vdx.internal;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.freebus.fts.persistence.vdx.VdxEntityManager;


/**
 * Entity details for the {@link VdxEntityManager}.
 */
public class VdxEntityInfo
{
   private boolean inspected;
   
   // The VD_ section name of the entity
   private String name;

   // The class of the entity
   private Class<?> clazz;

   // The @Id field, if any
   private Field id;

   // All annotated fields that we will process
   private final Map<String, Field> fields = new HashMap<String, Field>();

   // All objects of this entity type that the entity manager has loaded
   private Vector<Object> objs;

   // The @Id's of the objects in objs, using the contents of the {@link #id}
   // field, as string.
   private Map<String, Object> ids;

   // The external references to other entities. This contains all assiciations
   // that are annotated with @OneToMany, @ManyToOne, etc.
   private final Vector<VdxAssociation> associations = new Vector<VdxAssociation>();

   /**
    * Create an entity info object with default values.
    */
   public VdxEntityInfo()
   {
   }

   /**
    * Create an entity info object.
    *
    * @param name - the name of the object.
    * @param clazz - the class of the entity.
    */
   public VdxEntityInfo(String name, Class<?> clazz)
   {
      this.name = name;
      this.clazz = clazz;
   }

   /**
    * @return True if the corresponding class was inspected.
    */
   public boolean isInspected()
   {
      return inspected;
   }

   /**
    * Set the inspection flag.
    * 
    * @param inspected - True if the corresponding class was inspected.
    */
   public void setInspected(boolean inspected)
   {
      this.inspected = inspected;
   }

   /**
    * @return The name of the VDX section
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the VDX section.
    * 
    * @param name - the name of the section.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return The class of the entity.
    */
   public Class<?> getClazz()
   {
      return clazz;
   }

   /**
    * Set the class of the entity.
    * 
    * @param clazz - the class to set.
    */
   public void setClazz(Class<?> clazz)
   {
      this.clazz = clazz;
   }

   /**
    * @return The {@link @Id} field, may be null.
    */
   public Field getId()
   {
      return id;
   }

   /**
    * Set the {@link @Id} field.
    *
    * @param id - the field to set, may be null.
    */
   public void setId(Field id)
   {
      this.id = id;
   }

   /**
    * @return The objects.
    */
   public Vector<Object> getObjs()
   {
      return objs;
   }

   /**
    * Set the objects.
    *
    * @param objs - the vector of objects to set.
    */
   public void setObjs(Vector<Object> objs)
   {
      this.objs = objs;
   }

   /**
    * @return The IDs.
    */
   public Map<String, Object> getIds()
   {
      return ids;
   }

   /**
    * Set the IDs.
    *
    * @param ids - the IDs to set.
    */
   public void setIds(Map<String, Object> ids)
   {
      this.ids = ids;
   }

   /**
    * @return The fields.
    */
   public Map<String, Field> getFields()
   {
      return fields;
   }

   /**
    * Get a specific field.
    *
    * @param name - the name of the field to get.
    *
    * @return The field.
    */
   public Field getField(String name)
   {
      return fields.get(name);
   }

   /**
    * Add a field.
    * 
    * @param name - the name of the field.
    * @param field - the field.
    */
   public void addField(String name, Field field)
   {
      fields.put(name, field);
   }

   /**
    * @return The associations.
    */
   public Vector<VdxAssociation> getAssociations()
   {
      return associations;
   }

   /**
    * Add an association.
    * 
    * @param association - the association to add.
    */
   public void addAssociation(VdxAssociation association)
   {
      associations.add(association);
   }
}
