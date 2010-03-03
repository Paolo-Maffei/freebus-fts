package org.freebus.knxcomm.netip.blocks;

import java.util.Arrays;

import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Manufacturer data device-information block.
 */
public final class ManufacturerData implements Block
{
   private int id;
   private int[] data;

   /**
    * @return the 2-byte KNX manufacturer id.
    */
   public int getManufacturerId()
   {
      return id;
   }

   /**
    * Set the 2-byte KNX manufacturer id.
    * 
    * @param id - the manufacturer id to set.
    */
   public void setManufacturerId(int id)
   {
      this.id = id;
   }

   /**
    * @return manufacturer specific data.
    */
   public int[] getData()
   {
      return data;
   }

   /**
    * Set manufacturer specific data.
    * 
    * @param data - the data to set.
    */
   public void setData(int[] data)
   {
      this.data = data;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int fromRawData(int[] rawData, int start) throws InvalidDataException
   {
      int pos = start;

      final int dataLen = rawData[pos++] - 4;
      ++pos; // skip block type code

      id = (rawData[pos++] << 8) | rawData[pos++];

      if (dataLen > 0)
         data = Arrays.copyOfRange(rawData, pos, pos + dataLen);
      else data = null;

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      int pos = start;
      final int dataLen = data == null ? 0 : data.length;

      rawData[pos++] = dataLen + 4;
      rawData[pos++] = id >> 8;
      rawData[pos++] = id & 0xff;

      for (int i = 0; i < dataLen; ++i)
         rawData[pos++] = data[i];

      return pos - start;
   }

}
