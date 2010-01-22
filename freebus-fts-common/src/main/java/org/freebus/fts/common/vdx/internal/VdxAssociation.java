package org.freebus.fts.common.vdx.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.OneToMany;

/**
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

   /**
    * Create an association object.
    */
   public VdxAssociation(Annotation type, Field field, String vdxFieldName, Class<?> targetClass)
   {
      this.type = type;
      this.field = field;
      this.setVdxFieldName(vdxFieldName);
      this.targetClass = targetClass;
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
    */
   public void setField(Field field)
   {
      this.field = field;
   }

   /**
    * Set the name of the VD_ table field.
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
    */
   public void setTargetClass(Class<?> targetClass)
   {
      this.targetClass = targetClass;
   }

   /**
    * Set the field in the target class that is used for lookup of the entity
    * object by xToMany annotations.
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
}
