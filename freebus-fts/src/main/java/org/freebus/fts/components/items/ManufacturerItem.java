package org.freebus.fts.components.items;

import org.freebus.fts.products.Manufacturer;

/**
 * An item that holds a {@link Manufacturer}, returns the manufacturer's
 * name in {@link #toString()}, and is comparable by name - and can therefore
 * be sorted.
 * 
 * This is a useful helper class for creating lists of manufacturers,
 * like for {@link JList} or {@link JComboBox}.
 */
public class ManufacturerItem implements Comparable<Manufacturer>
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
   public int compareTo(Manufacturer o)
   {
      if (manufacturer == o) return 0;
      if (manufacturer == null) return -1;
      return manufacturer.getName().compareTo(o.getName());
   }

   /**
    * @return the name of the manufacturer.
    */
   @Override
   public String toString()
   {
      if (manufacturer == null) return null;
      return manufacturer.getName();
   }
}
