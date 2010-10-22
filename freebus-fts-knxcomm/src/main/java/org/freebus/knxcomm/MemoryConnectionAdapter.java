package org.freebus.knxcomm;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.freebus.knxcomm.application.MemoryRead;
import org.freebus.knxcomm.application.MemoryResponse;
import org.freebus.knxcomm.application.MemoryWrite;
import org.freebus.knxcomm.application.memory.MemoryLocation;

/**
 * An adapter for a {@link DataConnection} that handles reading and writing of
 * memory of a device (BCU) on the KNX bus.
 */
public class MemoryConnectionAdapter
{
   private final DataConnection connection;

   /**
    * Create a memory adapter.
    * 
    * @param connection - the data connection to use.
    * @throws NullPointerException if the connection is null.
    */
   public MemoryConnectionAdapter(DataConnection connection)
   {
      if (connection == null)
         throw new NullPointerException("connection is null");

      this.connection = connection;
   }

   /**
    * @return The data connection.
    */
   public DataConnection getConnection()
   {
      return connection;
   }

   /**
    * Read bytes from an absolute memory address.
    * 
    * @param address - the memory address.
    * @param count - the number of bytes to read/write, in the range 0..63
    * 
    * @throws TimeoutException if there is no reply from the remote device.
    * @throws IOException if there is a communication error.
    * @throws IllegalArgumentException if count is not in the range 0..63
    */
   public byte[] read(int address, int count) throws IOException, TimeoutException
   {
      return read(new MemoryRead(address, count));
   }

   /**
    * Read bytes from the entire memory range of the location.
    * 
    * @param location - the memory location.
    * 
    * @throws TimeoutException if there is no reply from the remote device.
    * @throws IOException if there is a communication error.
    */
   public byte[] read(MemoryLocation location) throws IOException, TimeoutException
   {
      return read(new MemoryRead(location));
   }

   /**
    * Read count bytes from the count bytes from a memory address which is
    * specified by a location and an offset.
    * 
    * @param location - the memory location.
    * @param offset - the offset relative to the location.
    * @param count - the number of bytes to read/write, in the range 1..63
    * 
    * @throws IllegalArgumentException if count is not in the range 1..63
    * @throws TimeoutException if there is no reply from the remote device.
    * @throws IOException if there is a communication error.
    */
   public byte[] read(MemoryLocation location, int offset, int count) throws IOException, TimeoutException
   {
      return read(new MemoryRead(location, offset, count));
   }

   /**
    * Read bytes, using the supplied memory-read application object.
    * 
    * @param memoryRead - the read application to send.
    * @return The read bytes.
    * 
    * @throws TimeoutException if there is no reply from the remote device.
    * @throws IOException if there is a communication error.
    */
   byte[] read(final MemoryRead memoryRead) throws IOException, TimeoutException
   {
      final MemoryResponse memoryResponse = (MemoryResponse) connection.query(memoryRead);
      return memoryResponse.getData();
   }

   /**
    * Write bytes to an absolute memory address. The written bytes are verified.
    * 
    * @param address - the memory address.
    * @param data - the bytes to write.
    * 
    * @throws TimeoutException if there is no reply from the remote device.
    * @throws IOException if there is a communication error.
    */
   public void write(int address, byte[] data) throws IOException, TimeoutException
   {
      final int endAddress = address + data.length;
      final int maxBlockSize = 12;

      for (int addr = address; addr < endAddress; addr += maxBlockSize)
      {
         int blockSize = endAddress - addr;
         if (blockSize > maxBlockSize)
            blockSize = maxBlockSize;

         final byte[] dataBlock = Arrays.copyOfRange(data, addr, addr + blockSize);
         byte[] currentBlock = read(addr, blockSize);
         if (Arrays.equals(dataBlock, currentBlock))
            continue;

         connection.query(new MemoryWrite(addr, dataBlock));

         currentBlock = read(addr, blockSize);
         if (!Arrays.equals(dataBlock, currentBlock))
            throw new IOException("memory write: verify failed");
      }
   }

   /**
    * Write bytes to a location. The written bytes are verified.
    * 
    * @param location - the memory location.
    * @param data - the bytes to write.
    * 
    * @throws TimeoutException if there is no reply from the remote device.
    * @throws IOException if there is a communication error.
    */
   public void write(MemoryLocation location, byte[] data) throws IOException, TimeoutException
   {
      write(connection.getMemoryAddressMapper().getMapping(location).getAdress(), data);
   }
}
