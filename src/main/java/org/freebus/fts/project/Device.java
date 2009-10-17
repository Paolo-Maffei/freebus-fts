package org.freebus.fts.project;

import java.util.Vector;

import org.freebus.fts.products.Product;

/**
 * An EIB device.
 */
public class Device
{
   private String name = "Unnamed";
   private Product deviceType = Product.NONE;
   
   private PhysicalAddress physicalAddr = PhysicalAddress.NULL;
   private final Vector<GroupAddress> groupAddrs = new Vector<GroupAddress>();

   /**
    * Create a device with the given name.
    */
   public Device(String name)
   {
      this.setName(name);
   }

   /**
    * Create a device with the given name and physical address.
    */
   public Device(String name, PhysicalAddress physicalAddr)
   {
      this.setName(name);
      this.physicalAddr = physicalAddr;
   }

   /**
    * Set the name of the device.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the name of the device.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the physical address of the device.
    */
   public void setPhysicalAddr(PhysicalAddress physicalAddr)
   {
      this.physicalAddr = physicalAddr;
   }
   
   /**
    * @return the physical address of the device.
    */
   public PhysicalAddress getPhysicalAddr()
   {
      return physicalAddr;
   }
}
