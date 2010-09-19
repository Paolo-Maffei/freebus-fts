package org.freebus.knxcomm.application;

import java.io.DataInput;
import java.io.IOException;

import org.freebus.knxcomm.application.memory.MemoryLocation;

/**
 * Read a block of bytes from the memory of a remote device.
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
    * @param address - the memory address.
    * @param count - the number of bytes to read/write, in the range 0..63
    * 
    * @throws IllegalArgumentException if count is not in the range 0..63
    */
   public MemoryRead(int address, int count)
   {
      super(address);
      setCount(count);
   }

   /**
    * Create a memory read object.
    * 
    * @param location - the memory location.
    * @param offset - the offset relative to the location.
    * @param count - the number of bytes to read/write, in the range 0..63
    * 
    * @throws IllegalArgumentException if count is not in the range 0..63
    */
   public MemoryRead(MemoryLocation location, int offset, int count)
   {
      super(location, offset);
      setCount(count);
   }

   /**
    * Create a memory read object that reads the entire memory range of the
    * location.
    * 
    * @param location - the memory location.
    */
   public MemoryRead(MemoryLocation location)
   {
      super(location, 0);
      count = -1;

      // TODO handle this correct
      throw new RuntimeException("sorry not implemented");
   }

   /**
    * Set the number of bytes to read from the memory.
    * 
    * @param count - the number of bytes to read. Range 1-63.
    * 
    * @throws IllegalArgumentException if count is not in the range 0..63
    */
   public void setCount(int count)
   {
      if (count < 0 || count > 63)
         throw new IllegalArgumentException("count must be in the range 0..63");

      this.count = count;
      super.setApciValue(count);
   }

   /**
    * @return The number of bytes to read from the memory.
    */
   public int getCount()
   {
      return count;
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
   public void readData(DataInput in, int length) throws IOException
   {
      count = getApciValue();
      setAddress(in.readUnsignedShort());
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
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof MemoryRead))
         return false;

      final MemoryRead oo = (MemoryRead) o;
      return getAddress() == oo.getAddress() && count == oo.count;
   }
}
