package org.freebus.knxcomm.AplicationData;

public class MemoryAddress {
	int adress;
	int length;

	private MemoryAddressTypes MemoryAddressTypes;

	int offset;

	public MemoryAddress(int adress, int length,
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

	public void setAdress(int adress) {
		this.adress = adress;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setMemoryAddress(MemoryAddressTypes memoryAddress) {
		this.MemoryAddressTypes = memoryAddress;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
