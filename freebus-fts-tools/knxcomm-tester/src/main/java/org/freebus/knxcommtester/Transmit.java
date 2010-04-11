package org.freebus.knxcommtester;

import java.io.IOException;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.application.MemoryRead;
import org.freebus.knxcomm.applicationData.DeviceDescriptorProperties;
import org.freebus.knxcomm.applicationData.DeviceDescriptorPropertiesFactory;
import org.freebus.knxcomm.applicationData.MemoryAddress;
import org.freebus.knxcomm.applicationData.MemoryAddressMapper;
import org.freebus.knxcomm.applicationData.MemoryAddressTypes;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;


	class Transmit implements TelegramListener {
		MemoryAddressMapper memoryAddressMapper;
		DeviceDescriptorProperties deviceDescriptorProperties;
		Telegram telegram ;
		private  BusInterface busInterface;
		// private static Logger logger = Logger.getRootLogger();
		private  String ftscomComport;
private DeviceDescriptorResponse deviceDescriptorResponse ;
		private  Config cfg;

		public void requestest() throws Exception {
			cfg = new Config();
			ftscomComport = cfg.get("comport");
			busInterface = BusInterfaceFactory
					.newSerialInterface(ftscomComport);

			busInterface.addListener(this);
			busInterface.open();

			 telegram = new Telegram();
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
			telegram.setSequence(0);
			telegram.setApplication(ApplicationType.DeviceDescriptor_Read);
			busInterface.send(telegram);

		}
		public void readmanData() throws IOException{
			telegram.setFrom(new PhysicalAddress(0, 0, 0));
			telegram.setDest(new PhysicalAddress(3, 3, 7));
			telegram.setPriority(Priority.SYSTEM);
			telegram.setRepeated(true);
			telegram.setTransport(Transport.Connected);
			telegram.setSequence(7);
			telegram.setApplication(ApplicationType.Memory_Read);
			MemoryAddress memoryAddress =memoryAddressMapper.getMemoryAddress(MemoryAddressTypes.SystemState);
			telegram.setApplication(new MemoryRead(memoryAddress));
			busInterface.send(telegram);
		}
		@Override
		public void telegramReceived(Telegram telegram) {
			try {
			if (telegram.getApplicationType() == ApplicationType.DeviceDescriptor_Response){

				 deviceDescriptorResponse = (DeviceDescriptorResponse) telegram.getApplication();

			DeviceDescriptorPropertiesFactory deviceDescriptorPropertiesFactory = new DeviceDescriptorPropertiesFactory();

			deviceDescriptorProperties=	deviceDescriptorPropertiesFactory.getDeviceDescriptor(deviceDescriptorResponse);



			memoryAddressMapper = deviceDescriptorProperties.getMemoryAddressMapper();
			readmanData();
			}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void telegramSent(Telegram telegram) {
			// TODO Auto-generated method stub

		}

                @Override
                public void telegramSendConfirmed(Telegram telegram) {
                        // TODO Auto-generated method stub

                }
}
