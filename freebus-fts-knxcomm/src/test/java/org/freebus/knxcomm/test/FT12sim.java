package org.freebus.knxcomm.test;

import java.io.InputStream;
import java.io.OutputStream;


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class FT12sim implements Runnable {
	static InputStream in;
	static OutputStream out;
	static FT12replay ft12replay; 
static String comport;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		opencomport("COM4");
		ft12replay = new FT12replay(in,out);

	}

        public void run() {
    		try {
				opencomport(this.comport);
    		ft12replay = new FT12replay(in,out);
    		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    public FT12sim(String comport){
    	this.comport = comport;
    }


	public static boolean opencomport(String Comport) throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(Comport);
		CommPort commPort = portIdentifier.open(Comport, 2000);
		SerialPort serialPort = (SerialPort) commPort;
		serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		in = serialPort.getInputStream();
		out = serialPort.getOutputStream();
		System.out.println("Listen on" +Comport);
		return false;
	}

}
