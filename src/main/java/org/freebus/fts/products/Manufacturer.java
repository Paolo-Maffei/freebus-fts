package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * A manufacturer.
 */
@Entity
@Table(name = "manufacturer", uniqueConstraints = @UniqueConstraint(columnNames = "manufacturer_id"))
public class Manufacturer
{
   public final static Manufacturer NOBODY = new Manufacturer(0, "Nobody");
   
   @Id
   @Column(name = "manufacturer_id", columnDefinition = "INT", unique = true, nullable = false)
   private int id;

   @Column(name = "manufacturer_name", length = 50, nullable = false)
   private String name;

   /**
    * Create a manufacturer with default values.
    */
   public Manufacturer()
   {
      id = 0;
      name = "Unnamed";
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
    * @return the name of the manufacturer.
    */
   public String getName()
   {
      return name;
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
    * Compare two manufacturers by id.
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
      return name + " [" + Integer.toString(id) + "]";
   }
}
