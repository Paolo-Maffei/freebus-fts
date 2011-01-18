package org.freebus.fts.client.pages.productsbrowser;

import javax.swing.JComboBox;
import javax.swing.JList;

import org.freebus.fts.products.Manufacturer;

/**
 * An item that holds a {@link Manufacturer}, returns the manufacturer's
 * name in {@link #toString()}, and is comparable by name - and can therefore
 * be sorted.
 * 
 * This is a useful helper class for creating lists of manufacturers,
 * like for {@link JList} or {@link JComboBox}.
 */
public class ManufacturerItem implements Comparable<ManufacturerItem>
{
   private Manufacturer manufacturer;

   /**
    * Create an empty object.
    */
   public ManufacturerItem()
   {
      this(null);
   }
   
   /**
    * Create an object with a manufacturer.
    */
   public ManufacturerItem(Manufacturer manufacturer)
   {
      this.manufacturer = manufacturer;
   }

   /**
    * @return the manufacturer.
    */
   public Manufacturer getManufacturer()
   {
      return manufacturer;
   }

   /**
    * Set the manufacturer.
    */
   public void setManufacturer(Manufacturer manufacturer)
   {
      this.manufacturer = manufacturer;
   }

   /**
    * Compare two items by comparing their manufacturer's names.
    */
   @Override
   public int compareTo(ManufacturerItem o)
   {
      if (manufacturer == o.manufacturer) return 0;
      if (manufacturer == null) return -1;
      return manufacturer.getName().compareTo(o.manufacturer.getName());
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this) return true;
      if (!(o instanceof ManufacturerItem)) return false;
      final ManufacturerItem oo = (ManufacturerItem) o;
      if (manufacturer == null)
      {
         return oo.manufacturer == null;
      }
      return manufacturer.getName().equals(oo.manufacturer.getName());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      if (manufacturer == null) return 0;
      return manufacturer.hashCode();
   }

   /**
    * @return the name of the manufacturer.
    */
   @Override
   public String toString()
   {
      if (manufacturer == null) return "";
      return manufacturer.getName();
   }
}
