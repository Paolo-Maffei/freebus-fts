package org.freebus.fts.products;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.freebus.fts.persistence.vdx.VdxField;


/**
 * Functional entities are for grouping of the virtual devices.
 */
@Entity
@Table(name = "functional_entity")
public class FunctionalEntity
{
   @Id
   @Column(name = "functional_entity_id", nullable = false)
   private int id;

   @ManyToOne(optional = false, fetch = FetchType.LAZY, targetEntity = Manufacturer.class, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "manufacturer_id", nullable = false, referencedColumnName = "manufacturer_id")
   private Manufacturer manufacturer;

   @Column(name = "functional_entity_name", nullable = false)
   private String name;

   @Column(name = "functional_entity_description")
   private String description;

   @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "parent_id", nullable = true)
   @VdxField(name = "fun_functional_entity_id")
   public FunctionalEntity parent;

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
   public FunctionalEntity(int id, Manufacturer manufacturer, String name, String description)
   {
      this.id = id;
      this.manufacturer = manufacturer;
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
    * @return The manufacturer to whom the functional entity belongs.
    */
   public Manufacturer getManufacturer()
   {
      return manufacturer;
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
    * Set the parent functional entity.
    */
   public void setParent(FunctionalEntity parent)
   {
      this.parent = parent;
   }

   /**
    * @return The parent functional entity.
    */
   public FunctionalEntity getParent()
   {
      return parent;
   }

   /**
    * @return a hash-code for the object.
    */
   @Override
   public int hashCode()
   {
      return (id << 10) | (manufacturer == null ? 0 : manufacturer.getId());
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
      return id == oo.id && manufacturer == oo.manufacturer;
   }

   /**
    * Returns a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      return getClass().getSimpleName() + " #" + id + " \"" + name + "\"";
   }
}
