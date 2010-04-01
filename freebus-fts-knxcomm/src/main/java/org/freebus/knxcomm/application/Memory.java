package org.freebus.knxcomm.application;

/**
 * Abstract base class for device memory access.
 */
public abstract class Memory implements Application
{
   private int address;

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
   public abstract int getCount();

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return getType().name() + String.format(" address 0x%04x, %d bytes", address, getCount());
   }
}
