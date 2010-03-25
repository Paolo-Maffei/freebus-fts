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
   public void fromRawData(int[] rawData, int start, int length) throws InvalidDataException
   {
      final int dataMask = type.getDataMask();

      if (dataMask != 0)
      {
         final int[] tmpData = Arrays.copyOfRange(rawData, start, start + length);
         tmpData[0] &= dataMask;
         setData(tmpData);
      }
      else if (length <= 1)
      {
         setData(null);
      }
      else
      {
         setData(Arrays.copyOfRange(rawData, start + 1, start + length));
      }
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

      rawData[pos++] = apci & ~dataMask & 255;

      if (data != null)
      {
         int i = 0;

         if (dataMask > 0)
            rawData[pos - 1] |= data[i++] & dataMask;

         for (; i < data.length; ++i)
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
