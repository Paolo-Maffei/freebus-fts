package org.freebus.fts.products;

import java.lang.ref.WeakReference;

/**
 * Functional entities are for grouping of the products.
 */
public class FunctionalEntity
{
   private final int id;
   private final String name, description;
   private WeakReference<FunctionalEntity> parent = null;

   /**
    * Create a new functional entity.
    *
    * @param id is the identifier of the entity.
    * @param name is the name of the entity.
    * @param description is the description of the entity.
    */
   public FunctionalEntity(int id, String name, String description)
   {
      this.id = id;
      this.name = name;
      this.description = description;
   }

   /**
    * @return the id.
    */
   public int getId()
   {
      return id;
   }

   /**
    * @return the name.
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the description.
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * Set the parent. May be null.
    */
   public void setParent(FunctionalEntity parent)
   {
      if (parent==null) this.parent = null;
      else this.parent = new WeakReference<FunctionalEntity>(parent);
   }

   /**
    * @return the parent. May be null.
    */
   public FunctionalEntity getParent()
   {
      if (parent==null) return null;
      return parent.get();
   }

   /**
    * Returns a hash-code for the object.
    */
   @Override
   public int hashCode()
   {
      return id;
   }
   
   /**
    * Compare two objects by id.
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o==this) return true;
      if (!(o instanceof FunctionalEntity)) return false;
      final FunctionalEntity oo = (FunctionalEntity)o;
      return id==oo.id;
   }

   /**
    * Returns a human readable representation of the object
    */
   @Override
   public String toString()
   {
      return name+" ["+Integer.toString(id)+"]";
   }
}
