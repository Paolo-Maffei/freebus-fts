package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * A Product contains the hardware details about a device,
 * taken from a .vdx or .xml specification file (HW_PRODUCTS section).
 * This is not a device that gets installed, it is the technical description
 * of the hardware.
 */
@Entity
@Table(name = "hw_product", uniqueConstraints = @UniqueConstraint(columnNames = "product_id"))
public class Product
{
   public final static Product NONE = new Product(0, "NONE", 0);

   @Id
   @Column(name = "product_id", columnDefinition = "INT", unique = true, nullable = false)
   private int id;

   @Column(name = "product_name", length = 50, nullable = false)
   private String name;

   @Column(name = "manufacturer_id", columnDefinition = "INT")
   private int manufacturerId; 

   @Column(name = "product_serial_number", length = 30)
   private String serial;

   @Column(name = "bus_current", columnDefinition = "SMALLINT")
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
   public Product(int id, String name, int manufacturerId)
   {
      this.id = id;
      this.name = name;
      this.manufacturerId = manufacturerId;
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
    * @return the manufacturer id.
    */
   public int getManufacturer()
   {
      return manufacturerId;
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
