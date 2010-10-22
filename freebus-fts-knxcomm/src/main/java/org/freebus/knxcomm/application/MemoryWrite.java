package org.freebus.knxcomm.application;

import org.freebus.knxcomm.application.memory.MemoryLocation;

/**
 * Write up to 63 bytes into device memory. Depending on the type of
 * the (your) bus connection, only 12 bytes can be transfered.
 */
public class MemoryWrite extends MemoryData
{
   /**
    * Create a memory write object with address 0 and no data.
    */
   public MemoryWrite()
   {
      this(0, null);
   }

   /**
    * Create a memory write object.
    * 
    * @param address - the 16 bit memory address.
    * @param data - the data. Up to 63 bytes.
    * 
    * @throws IllegalArgumentException if the supplied memory data has more than
    *            63 bytes.
    */
   public MemoryWrite(int address, byte[] data)
   {
      super(address, data);
   }

   /**
    * Create a memory write object.
    * 
    * @param location - the memory location.
    * @param data - the data. Up to 63 bytes.
    * 
    * @throws IllegalArgumentException if the supplied memory data has more than
    *            63 bytes.
    */
   public MemoryWrite(MemoryLocation location, byte[] data)
   {
      super(location, data);
   }

   /**
    * @return The type of the application: {@link ApplicationType#Memory_Write}.
    */
   @Override
   public ApplicationType getType()
   {
      return ApplicationType.Memory_Write;
   }
}
