package org.freebus.knxcommtester;

import java.io.IOException;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

public class TestAplicationClass {
	

	


	public static void main(String[] args) throws Exception {

		Transmit a =new Transmit();
		a.requestest();
	}

	
}
