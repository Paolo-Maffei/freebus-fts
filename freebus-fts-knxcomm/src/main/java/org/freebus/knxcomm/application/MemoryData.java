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
    * Create a memory data object with address 0 and no data.
    */
   public MemoryData()
   {
      this(0, null);
   }

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

      if (data != null && data.length > 63)
         throw new IllegalArgumentException("memory data is too long");

      this.data = data;
   }

   /**
    * @return the memory data
    */
   public int[] getData()
   {
      return data;
   }

   /**
    * Set the data. Up to 63 bytes are allowed.
    *
    * @param data the data to set
    *
    * @throws IllegalArgumentException if the supplied memory data has more than
    *            63 bytes.
    */
   public void setData(int[] data)
   {
      if (data != null && data.length > 63)
         throw new IllegalArgumentException("memory data is too long");

      this.data = data;
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

      setData(Arrays.copyOfRange(rawData, start, count));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      final ApplicationType appType = getType();
      int pos = start;

      rawData[pos++] = (appType.apci & 255) | (getCount() & 0x3f);

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

      if (!(o instanceof MemoryData) || !super.equals(o))
         return false;

      final MemoryData oo = (MemoryData) o;
      return data == null ? oo.data == null : Arrays.equals(data, oo.data);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      final StringBuffer sb = new StringBuffer();
      sb.append(super.toString()).append(' ').append(getCount()).append(" bytes");

      if (data != null)
      {
         sb.append(':');
         for (int i = 0; i < data.length; ++i)
            sb.append(String.format(" %02X", data[i]));
      }

      return sb.toString();
   }
}
