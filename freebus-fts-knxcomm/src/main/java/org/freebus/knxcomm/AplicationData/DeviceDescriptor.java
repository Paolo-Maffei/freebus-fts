package org.freebus.knxcomm.AplicationData;

public interface DeviceDescriptor {

	/**
	 *
	 * @param Data
	 * @throws Exception
	 */
	public void fromRawData(int[] Data) throws Exception;

	/**
	 * @return
	 */
	public MemoryAddressMapper getMemoryAddressMapper();
}
