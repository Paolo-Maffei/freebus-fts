package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.freebus.fts.interfaces.Named;

/**
 * A manufacturer.
 */
@Entity
@Table(name = "manufacturer")
public class Manufacturer implements Named
{
   /**
    * Unset / invalid manufacturer id.
    */
   public final static int INVALID_ID = -1;

   @Id
   @Column(name = "manufacturer_id", nullable = false)
   private int id = INVALID_ID;

   @Column(name = "manufacturer_name", length = 50, nullable = false)
   private String name = "";

   /**
    * Create a manufacturer with an empty name and id set to {@link #INVALID_ID}
    * .
    */
   public Manufacturer()
   {
   }

   /**
    * Create a manufacturer.
    * 
    * @param id - the id of the manufacturer.
    * @param name - the name of the manufacturer.
    */
   public Manufacturer(int id, String name)
   {
      this.id = id;
      this.name = name;
   }

   /**
    * Get the id of the manufacturer. {@link #INVALID_ID} is returned if the
    * manufacturer id is not set.
    * 
    * @return the id of the manufacturer, or {@link #INVALID_ID} if the id is
    *         unset.
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the id of the manufacturer.
    * 
    * @param id - the id to set.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the name of the manufacturer.
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the manufacturer.
    * 
    * @param name - the name to set.
    */
   public void setName(String name)
   {
      this.name = name == null ? "" : name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof Manufacturer))
         return false;

      final Manufacturer oo = (Manufacturer) o;
      return id == oo.id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return getClass().getSimpleName() + " #" + id + " \"" + name + "\"";
   }
}
