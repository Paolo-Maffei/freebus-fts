package org.freebus.knxcomm.application;

import java.util.Arrays;

import org.freebus.knxcomm.telegram.InvalidDataException;

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
    * @return the memory data
    */
   public int[] getData()
   {
      return data;
   }

   /**
    * Set the data. Up to 63 bytes are allowed. The supplied data is copied.
    *
    * @param data the data to set.
    *
    * @throws IllegalArgumentException if the supplied memory data has more than
    *            63 bytes.
    */
   public void setData(int[] data)
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
    * @return the number of bytes that the {@link #getData data} contains.
    */
   @Override
   public int getCount()
   {
      return data == null ? 0 : data.length;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void fromRawData(int[] rawData, int start, int length) throws InvalidDataException
   {
      final int count = rawData[start++] & 0x3f;
      setAddress((rawData[start++] << 8) | rawData[start++]);

      setData(Arrays.copyOfRange(rawData, start, start + count));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      final ApplicationType appType = getType();
      int pos = start;

      final int count = getCount();
      rawData[pos++] = (appType.getApci() & 255) | (count & appType.getDataMask());

      final int address = getAddress();
      rawData[pos++] = (address >> 8) & 255;
      rawData[pos++] = address & 255;

      final int[] data = getData();
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
