package org.freebus.knxcomm.aplicationData;

import java.util.ArrayList;
import java.util.Properties;

public class MemoryAddressMapper {

	private class MemoryAddressList extends ArrayList<MemoryAddress> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7126842835606550631L;

	}

	/**
	 * 
	 */

	private static final long serialVersionUID = -4685160705756019114L;
	Properties deviceProperties;

	private MemoryAddressList memoryAddressList;

	/**
	 * Creates the MemoryAddressList with Parameters from the Properties File
	 * @param deviceProperties
	 */
	protected MemoryAddressMapper(Properties deviceProperties) {
		this.deviceProperties = deviceProperties;
		String strlength;
		String strAddress;
		this.memoryAddressList = new MemoryAddressList();
		for (MemoryAddressTypes a : MemoryAddressTypes.values()) {

			strAddress = deviceProperties.getProperty("MemoryAddress."
					+ a.toString() + "Address");
			strlength = deviceProperties.getProperty("MemoryAddress."
					+ a.toString() + "Length");
			// System.out.println(strAddress);
			if (strAddress != null) {
				MemoryAddress memoryAddress = new MemoryAddress(
						AddressString2intAdr(strAddress), Integer
								.parseInt(strlength), a);

				memoryAddressList.add(memoryAddress);
			}
		}

	}

	/**
	 * Convert a 4 Digits long String of Hex chars to one integer value
	 * 
	 * @param adr
	 * @return
	 */

	public int AddressString2intAdr(String adr) {
		char[] HEX_String_TABLE = { '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		int x = 0;
		int y;

		for (int i = 0; i < adr.length(); i++) {
			int j = 0;
			for (char s : HEX_String_TABLE) {
				char a = adr.charAt(i);

				if (s == a) {
					y = j << (adr.length() - 1 - i) * 4;
					x = x + y;
				}
				j++;
			}

		}
		return x;
	}

	public MemoryAddress getMemoryAddress(int[] Address) {
		int memadr = memadrArray2int(Address);
		for (MemoryAddress m : memoryAddressList) {
			memadrArray2int(Address);
			if (m.getAdress() <= memadr
					&& m.getAdress() + m.getLength() > memadr)
				return m;

		}

		return null;
	}
	
	public MemoryAddress getMemoryAddress(MemoryAddressTypes AddressType) {
		for (MemoryAddress m : memoryAddressList) {
			
			if (m.getMemoryAddressType() == AddressType)
				return m;

		}

		return null;
	}
	private int memadrArray2int(int[] Address) {
		int a = Address[0];
		a = a << 8;
		a = a + Address[1];
		return a;

	}

}
