package org.freebus.knxcomm.internal;

import java.io.IOException;
import java.util.Arrays;

import org.freebus.knxcomm.serial.Ft12Connection;

/**
 * An implementation of the {@link Ft12Connection}, used for unit testing.
 */
public class SimulatedFt12Connection extends Ft12Connection
{
   private byte[] dataToRead, writtenData;
   public int readIndex;

   /**
    * {@inheritDoc}
    */
   @Override
   public void open()
   {
      writtenData = null;
   }

   /**
    * Reset read and write buffers.
    */
   public void reset()
   {
      dataToRead = null;
      readIndex = 0;
      writtenData = null;
   }

   /**
    * Set the data that can be read.
    */
   public void setReadableData(byte[] data)
   {
      dataToRead = data;
      readIndex = 0;
   }

   /**
    * @return the data that has been written by the last call to
    *         Ft12Connection.write(byte[], int).
    */
   public byte[] getWrittenData()
   {
      return writtenData;
   }

   @Override
   protected boolean isDataAvailable() throws IOException
   {
      return dataToRead != null && readIndex < dataToRead.length;
   }

   @Override
   protected int read() throws IOException
   {
      if (dataToRead == null || readIndex >= dataToRead.length)
         return -1;

      return dataToRead[readIndex++] & 255;
   }

   @Override
   protected void write(byte[] data, int length) throws IOException
   {
      writtenData = Arrays.copyOf(data, length);

      if (isDataAvailable())
         dataAvailable();
   }

   @Override
   public void close()
   {
   }

   @Override
   public boolean isConnected()
   {
      return true;
   }
}
