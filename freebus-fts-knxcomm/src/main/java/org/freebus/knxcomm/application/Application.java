package org.freebus.knxcomm.application;

import org.freebus.knxcomm.aplicationData.DeviceDescriptorProperties;
import org.freebus.knxcomm.telegram.InvalidDataException;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * Interface for application layer services.
 * 
 * @see {@link Telegram}
 */

public interface Application
{
   /**
    * Initialize the object from the given raw data, starting at start, using at
    * most length bytes. The first byte contains the application type.
    * 
    * @param rawData - the raw data to be processed.
    * @param start - the index of the first byte in rawData to use.
    * @param length - the number of bytes to process.
    * 
    * @throws InvalidDataException
    */
   public void fromRawData(int[] rawData, int start, int length) throws InvalidDataException;

   /**
    * @return The expected ApplicationType as an ArrayList
    */
   public ApplicationTypeResponse getApplicationResponses();

   /**
    * @return The type of the application.
    */
   public ApplicationType getType();

   /**
    * @return True if the Application needs a DeviceDescriptorProperties
    */
   public boolean isDeviceDescriptorRequiered();

   /**
    * Set the DeviceDescriptorProperties for the Application. The
    * DeviceDescriptorProperties is required if the ApplicationData Classes are
    * used.
    * 
    * @param deviceDescriptorProperties - The DeviceDescriptorProperties for the
    *           Application
    */
   public void setDeviceDescriptorProperties(DeviceDescriptorProperties deviceDescriptorProperties);

   /**
    * Write the raw data of the message into the array rawData, starting at
    * index start. The first byte has to contain the application type.
    * 
    * @param rawData - the data buffer to be filled.
    * @param start - the index of the first byte in rawData to use.
    * 
    * @return number of bytes that were written.
    */
   public int toRawData(int[] rawData, int start);
}
