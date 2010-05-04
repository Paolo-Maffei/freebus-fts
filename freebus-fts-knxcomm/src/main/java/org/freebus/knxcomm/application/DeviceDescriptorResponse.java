package org.freebus.knxcomm.application;

import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor0;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor2;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptorProperties;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A response to a {@link DeviceDescriptorRead device descriptor read} request.
 */
public class DeviceDescriptorResponse extends AbstractApplication
{
   private DeviceDescriptor descriptor;

   /**
    * Create an object with device descriptor type 0 and no device descriptor
    * data.
    */
   public DeviceDescriptorResponse()
   {
      this(DeviceDescriptor0.NULL);
   }

   /**
    * Create a response object with a device descriptor type. The device
    * descriptor is cloned.
    *
    * @param descriptor - the device descriptor.
    */
   public DeviceDescriptorResponse(DeviceDescriptor descriptor)
   {
      this.descriptor = descriptor;
   }

   /**
    * Set the device descriptor.
    *
    * @param descriptor - the device descriptor to set
    */
   public void setDescriptor(DeviceDescriptor descriptor)
   {
      this.descriptor = descriptor;
   }

   /**
    * @return the device descriptor.
    */
   public DeviceDescriptor getDescriptor()
   {
      return descriptor;
   }

   /**
    * @return the type of the device descriptor.
    */
   public int getDescriptorType()
   {
      if (descriptor == null)
         return DeviceDescriptorRead.INVALID_DESCRIPTOR_TYPE;
      return descriptor.getTypeCode();
   }

   /**
    * @return The type of the application:
    *         {@link ApplicationType#DeviceDescriptor_Read}.
    */
   @Override
   public ApplicationType getType()
   {
      return ApplicationType.DeviceDescriptor_Response;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void fromRawData(int[] rawData, int start, int length) throws InvalidDataException
   {
      int type = rawData[start] & DeviceDescriptorRead.DESCRIPTOR_TYPE_MASK;

      if (type == 0)
      {
         descriptor = new DeviceDescriptor0((rawData[start + 1] << 8) | rawData[start + 2]);
      }
      else if (type == 2)
      {
         final byte[] descriptorData = new byte[length - 1];
         for (int i = 0; i < length - 1; ++i)
            descriptorData[i] = (byte) rawData[i];

         descriptor = DeviceDescriptor2.valueOf(descriptorData);
      }
      else
      {
         descriptor = null;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      int pos = start;

      rawData[pos++] = (getType().getApci() | getDescriptorType()) & 255;

      final byte[] descriptorData = descriptor.toByteArray();
      for (int i = 0; i < descriptorData.length; ++i)
         rawData[pos++] = descriptorData[i] & 255;

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return (getType().getApci() << 8) | (descriptor == null ? 0 : descriptor.hashCode());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof DeviceDescriptorResponse))
         return false;

      final DeviceDescriptorResponse oo = (DeviceDescriptorResponse) o;

      return descriptor == null ? oo.descriptor == null : descriptor.equals(oo.descriptor);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return super.toString() + ' ' + descriptor;
   }

   @Override
   public ApplicationType getApplicationResponses()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isDeviceDescriptorRequired()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void setDeviceDescriptorProperties(DeviceDescriptorProperties deviceDescriptorProperties)
   {
      // TODO Auto-generated method stub

   }
}
