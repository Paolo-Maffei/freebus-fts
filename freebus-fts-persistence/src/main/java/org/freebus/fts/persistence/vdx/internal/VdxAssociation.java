package org.freebus.fts.persistence.vdx.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.OneToMany;

/**
 * JPA associations between entity classes.
 * <p>
 * Information about a JPA association of one entity class to another entity
 * class, usually annotated with @{@link OneToMany},
 * 
 * @{@link ManyToOne}, etc.
 */
public final class VdxAssociation
{
   private Annotation type;
   private Field field;
   private String vdxFieldName;
   private Class<?> targetClass;
   private Field targetField;
   private boolean nullable;

   /**
    * Create an association object.
    * 
    * @param type - the type of the annotation.
    * @param field - the field.
    * @param vdxFieldName - the VDX field name.
    * @param targetClass - the target class.
    * @param nullable - true if the field is nullable.
    */
   public VdxAssociation(Annotation type, Field field, String vdxFieldName, Class<?> targetClass, boolean nullable)
   {
      this.type = type;
      this.field = field;
      this.setVdxFieldName(vdxFieldName);
      this.targetClass = targetClass;
      this.setNullable(nullable);
   }

   /**
    * @return The annotation that the originating class has.
    */
   public Annotation getType()
   {
      return type;
   }

   /**
    * Set the annotation that the originating class has.
    * 
    * @param type - the type of the annotation.
    */
   public void setType(Annotation type)
   {
      this.type = type;
   }

   /**
    * @return The field of the entity which starts the association.
    */
   public Field getField()
   {
      return field;
   }

   /**
    * Set the field of the entity which starts the association.
    * 
    * @param field - the field of the entity.
    */
   public void setField(Field field)
   {
      this.field = field;
   }

   /**
    * Set the name of the VD_ table field.
    * 
    * @param vdxFieldName - the name of the VD_ field.
    */
   public void setVdxFieldName(String vdxFieldName)
   {
      this.vdxFieldName = vdxFieldName;
   }

   /**
    * @return The name of the VD_ table field.
    */
   public String getVdxFieldName()
   {
      return vdxFieldName;
   }

   /**
    * @return The class of the target of the association.
    */
   public Class<?> getTargetClass()
   {
      return targetClass;
   }

   /**
    * Set the class of the target of the association.
    * 
    * @param targetClass - the target class.
    */
   public void setTargetClass(Class<?> targetClass)
   {
      this.targetClass = targetClass;
   }

   /**
    * Set the field in the target class that is used for lookup of the entity
    * object by xToMany annotations.
    * 
    * @param targetField - the target field.
    */
   public void setTargetField(Field targetField)
   {
      this.targetField = targetField;
   }

   /**
    * @return The field in the target class that is used for lookup of the
    *         entity object by xToMany annotations.
    */
   public Field getTargetField()
   {
      return targetField;
   }

   /**
    * @param nullable the nullable to set
    */
   public void setNullable(boolean nullable)
   {
      this.nullable = nullable;
   }

   /**
    * @return the nullable
    */
   public boolean isNullable()
   {
      return nullable;
   }
}
