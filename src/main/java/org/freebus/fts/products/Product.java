package org.freebus.fts.products;

/**
 * A Product contains the hardware details about a device,
 * taken from a .vdx or .xml specification file (HW_PRODUCTS section).
 * This is not a device that gets installed, it is the technical description
 * of the hardware.
 */
public class Product
{
   public final static Product NONE = new Product("NONE", Manufacturer.NOBODY);

   private final String name;
   private final Manufacturer manufacturer; 

   private String serial, symbolName;
   private int busCurrent = 0;

   /**
    * Create a product.
    */
   public Product(String name, Manufacturer manufacturer)
   {
      this.name = name;
      this.manufacturer = manufacturer;
   }

   /**
    * @return the name of the product.
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the manufacturer of the product.
    */
   public Manufacturer getManufacturer()
   {
      return manufacturer;
   }

   /**
    * Set the serial-number that the manufacturer assigned.
    */
   public void setSerial(String serial)
   {
      this.serial = serial;
   }

   /**
    * @return the product-id that the manufacturer assigned.
    */
   public String getSerial()
   {
      return serial;
   }

   /**
    * @return the name of the symbol (icon)
    */
   public String getSymbolName()
   {
      return symbolName;
   }

   /**
    * Set the name of the symbol (icon)
    */
   public void setSymbolName(String symbolName)
   {
      this.symbolName = symbolName;
   }
   
   /**
    * Set the current that the product requires, in milli-ampere.
    */
   public void setBusCurrent(int busCurrent)
   {
      this.busCurrent = busCurrent;
   }

   /**
    * Return the current that the product requires, in milli-ampere.
    */
   public int getBusCurrent()
   {
      return busCurrent;
   }
}
