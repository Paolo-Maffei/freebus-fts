package org.freebus.knxcomm.application;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * An abstract base class for {@link Application application},
 * with default implementations for common methods.
 */
public abstract class AbstractApplication implements Application
{
   private int apciValue;

   /**
    * {@inheritDoc}
    */
   public int getApciValue()
   {
      return apciValue;
   }

   /**
    * {@inheritDoc}
    */
   public void setApciValue(int value)
   {
      this.apciValue = value;
   }

   /**
    * {@inheritDoc}
    */
   public byte[] toByteArray()
   {
      try
      {
         final ByteArrayOutputStream outByteStream = new ByteArrayOutputStream(256);
         final DataOutputStream out = new DataOutputStream(outByteStream);

         final ApplicationType type = getType();
         final int dataMask = type.getDataMask();
         int ApciValue = getApciValue();
         int apci = type.getApci();
         if (dataMask > 0)
            out.writeShort(apci | (ApciValue & dataMask));
         else out.writeShort(apci);

         writeData(out);

         return outByteStream.toByteArray();
      }
      catch (IOException e)
      {
         throw new RuntimeException();
      }
   }

   /**
    * {@inheritDoc}
    */
   public void writeData(DataOutput out) throws IOException
   {
      final int data[] = new int[256];
      final int wlen = toRawData(data, 0);

      for (int i = 1; i < wlen; ++i)
         out.write((byte) data[i]);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return getType().toString();
   }
}
