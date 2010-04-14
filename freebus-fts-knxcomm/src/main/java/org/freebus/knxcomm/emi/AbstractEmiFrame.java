package org.freebus.knxcomm.emi;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.freebus.knxcomm.emi.types.EmiFrameType;


/**
 * Base class for "External Message Interface" (EMI) frames.
 */
public abstract class AbstractEmiFrame implements EmiFrame
{
   private final EmiFrameType type;

   /**
    * Internal constructor that sets the message type to {@link EmiFrameType#UNKNOWN}.
    */
   protected AbstractEmiFrame()
   {
      this.type = EmiFrameType.UNKNOWN;
   }

   /**
    * Create a new message and set the message type.
    * Internal constructor for subclasses.
    */
   protected AbstractEmiFrame(EmiFrameType type)
   {
      this.type = type;
   }

   /* (non-Javadoc)
    * @see org.freebus.fts.eib.EmiMessageInterface#getType()
    */
   public final EmiFrameType getType()
   {
      return type;
   }

   /**
    * @return the message type as a string.
    */
   public final String getTypeString()
   {
      final Class<?> clazz = getClass();
      final Class<?> enclosingClazz = clazz.getEnclosingClass();

      if (enclosingClazz == null) return clazz.getSimpleName();
      return enclosingClazz.getSimpleName() + '.' + clazz.getSimpleName();
   }

   /**
    * {@inheritDoc}
    */
   public abstract void readData(DataInput in) throws IOException;

   /**
    * {@inheritDoc}
    */
   public abstract void writeData(DataOutput out) throws IOException;

   /**
    * {@inheritDoc}
    */
   final public byte[] toByteArray()
   {
      final ByteArrayOutputStream outByteStream = new ByteArrayOutputStream(1024);
      final DataOutputStream out = new DataOutputStream(outByteStream);

      try
      {
         out.write(getType().code);
         writeData(out);

         return outByteStream.toByteArray();
      }
      catch (IOException e)
      {
         throw new RuntimeException();
      }
   }

   /**
    * @return the address as a string: "xx.xx.xx" for a physical address,
    * "xx/xx/xx" for a group address.
    */
   protected String addrToString(int addr, boolean isGroup)
   {
      if (isGroup) return String.format("%d/%d/%d", (addr>>11)&15, (addr>>7)&15, addr&127);
      return String.format("%d.%d.%d", (addr>>12)&15, (addr>>8)&15, addr&255);
   }

   /**
    * @return the message in a human readable form.
    */
   @Override
   public String toString()
   {
      return getTypeString();
   }
}
