package org.freebus.knxcomm.netip.frames;

import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Interface for KNXnet/IP frame bodies for requests and answers.
 */
public interface FrameBody
{
   /**
    * @return the frame service type.
    */
   public ServiceType getServiceType();

   /**
    * Initialize the object from the given raw data, beginning at start.
    * 
    * @return the number of bytes that were read from rawData.
    * 
    * @throws InvalidDataException
    */
   public int fromRawData(int[] rawData, int start) throws InvalidDataException;

   /**
    * Fill the raw data of the object into the array rawData, starting at index
    * start. The bytes in rawData are in the range 0..255.
    * 
    * @return number of bytes that were written into rawData.
    */
   public int toRawData(int[] rawData, int start);
}
