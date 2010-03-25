package org.freebus.knxcomm.application;

import java.util.Arrays;

import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A response to a {@link DeviceDescriptorRead device descriptor read} request.
 */
public class DeviceDescriptorResponse extends DeviceDescriptorRead
{
   private int[] deviceDescriptor;

   /**
    * Create an object with device descriptor type 0 and no device descriptor
    * data.
    */
   public DeviceDescriptorResponse()
   {
      this(0, null);
   }

   /**
    * Create a request object with a device descriptor type. The device
    * descriptor is cloned.
    *
    * @param descriptorType - the device descriptor type.
    * @param deviceDescriptor - the device descriptor.
    */
   public DeviceDescriptorResponse(int descriptorType, int[] deviceDescriptor)
   {
      super(descriptorType);
      setDeviceDescriptor(deviceDescriptor);
   }

   /**
    * Set the device descriptor. The device descriptor is cloned.
    *
    * @param deviceDescriptor - the device descriptor to set
    */
   public void setDeviceDescriptor(int[] deviceDescriptor)
   {
      this.deviceDescriptor = deviceDescriptor == null ? null : deviceDescriptor.clone();
   }

   /**
    * @return the device descriptor.
    */
   public int[] getDeviceDescriptor()
   {
      return deviceDescriptor;
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
      super.fromRawData(rawData, start++, length--);

      if (length > 0)
         deviceDescriptor = Arrays.copyOfRange(rawData, start, start + length);
      else deviceDescriptor = null;
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
   public int hashCode()
   {
      return (getType().getApci() << 8) | (deviceDescriptor == null ? 0 : deviceDescriptor[0]);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof DeviceDescriptorRead))
         return false;

      final DeviceDescriptorResponse oo = (DeviceDescriptorResponse) o;

      if (getDescriptorType() != oo.getDescriptorType())
         return false;

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
