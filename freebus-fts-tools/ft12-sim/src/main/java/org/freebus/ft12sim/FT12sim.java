package org.freebus.ft12sim;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.BasicConfigurator;
import org.freebus.knxcomm.serial.SerialPortUtil;
import org.freebus.knxcomm.serial.SerialPortWrapper;

public class FT12sim implements Runnable {
	static SerialPortWrapper port;
	static InputStream in;
	static OutputStream out;
	static FT12replay ft12replay;
	private static String ftscomComport;
	private static Config cfg;
	static {
		// Configure Log4J
		BasicConfigurator.configure();

		// Ensure that the native serial port library and the RXTX library are
		// loaded.
		SerialPortUtil.loadSerialPortLib();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// opencomport("/dev/ttyS0"); // Linux Default
		cfg = new Config();
		System.out.println(cfg.getStringValue("XMLfile"));
		XMLreader xmlreader = new XMLreader(cfg.getStringValue("XMLfile"), cfg
				.getStringValue("XSDfile"));
		Sequences sequences = xmlreader.getSequences();
		for (Sequence b : sequences) {

			System.out.println("Resivec "
					+ ConvertTools.getHexString(b.ResciveFrame));
			for (int[] x : b.getTransmitFrames()) {
				System.out.println("transmit " + ConvertTools.getHexString(x));
			}

		}
		ftscomComport = cfg.get("comport");
		opencomport(ftscomComport);

		ft12replay = new FT12replay(in, out, sequences);
	}

	// public void run()
	// {
	// try
	// {
	// opencomport(ftscomComport);
	// ft12replay = new FT12replay(in, out);
	// }
	// catch (Exception e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public FT12sim(String comport) {
		FT12sim.ftscomComport = comport;
	}

	public static boolean opencomport(String comport) throws Exception {
		port = new SerialPortWrapper();
		port.open(comport, 57600, SerialPortWrapper.DATABITS_8,
				SerialPortWrapper.STOPBITS_1, SerialPortWrapper.PARITY_NONE);
		in = port.getInputStream();
		out = port.getOutputStream();
		System.out.println("Listen on" + comport);
		return false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
