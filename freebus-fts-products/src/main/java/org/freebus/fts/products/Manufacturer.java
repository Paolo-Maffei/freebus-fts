package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * A manufacturer.
 */
@Entity
@Table(name = "manufacturer")
public class Manufacturer
{
   public final static Manufacturer NOBODY = new Manufacturer(0, "Nobody");
   
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "GenManufacturerId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "manufacturer_id", nullable = false)
   private int id;

   @Column(name = "manufacturer_name", length = 50, nullable = false)
   private String name;

   /**
    * Create an empty manufacturer.
    */
   public Manufacturer()
   {
   }

   /**
    * Create a manufacturer.
    */
   public Manufacturer(int id, String name)
   {
      this.id = id;
      this.name = name;
   }

   /**
    * @return the id of the manufacturer.
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the id of the manufacturer.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the name of the manufacturer.
    */
   public String getName()
   {
      if (name == null) return "";
      return name;
   }
   
   /**
    * Set the name of the manufacturer.
    */
   public void setName(String name)
   {
      this.name = name;
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
    * Compare two manufacturer by id.
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o == this) return true;
      if (!(o instanceof Manufacturer)) return false;
      final Manufacturer oo = (Manufacturer) o;
      return id == oo.id;
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
