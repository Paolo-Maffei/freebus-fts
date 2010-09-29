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
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * A Product contains the hardware details about a device, taken from a .VD_
 * specification file (HW_PRODUCTS section). This is not a device that gets
 * installed, it is the technical description of the hardware.
 */
@Entity
@Table(name = "hw_product")
public class Product
{
   public final static Product NONE = new Product(0, "NONE", null);

   @Id
   @TableGenerator(name = "Product", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "Product")
   @Column(name = "product_id", nullable = false)
   private int id;

   @Column(name = "product_name", length = 50, nullable = false)
   private String name;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "manufacturer_id", nullable = false, referencedColumnName = "manufacturer_id")
   private Manufacturer manufacturer;

   @Column(name = "product_version_number", nullable = false)
   private int version;

   @Column(name = "product_serial_number", length = 30)
   private String serial;

   @Column(name = "bus_current")
   private int busCurrent;

   @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
   @JoinColumn(name = "bcu_type_number", nullable = true, referencedColumnName = "bcu_type_number")
   private BcuType bcuType;

   /**
    * Create an empty product object.
    */
   public Product()
   {
   }

   /**
    * Create a product.
    */
   public Product(int id, String name, Manufacturer manufacturer)
   {
      this.id = id;
      this.name = name;
      this.manufacturer = manufacturer;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof Product))
         return false;

      final Product oo = (Product) o;
      return id == oo.id && name.equals(oo.name) && manufacturer.equals(oo.manufacturer);
   }

   /**
    * Return the current that the product requires, in milli-ampere.
    */
   public int getBusCurrent()
   {
      return busCurrent;
   }

   /**
    * @return the product id.
    */
   public int getId()
   {
      return id;
   }

   /**
    * @return the manufacturer.
    */
   public Manufacturer getManufacturer()
   {
      return manufacturer;
   }

   /**
    * @return the name of the product.
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the product-id that the manufacturer assigned.
    */
   public String getSerial()
   {
      return serial;
   }

   public int getVersion()
   {
      return version;
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
    * Set the current that the product requires, in milli-ampere.
    */
   public void setBusCurrent(int busCurrent)
   {
      this.busCurrent = busCurrent;
   }

   /**
    * Set the product id.
    */
   public void setId(int id)
   {
      this.id = id;
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
    * Set the serial-number that the manufacturer assigned.
    */
   public void setSerial(String serial)
   {
      this.serial = serial;
   }

   public void setVersion(int version)
   {
      this.version = version;
   }

   /**
    * @param bcuType the bcuType to set
    */
   public void setBcuType(BcuType bcuType)
   {
      this.bcuType = bcuType;
   }

   /**
    * @return the bcuType
    */
   public BcuType getBcuType()
   {
      return bcuType;
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
