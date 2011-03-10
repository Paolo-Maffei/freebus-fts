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
public class MemoryConnection implements MemoryConnectionInterface
{
   private final DataConnection connection;

   /**
    * Create a memory connection.
    * 
    * @param connection - the data connection to use.
    * @throws NullPointerException if the connection is null.
    */
   public MemoryConnection(DataConnection connection)
   {
      if (connection == null)
         throw new NullPointerException("connection is null");

      this.connection = connection;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DataConnection getConnection()
   {
      return connection;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[] read(int address, int count) throws IOException, TimeoutException
   {
      return read(new MemoryRead(address, count));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[] read(MemoryLocation location) throws IOException, TimeoutException
   {
      return read(new MemoryRead(location));
   }

   /**
    * {@inheritDoc}
    */
   @Override
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
    * {@inheritDoc}
    */
   @Override
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
    * {@inheritDoc}
    */
   @Override
   public void write(MemoryLocation location, byte[] data) throws IOException, TimeoutException
   {
      write(connection.getMemoryAddressMapper().getMapping(location).getAdress(), data);
   }
}
