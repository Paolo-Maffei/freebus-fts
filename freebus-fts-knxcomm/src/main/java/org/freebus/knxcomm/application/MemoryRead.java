package org.freebus.knxcomm.application;

import org.freebus.knxcomm.applicationData.MemoryAddress;
import org.freebus.knxcomm.applicationData.MemoryAddressTypes;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Read a block of bytes from the memory of a remote device.
 */
public class MemoryRead extends Memory
{

   private int usercount = 0;
   private int offset = 0;

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

      super.setCount(count);
   }

   /**
    * Create a memory read object.
    *
    * @param memoryAddress a MemoryAddress Class
    */
   public MemoryRead(MemoryAddress memoryAddress)
   {
      super(memoryAddress.getAdress());
      super.setCount(memoryAddress.getLength());
   }

   public MemoryRead(MemoryAddressTypes memoryAddressTypes, int offset)
   {
      super(memoryAddressTypes);
      this.offset = offset;

   }

   public MemoryRead(MemoryAddressTypes memoryAddressTypes, int offset, int usercount)
   {
      super(memoryAddressTypes);
      this.offset = offset;
      this.usercount = usercount;

   }

   public MemoryRead(MemoryAddressTypes memoryAddressTypes)
   {
      super(memoryAddressTypes);
   }

   /**
    * Set the number of bytes to read from the memory.
    *
    * @param count - the number of bytes to read. Range 1-63.
    *
    * @throws IllegalArgumentException if count is not in the range 0..63
    */
   @Override
   public void setCount(int count)
   {
      if (count < 0 || count > 63)
         throw new IllegalArgumentException("count must be 0..63");

      super.setCount(count);
      super.setApciValue(count);
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
      super.setCount(rawData[start++] & 0x3f);
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
      int count;
      if (usercount == 0)
      {
         count = (super.getCount() - offset);
      }
      else
      {
         count = usercount;
      }
      rawData[pos++] = (appType.getApci() & 255) | (count & appType.getDataMask());

      final int address = getAddress() + offset;
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
      return (getAddress() << 8) | super.getCount();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getApciValue()
   {
      if (usercount == 0)
      {
         count = (super.getCount() - offset);
      }
      else
      {
         count = usercount;
      }
      return count;
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
      return getAddress() == oo.getAddress() && super.getCount() == oo.getCount();
   }

   public void setoffset(int offset)
   {
      this.offset = offset;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ApplicationType getApplicationResponses()
   {
      return ApplicationType.Memory_Response;
      
   }

}
