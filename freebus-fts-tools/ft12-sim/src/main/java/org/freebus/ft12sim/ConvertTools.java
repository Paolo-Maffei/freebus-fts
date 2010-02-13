package org.freebus.ft12sim;

import java.util.ArrayList;

public class ConvertTools {

	private static  int getIntValueforHEX(char c) {

		for (int i = 0; i < HEX_CHAR_TABLE.length; i++) {
			if (HEX_CHAR_TABLE[i] == c){
				return i;}
		}
		return c;

	}

//	public static void main(String[] args) throws Exception {
//		System.out.println(getHexString(String2IntArray("\n  AB C D4 ")));
//	}
 final static byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2',
			(byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
			(byte) '8', (byte) '9', (byte) 'A', (byte) 'B', (byte) 'C',
			(byte) 'D', (byte) 'E', (byte) 'F' };

	

	public static  int[] String2IntArray(String string){
		 ArrayList<Integer> intarray = new ArrayList<Integer>();
		  StringBuffer buf = new StringBuffer(string);
		  
		  int x=0;
		  for ( int i = 0; i < buf.length(); i++ ) {
			if(  buf.charAt(i)!=' ' &&  buf.charAt(i)!='\n'){
				x = getIntValueforHEX( buf.charAt(i));
			
			i++;
			if(i < buf.length()){
			if(  buf.charAt(i)!=' ' ){
				x = x << 4;
				x =x +getIntValueforHEX( buf.charAt(i));
			}}
			intarray.add(x);}
		  }
		  
		  
		  int[] returnValue = new int[intarray.size()];
		 for (int i =0;i<intarray.size();i++){
			 returnValue[i]= intarray.get(i).intValue();
		 }
		
		return returnValue;
	}

	public static String getHexString(int[] raw) throws Exception {

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
