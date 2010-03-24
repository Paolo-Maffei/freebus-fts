package org.freebus.knxcomm._plicationData;

public interface DeviceProperties {

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
