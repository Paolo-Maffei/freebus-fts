package org.freebus.knxcomm.application;

import org.freebus.knxcomm.aplicationData.DeviceDescriptorProperties;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A generic application without data bytes. For application types where no
 * application class exists.
 */
public class GenericApplication implements Application
{
   private final ApplicationType type;

   /**
    * Create an instance for a specific application type.
    * 
    * @param type - the application type.
    * 
    * @throws IllegalArgumentException if the type is null
    */
   public GenericApplication(ApplicationType type)
   {
      if (type == null)
         throw new IllegalArgumentException("type is null");

      this.type = type;
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
    * {@inheritDoc}
    */
   @Override
   public void fromRawData(int[] rawData, int start, int length) throws InvalidDataException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      rawData[start] = type.getApci() & 255;
      return 1;
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

      if (!(o instanceof GenericApplication))
         return false;

      final GenericApplication oo = (GenericApplication) o;
      return type == oo.type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return type.toString() + " no data";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isDeviceDescriptorRequiered()
   {
      // TODO Auto-generated method stub
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDeviceDescriptorProperties(DeviceDescriptorProperties deviceDescriptorProperties)
   {
      // TODO Auto-generated method stub

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ApplicationTypeResponse getApplicationResponses()
   {
      // TODO Auto-generated method stub
      return null;
   }
}
