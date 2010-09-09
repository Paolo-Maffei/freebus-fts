package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.persistence.vdx.VdxField;

/**
 * Functional entities are for grouping of the virtual devices.
 */
@Entity
@Table(name = "functional_entity")
public class FunctionalEntity
{
   @Id
   @TableGenerator(name = "FunctionalEntity", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "FunctionalEntity")
   @Column(name = "functional_entity_id", nullable = false)
   private int id;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "manufacturer_id", nullable = false, referencedColumnName = "manufacturer_id")
   private Manufacturer manufacturer;

   @Column(name = "functional_entity_name", nullable = false)
   private String name = "";

   @Column(name = "functional_entity_number", nullable = false)
   private String number = "";

   @Column(name = "functional_entity_description", nullable = false)
   private String description = "";

   @ManyToOne(fetch = FetchType.EAGER, optional = false)
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
    * @param manufacturer - the owning manufacturer.
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
    * Set the id.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return The manufacturer to whom the functional entity belongs.
    */
   public Manufacturer getManufacturer()
   {
      return manufacturer;
   }

   /**
    * Set the manufacturer.
    *
    * @param manufacturer - the manufacturer to set.
    */
   public void setManufacturer(Manufacturer manufacturer)
   {
      this.manufacturer = manufacturer;
   }

   /**
    * @return the name.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the number of the functional entity. This is a string, and is usually a
    * short 2-5 characters string.
    *
    * @param number - the number to set.
    */
   public void setNumber(String number)
   {
      this.number = number;
   }

   /**
    * Get the number of the functional entity. This is a string, and is usually a
    * short 2-5 characters string.
    *
    * @return The number of the functional entity.
    */
   public String getNumber()
   {
      return number;
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
      return id;
   }

   /**
    * Compare two objects.
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof FunctionalEntity))
         return false;

      final FunctionalEntity oo = (FunctionalEntity) o;
      return id == oo.id && (manufacturer == null ? oo.manufacturer == null : manufacturer.equals(oo.manufacturer));
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
