package org.freebus.fts.products;

import java.io.Serializable;

import javax.persistence.CascadeType;
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
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "GenCatalogEntryId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "catalog_entry_id", nullable = false)
   private int id;

   @Column(name = "entry_name", nullable = false)
   private String name;

   @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "manufacturer_id", nullable = false)
   private Manufacturer manufacturer;

   @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = Product.class)
   @JoinColumn(name = "product_id", referencedColumnName = "product_id")
   private Product product;

   @Column(name = "entry_width_in_modules")
   private int widthModules;

   @Column(name = "entry_width_in_millimeters")
   private int widthMM;

   @Column(name = "din_flag", nullable = false)
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
    * Create an empty catalog-entry object with an id.
    */
   public CatalogEntry(int id)
   {
      this.id = id;
   }

   /**
    * Create a catalog-entry object.
    */
   public CatalogEntry(int id, String name, Manufacturer manufacturer, Product product)
   {
      this.id = id;
      this.name = name;
      this.manufacturer = manufacturer;
      this.product = product;
   }

   /**
    * Create a catalog-entry object.
    */
   public CatalogEntry(String name, Manufacturer manufacturer)
   {
      this(0, name, manufacturer, null);
   }

   /**
    * @return the id of the catalog-entry.
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the id of the catalog-entry.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the name of the catalog-entry.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the catalog-entry.
    */
   public void setName(String name)
   {
      this.name = name;
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

      if (!(o instanceof CatalogEntry))
         return false;

      final CatalogEntry oo = (CatalogEntry) o;

      if (id != oo.id || !name.equals(oo.name))
         return false;

      if ((manufacturer == null && oo.manufacturer != null) ||
          (manufacturer != null && !manufacturer.equals(oo.manufacturer)))
         return false;

      if ((product == null && oo.product != null) ||
          (product != null && !product.equals(oo.product)))
         return false;

      return true;
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
