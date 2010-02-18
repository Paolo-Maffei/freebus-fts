package org.freebus.knxcommtester;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.telegram.Application;
import org.freebus.knxcomm.telegram.PhysicalAddress;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

public class testcom {
	private static int sequence = 0;
	private static BusInterface busInterface;
//	private static Logger logger = Logger.getRootLogger();
	private static String ftscomComport;

	private static Config cfg;

	public static void main(String[] args) throws Exception {
		cfg = new Config();
		ftscomComport = cfg.get("comport");
//		try {
//			SimpleLayout layout = new SimpleLayout();
//			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
//			logger.addAppender(consoleAppender);
//
//			// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
//			logger.setLevel(Level.DEBUG);
//
//		} catch (Exception ex) {
//			System.out.println(ex);
//		}

		try {
//			logger.info("Try to open com " + ftscomComport);
			busInterface = BusInterfaceFactory
					.newSerialInterface(ftscomComport);
//			logger.info("Start test com");
			busInterface.open();
		
	
			

			Telegram telegram = new Telegram();
			telegram.setFrom(new PhysicalAddress(0, 0, 0));
			telegram.setDest(new PhysicalAddress(3, 3, 6));
			telegram.setPriority(Priority.SYSTEM);
			telegram.setRepeated(true);
			telegram.setTransport(Transport.Connect);
			
			busInterface.send(telegram);
			telegram.setFrom(new PhysicalAddress(0, 0, 0));
			telegram.setDest(new PhysicalAddress(3, 3, 7));
			telegram.setPriority(Priority.SYSTEM);
			telegram.setRepeated(true);
			telegram.setTransport(Transport.Connected);
			telegram.setData(new int[] { });
			telegram.setSequence(0);
			telegram.setApplication(Application.DeviceDescriptor_Read);

			busInterface.send(telegram);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
