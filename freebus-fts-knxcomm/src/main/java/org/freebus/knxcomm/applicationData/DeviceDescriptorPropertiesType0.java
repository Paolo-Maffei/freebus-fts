package org.freebus.knxcomm.applicationData;

import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.freebus.knxcomm.application.DeviceDescriptorResponse;


public class DeviceDescriptorPropertiesType0 implements DeviceDescriptorProperties {
	Properties deviceProperties;

	/**
	 * Create a DeviceDescriptor String from the Application data Array
	 * 
	 * @param data
	 *            Application data Array
	 * 
	 * @return Mask String
	 */
	private String Type2MaskString(int[] data) {
		return String.format("%02X%02X", data[0] , data[1]);
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
	 * @throws IOException 
	 */
	public void loadProperties(DeviceDescriptorResponse deviceDescriptorResponse) throws IOException {
		deviceProperties = new Properties();
		String mask = Type2MaskString(deviceDescriptorResponse.getDescriptor());
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