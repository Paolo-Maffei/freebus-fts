package org.freebus.fts.products;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Catalog entries name the variations of a virtual device, as it can be bought
 * from a catalog or web-shop. Catalog entries of the same virtual device
 * usually differ in things like housing color or maximum switching power.
 */
@Entity
@Table(name = "catalog_entry")
public class CatalogEntry implements Serializable
{
   private static final long serialVersionUID = 4022059156749728267L;

   @Id
   @Column(name = "catalog_entry_id", columnDefinition = "INT", unique = true, nullable = false)
   private int id;

   @Column(name = "entry_name", nullable = false)
   private String name;

   @Column(name = "manufacturer_id", columnDefinition = "INT")
   private int manufacturerId;

   @Column(name = "product_id", columnDefinition = "INT")
   private int productId;

   @Column(name = "entry_width_in_modules", columnDefinition = "INT")
   private int widthModules;

   @Column(name = "entry_width_in_millimeters", columnDefinition = "INT")
   private int widthMM;

   @Column(name = "din_flag", columnDefinition = "BOOLEAN", nullable = false)
   private boolean din;

   @Column(name = "order_number")
   private String orderNumber;

   @Column(name = "entry_colour")
   private String color;

   @Column(name = "series")
   private String series;

   /**
    * Create an empty catalog-entry object.
    */
   public CatalogEntry()
   {      
   }

   /**
    * Create a catalog-entry object.
    */
   public CatalogEntry(int id, String name, int manufacturerId, int productId)
   {
      this.id = id;
      this.name = name;
      this.manufacturerId = manufacturerId;
      this.productId = productId;
   }

   /**
    * @return the id of the catalog-entry.
    */
   public int getId()
   {
      return id;
   }

   /**
    * @return the name of the catalog-entry.
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the manufacturer id.
    */
   public int getManufacturerId()
   {
      return manufacturerId;
   }

   /**
    * @return the product id.
    */
   public int getProductId()
   {
      return productId;
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
      if (o == this) return true;
      if (!(o instanceof CatalogEntry)) return false;
      final CatalogEntry oo = (CatalogEntry) o;
      return id == oo.id && manufacturerId == oo.manufacturerId && productId == oo.productId;
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