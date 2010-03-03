package org.freebus.knxcomm.netip.blocks;

import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Interface of structures in KNXnet/IP frame bodies that contain the data for a
 * specific service type.
 */
public interface Block
{
   /**
    * Initialize the object from the given raw data, beginning at start.
    * 
    * @return number of bytes that were used.
    * @throws InvalidDataException
    */
   public int fromRawData(int[] rawData, int start) throws InvalidDataException;

   /**
    * Fill the raw data of the object into the array rawData, starting at index
    * start. The bytes in rawData are in the range 0..255.
    * 
    * @return number of bytes that were used.
    */
   public int toRawData(int[] rawData, int start);
}
