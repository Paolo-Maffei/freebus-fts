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
		String[] HEX_String_TABLE = { "0", "1", "2", "3", "4", "5", "6", "7",
				"8", "9", "A", "B", "C", "D", "E", "F" };
		String mask = "";
		mask = HEX_String_TABLE[(data[1] & 0xF0) >> 4];
		mask = mask + HEX_String_TABLE[data[1] & 0x0F];
		mask = mask + HEX_String_TABLE[(data[2] & 0xF0) >> 4];
		mask = mask + HEX_String_TABLE[data[2] & 0x0F];
		return mask;
	}

	@Override
	public void formRawData(int[] Data) throws Exception {

		loadpropertries(Data2MaskString(Data));
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

	public MemoryAddressMapper getMemoryAdressMapper() {
		MemoryAddressMapper memoryAddressMapper;
		memoryAddressMapper = new MemoryAddressMapper(deviceProperties);
		return memoryAddressMapper;

	}

	private void loadpropertries(String mask) throws Exception {
		deviceProperties = new Properties();
		InputStream in = null;

		in = new FileInputStream(
				"../freebus-fts-knxcomm/resources/DeviceDescriptorType0_"
						+ mask + ".properties");

		deviceProperties.load(in);

		in.close();

	}
}
