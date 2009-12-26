package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Functional entities are for grouping of the virtual devices.
 */
@Entity
@Table(name = "functional_entry")
public class FunctionalEntity
{
   @Id
   @Column(name = "functional_entity_id", nullable = false)
   private int id;
   
   @Column(name = "manufacturer_id", nullable = false)
   private int manufacturerId;

   @Column(name = "functional_entity_name", nullable = false)
   private String name;

   @Column(name = "functional_entity_description")
   private String description;

   @Column(name = "fun_functional_entity_id")
   private int parentId = 0;

   /**
    * Create an empty object.
    */
   public FunctionalEntity()
   {
   }
   
   /**
    * Create a new functional entity.
    *
    * @param id - the identifier of the functional entity.
    * @param manufacturerId - the id of the owning manufacturer.
    * @param name - the name of the functional entity.
    * @param description - the description of the functional entity.
    */
   public FunctionalEntity(int id, int manufacturerId, String name, String description)
   {
      this.id = id;
      this.manufacturerId = manufacturerId;
      this.name = name;
      this.description = description;
   }

   /**
    * @return the identifier of the functional entity.
    */
   public int getId()
   {
      return id;
   }

   /**
    * @return the identifier of the manufacturer to whom the functional entity belongs.
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
      if (!(o instanceof FunctionalEntity)) return false;
      final FunctionalEntity oo = (FunctionalEntity)o;
      return id == oo.id && manufacturerId == oo.manufacturerId;
   }

   /**
    * Returns a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      return name;
   }
}
