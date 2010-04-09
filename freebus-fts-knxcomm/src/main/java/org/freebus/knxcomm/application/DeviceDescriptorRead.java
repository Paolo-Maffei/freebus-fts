package org.freebus.knxcomm.application;

import org.freebus.knxcomm.applicationData.DeviceDescriptorProperties;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Read a device descriptor.
 */
public class DeviceDescriptorRead implements Application
{
   /**
    * An invalid device descriptor type.
    */
   final public static int INVALID_DESCRIPTOR_TYPE = 0x3f;

   /**
    * The bit mask for the device descriptor types.
    */
   final public static int DESCRIPTOR_TYPE_MASK = 0x3f;

   private int descriptorType;

   /**
    * Create a device descriptor object for device descriptor type 0.
    */
   public DeviceDescriptorRead()
   {
      this(0);
   }

   /**
    * Create a device descriptor object.
    * 
    * @param descriptorType - the device descriptor type.
    */
   public DeviceDescriptorRead(int descriptorType)
   {
      setDescriptorType(descriptorType);
   }

   /**
    * @return the device descriptor type
    */
   public int getDescriptorType()
   {
      return descriptorType;
   }

   /**
    * Set the device descriptor type.
    * 
    * @param descriptorType - the descriptor type to set
    */
   public void setDescriptorType(int descriptorType)
   {
      this.descriptorType = descriptorType;
   }

   /**
    * @return The type of the application:
    *         {@link ApplicationType#DeviceDescriptor_Read}.
    */
   @Override
   public ApplicationType getType()
   {
      return ApplicationType.DeviceDescriptor_Read;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void fromRawData(int[] rawData, int start, int length) throws InvalidDataException
   {
      descriptorType = rawData[start] & DESCRIPTOR_TYPE_MASK;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      final ApplicationType appType = getType();
      rawData[start] = (appType.getApci() & 255) | (descriptorType & DESCRIPTOR_TYPE_MASK);
      return 1;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return getType().getApci() | descriptorType;
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

      final DeviceDescriptorRead oo = (DeviceDescriptorRead) o;
      return descriptorType == oo.descriptorType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return getType().toString() + " descriptor #" + descriptorType;
   }

   @Override
   public boolean isDeviceDescriptorRequiered()
   {
      // TODO Auto-generated method stub
      return false;
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
      ApplicationTypeResponse appr = new ApplicationTypeResponse();
      appr.add(ApplicationType.DeviceDescriptor_Read);
      appr.add(ApplicationType.DeviceDescriptor_Response);
      return appr;
   }
}
