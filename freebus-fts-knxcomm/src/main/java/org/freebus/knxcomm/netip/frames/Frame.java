package org.freebus.knxcomm.netip.frames;

import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Interface for KNXnet/IP frames for requests and answers.
 */
public interface Frame
{
   /**
    * @return the frame service type.
    */
   public ServiceType getServiceType();

   /**
    * Initialize the object from the given data.
    * 
    * @param data - the data to process.
    * 
    * @return the number of bytes that were read.
    * 
    * @throws InvalidDataException
    */
   public int fromData(int[] data) throws InvalidDataException;

   /**
    * Fill the data of the object into the array rawData.
    * 
    * @param data - the data buffer to fill.
    * 
    * @return number of bytes that were written.
    */
   public int toData(int[] data);
}
