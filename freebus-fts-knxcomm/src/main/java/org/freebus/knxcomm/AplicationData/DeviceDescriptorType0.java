package org.freebus.knxcomm.AplicationData;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;


public class DeviceDescriptorType0 implements DeviceDescriptor {
	Properties deviceProperties;

	/**
	 * Create a DeviceDescriptor String from the Application data Array
	 * 
	 * @param data
	 *            Application data Array
	 * 
	 * @return Mask String
	 */
	private String Data2MaskString(int data[]) {
		return String.format("%02x%02x", data[1], data[2]).toUpperCase();
	}

	@Override
	public void fromRawData(int[] Data) throws Exception {

		loadProperties(Data2MaskString(Data));
	}

	public Properties getDeviceProperties() {
		return deviceProperties;
	}

	/**
	 * @return
	 * 
	 *         Creates a MemoryAddressMapper
	 */
	public MemoryAddressMapper getMemoryAddressMapper() {
		return new MemoryAddressMapper(deviceProperties);
	}



	/**
	 * Load the property file for a given device descriptor. 
	 * 
	 * @param mask
	 * @throws Exception
	 */
	private void loadProperties(String mask) throws Exception {
		deviceProperties = new Properties();
		InputStream in = null;
		ClassLoader cl = this.getClass().getClassLoader();
		in = cl.getResourceAsStream("DeviceDescriptorType0_"+ mask + ".properties");
		if (in == null) {
			in = new FileInputStream(
					"../freebus-fts-knxcomm/src/main/resources/DeviceDescriptorType0_"
							+ mask + ".properties");
		}
		deviceProperties.load(in);

		in.close();

	}
}
