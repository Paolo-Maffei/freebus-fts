package org.freebus.knxcomm.application.devicedescriptor;

import org.freebus.knxcomm.applicationData.ApplicationDataException;

/**
 *Factory Class for DeviceDescriptors
 * 
 */
public final class DeviceDescriptorPropertiesFactory
{

   /**
    * Creates the DeviceDescriptor class in depends of the DeviceDescriptortype
    * bits in a DeviceDescriptor response
    * @throws ApplicationDataException 
    * 
    * 
    */
   public DeviceDescriptorProperties getDeviceDescriptor(DeviceDescriptor deviceDescriptor) throws ApplicationDataException
        
   {
      // TODO create own exception class
      DeviceDescriptorProperties deviceDescriptorProperties = null;

      if (deviceDescriptor instanceof DeviceDescriptor0 )
      {
         DeviceDescriptor0 ds0 = (DeviceDescriptor0) deviceDescriptor;
         DeviceDescriptorPropertiesType0 deviceDescriptorPropertiesType0 = new DeviceDescriptorPropertiesType0();
         deviceDescriptorPropertiesType0.loadProperties(ds0.getMaskVersion());
         deviceDescriptorProperties = (DeviceDescriptorProperties)deviceDescriptorPropertiesType0;

      }
      if (deviceDescriptor instanceof DeviceDescriptor2 )
      {
         deviceDescriptorProperties = new DeviceDescriptorPropertiesType2();

      }
      return deviceDescriptorProperties;

   }

}
