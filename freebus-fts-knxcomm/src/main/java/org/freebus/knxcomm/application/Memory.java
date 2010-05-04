package org.freebus.knxcomm.application;

import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptorProperties;
import org.freebus.knxcomm.applicationData.MemoryAddress;
import org.freebus.knxcomm.applicationData.MemoryAddressTypes;

/**
 * Abstract base class for device memory access.
 */
public abstract class Memory extends AbstractApplication
{
   private int address;
   protected int count;
   private MemoryAddressTypes memoryAddressTypes;
   private DeviceDescriptorProperties deviceDescriptorProperties;

   /**
    * Create a memory object.
    *
    * @param address - the 16 bit memory address.
    */
   protected Memory(int address)
   {
      this.address = address;
   }

   /**
    * Create a memory object.
    *
    * @param address - the 16 bit memory address.
    */
   protected Memory(MemoryAddressTypes memoryAddressTypes)
   {
      this.memoryAddressTypes = memoryAddressTypes;
   }

   /**
    * @return the 16-bit memory address
    */
   public int getAddress()
   {
      return address;
   }

   /**
    * Set the 16-bit memory address.
    *
    * @param address the address to set
    */
   public void setAddress(int address)
   {
      this.address = address;
   }

   /**
    * @return the number of bytes to read from the memory / write to the memory.
    */
   public int getCount()
   {
      return count;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return getType().name() + String.format(" address 0x%04x, %d bytes", address, getCount());
   }

   /**
    * {@inheritDoc}
    */
   public void setDeviceDescriptorProperties(DeviceDescriptorProperties deviceDescriptorProperties)
   {
      this.deviceDescriptorProperties = deviceDescriptorProperties;
      updateAdresseFromAddressType();

   }

   /**
    * @param count the number of bytes to read from the memory / write to the
    *           memory.
    */
   public void setCount(int count)
   {
      this.count = count;
   }

   private void updateAdresseFromAddressType()
   {
      MemoryAddress memoryAddress = deviceDescriptorProperties.getMemoryAddressMapper().getMemoryAddress(
            memoryAddressTypes);
      address = memoryAddress.getAdress();
      if (count == 0)
      {
         count = memoryAddress.getLength();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isDeviceDescriptorRequired()
   {
      // TODO Auto-generated method stub
      return true;
   }
}
