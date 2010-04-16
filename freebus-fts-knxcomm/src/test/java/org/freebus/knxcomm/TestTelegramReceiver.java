package org.freebus.knxcomm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.freebus.knxcomm.internal.SimulatedBusInterface;
import org.junit.Test;

public class TestTelegramReceiver
{
   @Test
   public final void testTelegramReceiverBusInterface()
   {
      final SimulatedBusInterface busInterface = new SimulatedBusInterface();
      final TelegramReceiver recv = new TelegramReceiver(busInterface);

      assertEquals(busInterface, recv.getBusInterface());
      assertTrue(recv.isSystemBroadcasts());
      assertNotNull(recv.getDest());
      assertNull(recv.getApplicationType());
      assertFalse(recv.isConfirmations());
   }
}
