package org.freebus.knxcomm.application;

import java.util.Arrays;

import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A response to a {@link DeviceDescriptorRead device descriptor read} request.
 * A {@link #getType()

 */
public class DeviceDescriptorResponse extends DeviceDescriptor
{
   private int[] deviceDescriptor;

   /**
    * Create an object with device descriptor type 0 and no device descriptor
    * data.
    */
   public DeviceDescriptorResponse()
   {
      super(0);
   }

   /**
    * Create a request object with a device descriptor type.
    *
    * @param descriptorType - the device descriptor type.
    * @param descriptor - the device descriptor.
    */
   public DeviceDescriptorResponse(int descriptorType, int[] descriptor)
   {
      super(descriptorType);
   }

   /**
    * Set the device descriptor.
    *
    * @param deviceDescriptor - the device descriptor to set
    */
   public void setDeviceDescriptor(int[] deviceDescriptor)
   {
      this.deviceDescriptor = deviceDescriptor;
   }

   /**
    * @return the device descriptor.
    */
   public int[] getDeviceDescriptor()
   {
      return deviceDescriptor;
   }

   /**
    * @return The type of the application: {@link ApplicationType#DeviceDescriptor_Read}.
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
      super.fromRawData(rawData, start++, length--);
      deviceDescriptor = Arrays.copyOfRange(rawData, start, start + length);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      int pos = super.toRawData(rawData, start);

      if (deviceDescriptor != null)
      {
         for (int i = 0; i < deviceDescriptor.length; ++i)
            rawData[pos++] = deviceDescriptor[i];
      }

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!super.equals(o))
         return false;

      if (!(o instanceof DeviceDescriptor))
         return false;

      final DeviceDescriptorResponse oo = (DeviceDescriptorResponse) o;
      return deviceDescriptor == null ? oo.deviceDescriptor == null : Arrays.equals(deviceDescriptor,
            oo.deviceDescriptor);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      final StringBuffer sb = new StringBuffer();
      sb.append(super.toString()).append(" descriptor");

      if (deviceDescriptor != null)
      {
         for (int i = 0; i < deviceDescriptor.length; ++i)
            sb.append(String.format(" 0x%02x", deviceDescriptor[i]));
      }

      return sb.toString();
   }
}
