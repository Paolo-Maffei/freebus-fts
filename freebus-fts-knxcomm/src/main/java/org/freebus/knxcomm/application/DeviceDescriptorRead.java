package org.freebus.knxcomm.application;


/**
 * Read the device descriptor of a remote device.
 */
public class DeviceDescriptorRead extends DeviceDescriptor
{
   /**
    * Create an object with device descriptor type 0.
    */
   public DeviceDescriptorRead()
   {
      super(0);
   }

   /**
    * Create a request object with a device descriptor type.
    *
    * @param descriptorType - the device descriptor type.
    * @param descriptor - the device descriptor.
    */
   public DeviceDescriptorRead(int descriptorType, int[] descriptor)
   {
      super(descriptorType);
   }
}
