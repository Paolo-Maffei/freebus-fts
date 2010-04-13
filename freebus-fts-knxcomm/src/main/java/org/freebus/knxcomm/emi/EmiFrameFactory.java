package org.freebus.knxcomm.emi;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Factory class that creates {@link EmiFrame EMI frame} objects from raw data.
 */
public final class EmiFrameFactory
{
   /**
    * Create an EMI frame object from the input stream. It is expected that the
    * data input stream contains a full frame, including the frame header.
    *
    * @param in - the EMI frame is read from this stream.
    *
    * @return the created frame, or null if the frame type is valid, but there
    *         exists no frame class for the frame type.
    *
    * @throws IOException
    * @throws InvalidDataException
    */
   static public EmiFrame createFrame(final DataInput in) throws IOException
   {
      final int data[] = new int[1024];
      int pos = 0;

      try
      {
         // A hack, required until EmiFrame implements readData(DataInput)

         while (pos < data.length)
            data[pos++] = in.readUnsignedByte();
      }
      catch (IOException e)
      {
      }

      final EmiFrame frame = EmiFrameType.valueOf(data[0]).newInstance();
      frame.fromRawData(data, 0);

      return frame;
   }

   /**
    * Create an EMI frame object from the data. It is expected that the data
    * input stream contains a full frame, including the frame header.
    *
    * @param data - the raw data of the EMI frame.
    *
    * @return the created frame (body), or null if the frame type is valid, but
    *         there exists no frame class for the frame type.
    *
    * @throws IOException
    * @throws InvalidDataException
    */
   static public EmiFrame createFrame(final byte[] data) throws IOException
   {
      return createFrame(new DataInputStream(new ByteArrayInputStream(data)));
   }

   /*
    * Disabled
    */
   private EmiFrameFactory()
   {
   }
}
