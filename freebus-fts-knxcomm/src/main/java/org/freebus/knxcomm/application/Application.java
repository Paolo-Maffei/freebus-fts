package org.freebus.knxcomm.application;

import org.freebus.knxcomm.telegram.ApplicationType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Interface for application layer services.
 */
public interface Application
{
   /**
    * @return The type of the application.
    */
   public ApplicationType getType();

   /**
    * Initialize the object from the given raw data, starting at start, using at
    * most length bytes.
    *
    * @param rawData - the raw data to be processed.
    * @param start - the index of the first byte in rawData to use.
    * @param length - the number of bytes to process.
    *
    * @throws InvalidDataException
    */
   public void fromRawData(int[] data, int start, int length) throws InvalidDataException;

   /**
    * Write the raw data of the message into the array rawData, starting at
    * index start.
    *
    * @param rawData - the data buffer to be filled.
    * @param start - the index of the first byte in rawData to use.
    *
    * @return number of bytes that were written.
    */
   public int toRawData(int[] rawData, int start);
}
