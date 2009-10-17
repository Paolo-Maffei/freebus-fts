package org.freebus.fts.products;

import java.util.Vector;

/**
 * A device as it can be bought from a catalog or web-shop.
 */
public class CatalogEntry
{
   private final String name;
   private final Manufacturer manufacturer;
   private final Product product;
   private final Vector<VirtualDevice> virtualDevices = new Vector<VirtualDevice>();
   private int widthModules = 0;
   private int widthMM = 0;
   private boolean din = false;
   private String orderNumber = "";
   private String color = "";
   private String series = ""; 

   /**
    * Create a catalog-entry object.
    */
   public CatalogEntry(String name, Manufacturer manufacturer, Product product)
   {
      this.name = name;
      this.manufacturer = manufacturer;
      this.product = product;
   }

   /**
    * @return the name of the catalog-entry.
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the manufacturer.
    */
   public Manufacturer getManufacturer()
   {
      return manufacturer;
   }

   /**
    * @return the product.
    */
   public Product getProduct()
   {
      return product;
   }

   /**
    * @return the number of virtual devices that this catalog-entry contains.
    */
   public int getVirtualDevicesCount()
   {
      return virtualDevices.size();
   }

   /**
    * @return the idx-th virtual device
    */
   public VirtualDevice getVirtualDevice(int idx)
   {
      return virtualDevices.get(idx);
   }
   
   /**
    * Add a virtual device
    */
   public void addVirtualDevice(VirtualDevice virtualDevice)
   {
      virtualDevices.add(virtualDevice);
   }

   /**
    * @return the with in module-units.
    */
   public int getWidthModules()
   {
      return widthModules;
   }

   /**
    * Set the with in module-units.
    */
   public void setWidthModules(int widthModules)
   {
      this.widthModules = widthModules;
   }

   /**
    * @return the width in millimeters.
    */
   public int getWidthMM()
   {
      return widthMM;
   }

   /**
    * Set the width in millimeters.
    */
   public void setWidthMM(int widthMM)
   {
      this.widthMM = widthMM;
   }

   /**
    * Test if the device can be mounted on DIN rails.
    */
   public boolean getDIN()
   {
      return din;
   }

   /**
    * Set if the device can be mounted on DIN rails.
    */
   public void setDIN(boolean din)
   {
      this.din = din;
   }

   /**
    * @return the order-number.
    */
   public String getOrderNumber()
   {
      return orderNumber;
   }

   /**
    * Set the order-number.
    */
   public void setOrderNumber(String orderNumber)
   {
      this.orderNumber = orderNumber;
   }

   /**
    * @return the color of the device.
    */
   public String getColor()
   {
      return color;
   }

   /**
    * Set the color of the device.
    */
   public void setColor(String color)
   {
      this.color = color;
   }

   /**
    * @return the series.
    */
   public String getSeries()
   {
      return series;
   }

   /**
    * Set the series.
    */
   public void setSeries(String series)
   {
      this.series = series;
   }

   /**
    * Returns a hash-code for the object.
    */
   @Override
   public int hashCode()
   {
      return name.hashCode();
   }
   
   /**
    * Compare two objects.
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o==this) return true;
      if (!(o instanceof CatalogEntry)) return false;
      final CatalogEntry oo = (CatalogEntry)o;
      return product==oo.product && manufacturer==oo.manufacturer && name==oo.name;
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
