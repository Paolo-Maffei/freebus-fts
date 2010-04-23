package org.freebus.knxcomm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.freebus.fts.common.HexString;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.internal.SimulatedBusInterface;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramFactory;
import org.freebus.knxcomm.telegram.TelegramReceiver;
import org.junit.Test;

public class TestTelegramReceiver
{
   @Test
   public final void testTelegramReceiverBusInterface()
   {
      final SimulatedBusInterface busInterface = new SimulatedBusInterface();
      final TelegramReceiver recv = new TelegramReceiver(busInterface);

      assertEquals(busInterface, recv.getBusInterface());
      assertNotNull(recv.getDest());
      assertNull(recv.getApplicationType());
      assertFalse(recv.isConfirmations());
   }

   @Test
   public final void testFilter()
   {
      final SimulatedBusInterface busInterface = new SimulatedBusInterface();
      final TelegramReceiver recv = new TelegramReceiver(busInterface);

      final Telegram telegram = new Telegram();
      telegram.setDest(busInterface.getPhysicalAddress());

      assertTrue(recv.filter(telegram, false));
      assertFalse(recv.filter(telegram, true));

      telegram.setDest(GroupAddress.BROADCAST);
      assertFalse(recv.filter(telegram, false));

      recv.setDest(GroupAddress.BROADCAST);
      assertTrue(recv.filter(telegram, false));
   }

   @Test
   public final void testFilter2() throws IOException
   {
      final SimulatedBusInterface busInterface = new SimulatedBusInterface();

      final TelegramReceiver recv = new TelegramReceiver(busInterface);
      recv.setApplicationType(ApplicationType.IndividualAddress_Response);
      recv.setDest(GroupAddress.BROADCAST);

      final byte[] data = HexString.valueOf("b0 11 12 00 00 e1 01 40 ec");
      final Telegram telegram = TelegramFactory.createTelegram(data);

      assertTrue(recv.filter(telegram, false));

      recv.clear();
      recv.processTelegram(telegram, false);
      assertEquals(telegram, recv.receive(10));
      assertNull(recv.receive(10));

      recv.clear();
      recv.processTelegram(telegram, false);
      assertEquals(1, recv.receiveMultiple(0).size());
   }
}
