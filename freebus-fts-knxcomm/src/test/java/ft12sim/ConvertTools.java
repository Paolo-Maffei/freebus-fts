package ft12sim;

import java.io.UnsupportedEncodingException;



public class ConvertTools {

	public String getHexString(int[] raw) throws Exception
			 {

		final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2',
				(byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
				(byte) '8', (byte) '9', (byte) 'A', (byte) 'B', (byte) 'C',
				(byte) 'D', (byte) 'E', (byte) 'F' };

		{
			byte[] hex = new byte[3 * raw.length];
			int index = 0;

			for (int b : raw) {
				int v = b & 0xFF;
				hex[index++] = HEX_CHAR_TABLE[v >>> 4];
				hex[index++] = HEX_CHAR_TABLE[v & 0xF];
				hex[index++] = 0x20;
			}
			return new String(hex, "ASCII");
		}
	}
	
	public int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}
}
