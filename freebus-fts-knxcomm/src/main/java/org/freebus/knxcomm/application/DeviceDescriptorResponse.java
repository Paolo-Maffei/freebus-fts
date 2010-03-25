package org.freebus.knxcomm.application;

import java.util.Arrays;

import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A response to a {@link DeviceDescriptorRead device descriptor read} request.
 */
public class DeviceDescriptorResponse extends DeviceDescriptorRead
{
   private int[] descriptor;

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
    * @param descriptor - the device descriptor.
    */
   public DeviceDescriptorResponse(int descriptorType, int[] descriptor)
   {
      super(descriptorType);
      setDescriptor(descriptor);
   }

   /**
    * Set the device descriptor. The device descriptor is cloned.
    *
    * @param descriptor - the device descriptor to set
    */
   public void setDescriptor(int[] descriptor)
   {
      this.descriptor = descriptor == null ? null : descriptor.clone();
   }

   /**
    * @return the device descriptor.
    */
   public int[] getDescriptor()
   {
      return descriptor;
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
         descriptor = Arrays.copyOfRange(rawData, start, start + length);
      else descriptor = null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      int pos = super.toRawData(rawData, start);

      if (descriptor != null)
      {
         for (int i = 0; i < descriptor.length; ++i)
            rawData[pos++] = descriptor[i];
      }

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return (getType().getApci() << 8) | (descriptor == null ? 0 : descriptor[0]);
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

      return descriptor == null ? oo.descriptor == null : Arrays.equals(descriptor,
            oo.descriptor);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      final StringBuffer sb = new StringBuffer();
      sb.append(super.toString()).append(" descriptor");

      if (descriptor != null)
      {
         for (int i = 0; i < descriptor.length; ++i)
            sb.append(String.format(" 0x%02x", descriptor[i]));
      }

      return sb.toString();
   }
}
