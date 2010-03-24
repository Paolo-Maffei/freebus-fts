package org.freebus.knxcomm.application;

import java.util.Arrays;

import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A generic application with data bytes. For application types where no
 * application class exists.
 */
public class GenericDataApplication implements Application
{
   private final ApplicationType type;
   private int[] data;

   /**
    * Create an instance for a specific application type.
    *
    * @param type - the application type.
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
    */
   public GenericDataApplication(ApplicationType type, int[] data)
   {
      this.type = type;
      this.data = (data == null ? null : data.clone());
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
   public void fromRawData(int[] rawData, int start, int length) throws InvalidDataException
   {
      ++start;
      this.setData(Arrays.copyOfRange(rawData, start, start + length));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      final int dataMask = type.getDataMask();
      final int[] data = getData();
      int pos = start;

      if (data == null)
      {
         rawData[pos++] = type.apci & 255;
      }
      else if (data.length == 1 && dataMask != 0)
      {
         rawData[pos++] = (type.apci & ~dataMask & 255) | (data[0] & dataMask);
      }
      else
      {
         rawData[pos++] = type.apci & 255;

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
      return type == null ? 0 : type.apci;
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

      if (data == null || data.length == 0)
         sb.append(" no data");
      else
      {
         sb.append(" data");
         for (int i = 0; i < data.length; ++i)
            sb.append(String.format(" %02x", data[i]));
      }

      return sb.toString();
   }
}
