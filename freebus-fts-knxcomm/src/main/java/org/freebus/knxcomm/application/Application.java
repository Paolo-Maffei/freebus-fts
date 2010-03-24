package org.freebus.knxcomm.application;

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
    * @return The type of the application.
    */
   public ApplicationType getType();

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
