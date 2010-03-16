package org.freebus.knxcomm.AplicationData;

/**
 *Factory Class for DeviceDescriptors
 * 
 */
public class DeviceDescriptorFactory {

	/**
	 * Creates the DeviceDescriptor class in depends of the DeviceDescriptortype
	 * bits in a DeviceDescriptor request
	 * 
	 * @throws Exception
	 * 
	 */

	public DeviceDescriptor getDeviceDescriptor(int[] data) throws Exception {
		DeviceDescriptor deviceDescriptor = null;

		if ((data[0] & 0x3F) == 0) {
			deviceDescriptor = new DeviceDescriptorType0();
			deviceDescriptor.formRawData(data);

		}
		if ((data[0] & 0x3F) == 3) {
			deviceDescriptor = new DeviceDescriptorType3();
			deviceDescriptor.formRawData(data);
		}
		return deviceDescriptor;

	}

}
