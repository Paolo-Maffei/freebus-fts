package org.freebus.knxcomm.emi;

import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Interface for "External Message Interface" (EMI) frames.
 */
public interface EmiFrame
{
   /**
    * @return the type of the message.
    */
   public EmiFrameType getType();

   /**
    * Initialize the message from the given raw data, beginning at start. The
    * first byte is expected to be the message type.
    *
    * Must be overridden in subclasses.
    *
    * @throws InvalidDataException
    */
   public void fromRawData(int[] rawData, int start) throws InvalidDataException;

   /**
    * Fill the raw data of the message into the array rawData, starting at index
    * start. The bytes in rawData are in the range 0..255.
    *
    * @return number of bytes that were used.
    */
   public int toRawData(int[] rawData, int start);

   /**
    * Initialize the object from a {@link DataInput data input stream}. The
    * first byte of the stream is expected to be the first byte after the frame
    * type.
    *
    * @param in - the input stream to read.
    *
    * @throws InvalidDataException
    */
//   public void readData(DataInput in) throws IOException;
}
