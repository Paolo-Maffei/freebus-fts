package org.freebus.knxcomm.application;

import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;

/**
 * Abstract class for memory response or write, including memory data.
 */
public abstract class MemoryData extends Memory
{
   private int[] data;

   /**
    * Create a memory data object.
    * 
    * @param address - the 16 bit memory address.
    * @param data - the data. Up to 63 bytes.
    * 
    * @throws IllegalArgumentException if the supplied memory data has more than
    *            63 bytes.
    */
   protected MemoryData(int address, int[] data)
   {
      super(address);
      setData(data);
   }

   /**
    * @return The memory data
    */
   public final int[] getData()
   {
      return data;
   }

   /**
    * @return The number of bytes that {@link #getData() the data} contains.
    */
   public final int getCount()
   {
      return data == null ? 0 : data.length;
   }

   /**
    * Set the data. Up to 63 bytes are allowed. The supplied data is copied.
    * 
    * @param data the data to set.
    * 
    * @throws IllegalArgumentException if the supplied memory data has more than
    *            63 bytes.
    */
   public final void setData(int[] data)
   {
      if (data == null)
      {
         this.data = null;
      }
      else
      {
         if (data.length > 63)
            throw new IllegalArgumentException("memory data is too long");

         this.data = data.clone();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final int getApciValue()
   {
      return data == null ? 0 : data.length;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void readData(DataInput in, int length) throws IOException
   {
      int count = super.getApciValue();
      setAddress(in.readUnsignedShort());

      if (count > 0)
      {
         data = new int[count];
         for (int i = 0; i < count; ++i)
            data[i] = in.readUnsignedByte();
      }
      else data = null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      final ApplicationType appType = getType();
      int pos = start;

      final int[] data = getData();
      final int count = data == null ? 0 : data.length;

      rawData[pos++] = (appType.getApci() & 255) | (count & appType.getDataMask());

      final int address = getAddress();
      rawData[pos++] = (address >> 8) & 255;
      rawData[pos++] = address & 255;

      for (int i = 0; i < count; ++i)
         rawData[pos++] = data[i];

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return (getAddress() << 8) | (data == null ? 0 : data.length);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof MemoryData))
         return false;

      final MemoryData oo = (MemoryData) o;
      return getAddress() == oo.getAddress() && (data == null ? oo.data == null : Arrays.equals(data, oo.data));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      final StringBuffer sb = new StringBuffer();
      sb.append(super.toString());

      if (data != null)
      {
         sb.append(':');
         for (int i = 0; i < data.length; ++i)
            sb.append(String.format(" %02X", data[i]));
      }

      return sb.toString();
   }
}
