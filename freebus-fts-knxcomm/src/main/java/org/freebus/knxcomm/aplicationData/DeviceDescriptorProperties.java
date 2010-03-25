package org.freebus.knxcomm.aplicationData;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.freebus.knxcomm.application.DeviceDescriptorResponse;


public interface DeviceDescriptorProperties {

	

	/**
	 * Create a MemoryAddressMapper with the loaded PorpertieFile
	 * @return
	 */
	public MemoryAddressMapper getMemoryAddressMapper();

	/**
	 * Load the PropertiesFile for the DeviceDescriptor from the resource folder
	 * @param deviceDescriptorResponse
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadProperties(DeviceDescriptorResponse deviceDescriptorResponse) throws FileNotFoundException, IOException;
}
