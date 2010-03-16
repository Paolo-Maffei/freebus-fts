package org.freebus.knxcomm.AplicationData;

public interface DeviceDescriptor {

	public void formRawData(int[] Data) throws Exception;

	public MemoryAddressMapper getMemoryAddressMapper();
}
