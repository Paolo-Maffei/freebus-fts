package org.freebus.knxcomm.aplicationData;

import org.freebus.knxcomm.application.DeviceDescriptorResponse;

/**
 *Factory Class for DeviceDescriptors
 * 
 */
public class DeviceDescriptorPropertiesFactory {

	/**
	 * Creates the DeviceDescriptor class in depends of the DeviceDescriptortype
	 * bits in a DeviceDescriptor request
	 * 
	 * @throws Exception
	 * 
	 */

	public  DeviceDescriptorProperties getDeviceDescriptor(DeviceDescriptorResponse deviceDescriptorResponse) throws Exception {
		DeviceDescriptorProperties deviceDescriptorProperties = null;

		if (deviceDescriptorResponse.getDescriptorType() == 0) {
			deviceDescriptorProperties = new DeviceDescriptorPropertiesType0();
			deviceDescriptorProperties.loadProperties(deviceDescriptorResponse);
		}
		if (deviceDescriptorResponse.getDescriptorType() == 3) {
			deviceDescriptorProperties = new DeviceDescriptorPropertiesType3();
			
		}
		return deviceDescriptorProperties;

	}

}
