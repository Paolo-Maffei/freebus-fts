package org.freebus.knxcommtester;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.telegram.ApplicationType;
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
			telegram.setDest(new PhysicalAddress(3, 3, 7));
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
			telegram.setApplication(ApplicationType.DeviceDescriptor_Read);

			
			busInterface.send(telegram);
			telegram.setData(new int[] {1,1,4 });
			telegram.setSequence(1);
			telegram.setApplication(ApplicationType.Memory_Read);
			busInterface.send(telegram);
			
			
			telegram.setData(new int[] {1,8 });
			telegram.setSequence(2);
			telegram.setApplication(ApplicationType.ADC_Read);
			busInterface.send(telegram);
			
			
			telegram.setData(new int[] {1,0,0x60 });
			telegram.setSequence(3);
			telegram.setApplication(ApplicationType.Memory_Read);
			busInterface.send(telegram);
			
			telegram.setData(new int[] {1,1,0x0D });
			telegram.setSequence(4);
			telegram.setApplication(ApplicationType.Memory_Read);
			busInterface.send(telegram);
			
			telegram.setData(new int[] {0x84,8 });
			telegram.setSequence(5);
			telegram.setApplication(ApplicationType.ADC_Read);
			busInterface.send(telegram);
			
			telegram.setData(new int[] {4,1,4 });
			telegram.setSequence(6);
			telegram.setApplication(ApplicationType.Memory_Read);
			busInterface.send(telegram);
			
			telegram.setData(new int[] {1,0,0x60 });
			telegram.setSequence(7);
			telegram.setApplication(ApplicationType.Memory_Read);
			busInterface.send(telegram);
			
			telegram.setData(new int[] {1,1,9 });
			telegram.setSequence(8);
			telegram.setApplication(ApplicationType.Memory_Read);
			busInterface.send(telegram);
			busInterface.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
