package org.freebus.knxcomm.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.MemoryConnectionInterface;
import org.freebus.knxcomm.application.memory.MemoryLocation;

/**
 * A simulated memory connection that works with internal byte buffers. It's
 * main use is probably for testing.
 */
public final class SimulatedMemoryConnection implements MemoryConnectionInterface
{
   private final byte[] mem;
   private final int startAddress;

   /**
    * Create a simulated memory connection.
    * 
    * @param startAddress - the starting address of the memory range
    * @param size - the size of the memory range
    */
   public SimulatedMemoryConnection(int startAddress, int size)
   {
      this.startAddress = startAddress;
      mem = new byte[size];
   }

   /**
    * @return The memory area, with the start address at index 0.
    */
   public byte[] getMem()
   {
      return mem;
   }

   /**
    * Clear the memory area with zeroes.
    */
   public void clear()
   {
      Arrays.fill(mem, (byte)0);
   }

   /**
    * @return The start address of the memory area.
    */
   public int getStartAddress()
   {
      return startAddress;
   }

   @Override
   public DataConnection getConnection()
   {
      return null;
   }

   @Override
   public byte[] read(int address, int count) throws IOException, TimeoutException
   {
      if (address < startAddress || address + count >= startAddress + mem.length)
         throw new IllegalArgumentException("out of range");

      return Arrays.copyOfRange(mem, address - startAddress, count);
   }

   @Override
   public byte[] read(MemoryLocation location) throws IOException, TimeoutException
   {
      throw new IllegalAccessError("not implemented");
   }

   @Override
   public byte[] read(MemoryLocation location, int offset, int count) throws IOException, TimeoutException
   {
      throw new IllegalAccessError("not implemented");
   }

   @Override
   public void write(int address, byte[] data) throws IOException, TimeoutException
   {
      if (address < startAddress || address + data.length >= startAddress + mem.length)
      {
         throw new IllegalArgumentException("[" + address + ".." + (address + data.length)
               + "] is outside the memory range [" + startAddress + ".." + (startAddress + mem.length) + "]");
      }

      final int offs = address - startAddress;
      for (int i = 0; i < data.length; ++i)
         mem[offs + i] = data[i];
   }

   @Override
   public void write(MemoryLocation location, byte[] data) throws IOException, TimeoutException
   {
      throw new IllegalAccessError("not implemented");
   }
}
