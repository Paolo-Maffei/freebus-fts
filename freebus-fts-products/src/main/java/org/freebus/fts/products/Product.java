package org.freebus.fts.products;

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
 * A Product contains the hardware details about a device,
 * taken from a .vdx or .xml specification file (HW_PRODUCTS section).
 * This is not a device that gets installed, it is the technical description
 * of the hardware.
 */
@Entity
@Table(name = "hw_product")
public class Product
{
   public final static Product NONE = new Product(0, "NONE", null);

   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "GenProductId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "product_id", nullable = false)
   private int id;

   @Column(name = "product_name", length = 50, nullable = false)
   private String name;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "manufacturer_id", nullable = false, referencedColumnName = "manufacturer_id")
   private Manufacturer manufacturer;

   @Column(name = "product_serial_number", length = 30)
   private String serial;

   @Column(name = "bus_current")
   private int busCurrent;

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
    * Set the product id.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the product id.
    */
   public int getId()
   {
      return id;
   }

   /**
    * @return the name of the product.
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
