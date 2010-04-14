package org.freebus.knxcomm.emi;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

import org.freebus.knxcomm.emi.types.EmiFrameType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Factory class that creates {@link EmiFrame EMI frame} objects from raw data
 * and {@link DataInput data input} streams.
 */
public final class EmiFrameFactory
{
   /**
    * Create an EMI frame object.
    *
    * @param type - the type of the EMI frame to create.
    *
    * @return the created frame, or null if the frame type is valid, but there
    *         exists no frame class for the frame type.
    */
   static public EmiFrame createFrame(EmiFrameType type)
   {
      if (type.clazz == null)
         return null;

      try
      {
         return type.clazz.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Create an EMI frame object from the input stream. It is expected that the
    * first byte of the data input stream contains the {@link EmiFrameType EMI
    * frame type}.
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
      final EmiFrameType type = EmiFrameType.valueOf(in.readUnsignedByte());
      final EmiFrame frame = createFrame(type);

      if (frame != null)
         frame.readData(in);

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

   /**
    * A temporary method until EmiFrame users use byte buffers too.
    */
   @Deprecated
   static public EmiFrame createFrame(final int[] data) throws IOException
   {
      final byte[] byteData = new byte[data.length];
      for (int i = 0; i < data.length; ++i)
         byteData[i] = (byte) data[i];

      return createFrame(new DataInputStream(new ByteArrayInputStream(byteData)));
   }

   /*
    * Disabled
    */
   private EmiFrameFactory()
   {
   }
}
