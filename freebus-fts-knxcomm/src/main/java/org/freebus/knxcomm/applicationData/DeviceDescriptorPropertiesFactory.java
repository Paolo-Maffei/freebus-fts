package org.freebus.knxcomm.applicationData;

import org.freebus.knxcomm.application.DeviceDescriptorResponse;

/**
 *Factory Class for DeviceDescriptors
 * 
 */
public class DeviceDescriptorPropertiesFactory
{

   /**
    * Creates the DeviceDescriptor class in depends of the DeviceDescriptortype
    * bits in a DeviceDescriptor response
    * @throws ApplicationDataException 
    * 
    * 
    */
   public DeviceDescriptorProperties getDeviceDescriptor(DeviceDescriptorResponse deviceDescriptorResponse) throws ApplicationDataException
        
   {
      // TODO create own exception class
      DeviceDescriptorProperties deviceDescriptorProperties = null;

      if (deviceDescriptorResponse.getDescriptorType() == 0)
      {
         DeviceDescriptorPropertiesType0 DeviceDescriptorPropertiesType0 = new DeviceDescriptorPropertiesType0();
         DeviceDescriptorPropertiesType0.loadProperties(deviceDescriptorResponse);
         deviceDescriptorProperties = (DeviceDescriptorProperties)DeviceDescriptorPropertiesType0;
      }
      if (deviceDescriptorResponse.getDescriptorType() == 3)
      {
         deviceDescriptorProperties = new DeviceDescriptorPropertiesType3();

      }
      return deviceDescriptorProperties;

   }

}
