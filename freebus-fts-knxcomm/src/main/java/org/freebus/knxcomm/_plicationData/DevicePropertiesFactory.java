package org.freebus.knxcomm._plicationData;

/**
 *Factory Class for DeviceDescriptors
 * 
 */
public class DevicePropertiesFactory {

	/**
	 * Creates the DeviceDescriptor class in depends of the DeviceDescriptortype
	 * bits in a DeviceDescriptor request
	 * 
	 * @throws Exception
	 * 
	 */

	public DeviceProperties getDeviceDescriptor(int[] data) throws Exception {
		DeviceProperties deviceDescriptor = null;

		if ((data[0] & 0x3F) == 0) {
			deviceDescriptor = new DevicePropertiesType0();
			deviceDescriptor.fromRawData(data);

		}
		if ((data[0] & 0x3F) == 3) {
			deviceDescriptor = new DevicePropertiesType3();
			deviceDescriptor.fromRawData(data);
		}
		return deviceDescriptor;

	}

}
