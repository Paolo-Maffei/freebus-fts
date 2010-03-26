package org.freebus.knxcomm.application;

import org.freebus.knxcomm.aplicationData.MemoryAddress;
import org.freebus.knxcomm.telegram.InvalidDataException;


/**
 * Read the memory of a remote device.
 */
public class MemoryRead extends Memory
{
  private int count;

  /**
    * Create a memory read object with address and count 0.
    */
   public MemoryRead()
   {
      this(0, 0);
   }

   /**
    * Create a memory read object.
    *
    * @param address - the 16 bit memory address.
    * @param count - the number of bytes to read/write, in the range 0..63
    *
    * @throws IllegalArgumentException if count is not in the range 0..63
    */
   public MemoryRead(int address, int count)
   {
      super(address);

      if (count < 0 || count > 63)
         throw new IllegalArgumentException("count must be 0..63");

      this.count = count;
   }
   
   /**
    * Create a memory read object.
    *
    * @param memoryAddress a MemoryAddress Class
    */
   public MemoryRead(MemoryAddress memoryAddress )
   {
      super(memoryAddress.getAdress());
     this.count = memoryAddress.getLength();
   }

   /**
    * @return the number of bytes to read.
    */
   @Override
   public int getCount()
   {
      return count;
   }

   /**
    * Set the number of bytes to read.
    *
    * @param count - the number of bytes to read. Range 1-63.
    *
    * @throws IllegalArgumentException if count is not in the range 0..63
    */
   public void setCount(int count)
   {
      if (count < 0 || count > 63)
         throw new IllegalArgumentException("count must be 0..63");

      this.count = count;
   }

   /**
    * @return The type of the application: {@link ApplicationType#Memory_Read}.
    */
   @Override
   public ApplicationType getType()
   {
      return ApplicationType.Memory_Read;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void fromRawData(int[] rawData, int start, int length) throws InvalidDataException
   {
      count = rawData[start++] & 0x3f;
      setAddress((rawData[start++] << 8) | rawData[start++]);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      final ApplicationType appType = getType();
      int pos = start;

      rawData[pos++] = (appType.getApci() & 255) | (count & appType.getDataMask());

      final int address = getAddress();
      rawData[pos++] = (address >> 8) & 255;
      rawData[pos++] = address & 255;

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return (getAddress() << 8) | count;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof MemoryRead))
         return false;

      final MemoryRead oo = (MemoryRead) o;
      return getAddress() == oo.getAddress() && count == oo.count;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return super.toString() + " " + count + " bytes";
   }
}
