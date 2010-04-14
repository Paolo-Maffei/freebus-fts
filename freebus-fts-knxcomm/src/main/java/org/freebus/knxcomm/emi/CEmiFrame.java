/**
 *
 */
package org.freebus.knxcomm.emi;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.freebus.knxcomm.emi.types.EmiFrameType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A cEMI frame
 */
public class CEmiFrame
{
   private EmiFrame frame;
   private byte[] info;

   /**
    * Create an empty cEMI frame object.
    */
   public CEmiFrame()
   {
   }

   /**
    * @return the EMI frame
    */
   public EmiFrame getFrame()
   {
      return frame;
   }

   /**
    * Set the {@link EmiFrame EMI frame}.
    *
    * @param frame - the frame to set
    */
   public void setFrame(EmiFrame frame)
   {
      this.frame = frame;
   }

   /**
    * @return the additional information, or null if none.
    */
   public byte[] getInfo()
   {
      return info;
   }

   /**
    * Set additional information.
    *
    * @param info - the additional information to set
    */
   public void setInfo(byte[] info)
   {
      this.info = info;
   }

   /**
    * Initialize the object from a {@link DataInput data input stream}.
    *
    * @param in - the input stream to read.
    *
    * @throws InvalidDataException
    */
   public void readData(DataInput in) throws IOException
   {
      final EmiFrameType type = EmiFrameType.valueOf(in.readUnsignedByte());

      final int infoLen = in.readUnsignedByte();
      if (infoLen > 0)
      {
         info = new byte[infoLen];
         in.readFully(info);
      }
      else info = null;

      frame = EmiFrameFactory.createFrame(type);

      if (frame instanceof EmiTelegramFrame)
         ((EmiTelegramFrame) frame).setForceExtTelegram(true);

      frame.readData(in);
   }

   /**
    * Write the object to a {@link DataOutput data output stream}.
    *
    * @param out - the output stream to write the object to.
    *
    * @throws IOException
    */
   public void writeData(DataOutput out) throws IOException
   {
      if (frame instanceof EmiTelegramFrame)
         ((EmiTelegramFrame) frame).setForceExtTelegram(true);

      out.write(frame.getType().code);

      if (info == null)
      {
         out.write(0);
      }
      else
      {
         out.write(info.length);
         out.write(info);
      }

      frame.writeData(out);
   }
}
