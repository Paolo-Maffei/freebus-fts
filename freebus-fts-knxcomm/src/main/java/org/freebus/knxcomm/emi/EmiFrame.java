package org.freebus.knxcomm.emi;

import org.freebus.knxcomm.telegram.InvalidDataException;


/**
 * An "External Message Interface" (EMI) frame.
 */
public interface EmiFrame
{
   /**
    * @return the type of the message.
    */
   public abstract EmiFrameType getType();

   /**
    * Initialize the message from the given raw data, beginning at start.
    * The first byte is expected to be the message type.
    * 
    * Must be overridden in subclasses.
    *
    * @throws InvalidDataException 
    */
   public abstract void fromRawData(int[] rawData, int start) throws InvalidDataException;

   /**
    * Fill the raw data of the message into the array rawData, starting at
    * index start. The bytes in rawData are in the range 0..255.
    * 
    * @return number of bytes that were used.
    */
   public abstract int toRawData(int[] rawData, int start);
}
