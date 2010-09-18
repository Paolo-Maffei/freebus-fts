package org.freebus.knxcomm.application;

import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;

/**
 * A generic application with data bytes. For application types where no
 * application class exists.
 */
public class GenericDataApplication extends AbstractApplication
{
   private final ApplicationType type;
   private int[] data;

   /**
    * Create an instance for a specific application type.
    * 
    * @param type - the application type.
    * 
    * @throws IllegalArgumentException if the type is null
    */
   public GenericDataApplication(ApplicationType type)
   {
      this(type, null);
   }

   /**
    * Create an instance for a specific application type. The data is cloned.
    * 
    * @param type - the application type.
    * @param data - the application's data.
    * 
    * @throws IllegalArgumentException if the type is null
    */
   public GenericDataApplication(ApplicationType type, int[] data)
   {
      if (type == null)
         throw new IllegalArgumentException("type is null");

      this.type = type;
      setData(data);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ApplicationType getType()
   {
      return type;
   }

   /**
    * Set the application data. The data is cloned.
    * 
    * @param data - the data to set
    */
   public void setData(int[] data)
   {
      this.data = (data == null ? null : data.clone());
   }

   /**
    * @return the application data.
    */
   public int[] getData()
   {
      return data;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void readData(DataInput in, int length) throws IOException
   {
      if (length > 0)
      {
         data = new int[length];
         for (int i = 0; i < length; ++i)
            data[i] = in.readUnsignedByte();
      }
      else data = null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      final int dataMask = type.getDataMask();
      final int[] data = getData();
      final int apci = type.getApci();
      int pos = start;

      rawData[pos++] = ((apci & ~dataMask) | (getApciValue() & dataMask)) & 255;

      if (data != null)
      {
         for (int i = 0; i < data.length; ++i)
            rawData[pos++] = data[i];
      }

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return type.getApci();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof GenericDataApplication))
         return false;

      final GenericDataApplication oo = (GenericDataApplication) o;
      return type == oo.type && (data == null ? oo.data == null : Arrays.equals(data, oo.data));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      final StringBuffer sb = new StringBuffer();

      sb.append(type.name());

      if (data != null && data.length > 0)
      {
         sb.append(" data");
         for (int i = 0; i < data.length; ++i)
            sb.append(String.format(" %02x", data[i]));
      }
      else if (getType().getDataMask() > 0)
      {
         sb.append(" data").append(String.format(" %02x", getApciValue()));
      }
      else
      {
         sb.append(" no data");
      }

      return sb.toString();
   }
}
