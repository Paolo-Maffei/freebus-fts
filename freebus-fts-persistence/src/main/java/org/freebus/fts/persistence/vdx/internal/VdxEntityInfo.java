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

   public VdxEntityInfo(String name, Class<?> clazz)
   {
      this.name = name;
      this.clazz = clazz;
   }

   public VdxEntityInfo()
   {
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
    * Set the name of the VDX section
    */
   public void setName(String name)
   {
      this.name = name;
   }

   public Class<?> getClazz()
   {
      return clazz;
   }

   public void setClazz(Class<?> clazz)
   {
      this.clazz = clazz;
   }

   public Field getId()
   {
      return id;
   }

   public void setId(Field id)
   {
      this.id = id;
   }

   public Vector<Object> getObjs()
   {
      return objs;
   }

   public void setObjs(Vector<Object> objs)
   {
      this.objs = objs;
   }

   public Map<String, Object> getIds()
   {
      return ids;
   }

   public void setIds(Map<String, Object> ids)
   {
      this.ids = ids;
   }

   public Map<String, Field> getFields()
   {
      return fields;
   }

   public Field getField(String name)
   {
      return fields.get(name);
   }

   public void addField(String name, Field field)
   {
      fields.put(name, field);
   }

   public Vector<VdxAssociation> getAssociations()
   {
      return associations;
   }

   /**
    * Add an association.
    */
   public void addAssociation(VdxAssociation association)
   {
      associations.add(association);
   }
}
