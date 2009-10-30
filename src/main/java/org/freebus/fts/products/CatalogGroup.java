package org.freebus.fts.products;

import java.lang.ref.WeakReference;

/**
 * Catalog groups, or "functional entities", as they are called in the VDX files,
 * are for grouping of the products.
 */
public class CatalogGroup
{
   private final int id, manufacturerId;
   private final String name, description;
   private int parentId = 0;

   /**
    * Create a new catalog group.
    *
    * @param id - the identifier of the catalog group.
    * @param manufacturerId - the id of the manufacturer to which the catalog group belongs.
    * @param name - the name of the catalog group.
    * @param description - the description of the catalog group.
    */
   public CatalogGroup(int id, int manufacturerId, String name, String description)
   {
      this.id = id;
      this.manufacturerId = manufacturerId;
      this.name = name;
      this.description = description;
   }

   /**
    * @return the identifier of the catalog-group.
    */
   public int getId()
   {
      return id;
   }

   /**
    * @return the identifier of the manufacturer to which the catalog group belongs.
    */
   public int getManufacturerId()
   {
      return manufacturerId;
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
    * Set the parent id. 0 for no parent.
    */
   public void setParentId(int parentId)
   {
      this.parentId = parentId;
   }

   /**
    * @return the parent id. 0 is returned for no parent.
    */
   public int getParentId()
   {
      return parentId;
   }

   /**
    * @return a hash-code for the object.
    */
   @Override
   public int hashCode()
   {
      return (id << 10) | manufacturerId;
   }
   
   /**
    * Compare two objects.
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o==this) return true;
      if (!(o instanceof CatalogGroup)) return false;
      final CatalogGroup oo = (CatalogGroup)o;
      return id == oo.id && manufacturerId == oo.manufacturerId;
   }

   /**
    * Returns a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      return name + " [" + Integer.toString(id) + "]";
   }
}
