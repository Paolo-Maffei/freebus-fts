package org.freebus.fts.products;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.interfaces.Named;

/**
 * Catalog entries name the variations of a virtual device, as it can be bought
 * from a catalog or web-shop. Catalog entries of the same virtual device
 * usually differ in things like housing color or maximum switching power.
 */
@Entity
@Table(name = "catalog_entry")
public class CatalogEntry implements Named
{
   @Id
   @TableGenerator(name = "CatalogEntry", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "CatalogEntry")
   @Column(name = "catalog_entry_id", nullable = false)
   private int id;

   @Column(name = "entry_name", nullable = false)
   private String name;

   @ManyToOne(optional = false, fetch = FetchType.EAGER)
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

   @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "catalogEntry")
   private ProductDescription description;

   /**
    * Create an empty catalog-entry object.
    */
   public CatalogEntry()
   {
   }

   /**
    * Create an empty catalog-entry object with an id.
    * 
    * @param id - the database ID of the object.
    */
   public CatalogEntry(int id)
   {
      this.id = id;
   }

   /**
    * Create a catalog-entry object.
    * 
    * @param id - the database ID of the object.
    * @param name - the name of the object.
    * @param manufacturer - the manufacturer.
    * @param product - the product.
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
    *
    * @param name - the name of the object.
    * @param manufacturer - the manufacturer.
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
    * 
    * @param id - the ID to set.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the name of the catalog-entry.
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the catalog-entry.
    * 
    * @param name - the name to set.
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
    * Set the manufacturer.
    * 
    * @param manufacturer - the manufacturer to set.
    */
   public void setManufacturer(Manufacturer manufacturer)
   {
      this.manufacturer = manufacturer;
   }

   /**
    * @return the product.
    */
   public Product getProduct()
   {
      return product;
   }

   /**
    * Set the hardware product.
    * 
    * @param product - the product to set.
    */
   public void setProduct(Product product)
   {
      this.product = product;
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
    * 
    * @param widthModules - the width to set.
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
    * 
    * @param widthMM - the width to set.
    */
   public void setWidthMM(int widthMM)
   {
      this.widthMM = widthMM;
   }

   /**
    * Test if the device can be mounted on DIN rails.
    * 
    * @return True if the device is DIN mountable.
    */
   public boolean getDIN()
   {
      return din;
   }

   /**
    * Set if the device can be mounted on DIN rails.
    * 
    * @param din - true if the device is DIN mountable.
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
    * 
    * @param orderNumber - the order number to set.
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
    * 
    * @param color - the color of the device.
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
    * 
    * @param series - the series to set.
    */
   public void setSeries(String series)
   {
      this.series = series;
   }

   /**
    * Set the description.
    * 
    * @param description - the description to set.
    */
   public void setDescription(ProductDescription description)
   {
      this.description = description;
   }

   /**
    * @return The description.
    */
   public ProductDescription getDescription()
   {
      return description;
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
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return name == null ? "#" + id : name;
   }
}
