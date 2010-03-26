package org.freebus.knxcomm.aplicationData;

public class MemoryAddress {
	int adress;
	int length;

	private MemoryAddressTypes MemoryAddressTypes;

	int offset;

	protected MemoryAddress(int adress, int length,
			MemoryAddressTypes memoryAddressType) {
		this.MemoryAddressTypes = memoryAddressType;
		this.adress = adress;
		this.length = length;
	}

	public int getAdress() {
		return adress;
	}

	public int getLength() {
		return length;
	}

	public MemoryAddressTypes getMemoryAddressType() {
		return MemoryAddressTypes;
	}

	public int getOffset() {
		return offset;
	}

	protected void setMemoryAddress(MemoryAddressTypes memoryAddress) {
		this.MemoryAddressTypes = memoryAddress;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
