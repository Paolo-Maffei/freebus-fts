package ft12sim;

import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class Main {
	static InputStream in;
	static OutputStream out;
	static FT12replay ft12replay; 

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		opencomport("COM4");
		ft12replay = new FT12replay(in,out);

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
