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
   private Class<?> targetClass;
   private Field targetField;

   /**
    * Create an association object.
    */
   public VdxAssociation(Annotation type, Field field, Class<?> targetClass)
   {
      this.type = type;
      this.field = field;
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
    * @return The field of the target for bidirectional associations.
    */
   public Field getTargetField()
   {
      return targetField;
   }

   /**
    * Set the field of the target for bidirectional associations.
    */
   public void setTargetField(Field targetField)
   {
      this.targetField = targetField;
   }
}
