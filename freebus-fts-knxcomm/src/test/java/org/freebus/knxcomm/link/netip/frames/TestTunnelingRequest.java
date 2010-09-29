package org.freebus.knxcomm.link.netip.frames;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.freebus.fts.common.HexString;
import org.freebus.knxcomm.emi.EmiTelegramFrame;
import org.freebus.knxcomm.link.netip.frames.FrameFactory;
import org.freebus.knxcomm.link.netip.frames.TunnelingRequest;
import org.freebus.knxcomm.link.netip.types.ServiceType;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramFactory;
import org.junit.Test;

public class TestTunnelingRequest
{
   @Test
   public final void testTunnelRequest()
   {
      final TunnelingRequest req = new TunnelingRequest();

      assertEquals(ServiceType.TUNNELING_REQUEST, req.getServiceType());
   }

   @Test
   public final void testFromToRawData() throws IOException
   {
      // Recv L_Data low from 1.1.6 to 1/0/10 hops: 06 T_DATA_XXX_REQ A_GroupValue_Write (small) 01
      final byte data[] = HexString.valueOf("06 10 04 20 00 15 04 01 00 00 29 00 3C E0 11 06 08 0A 01 00 81");

      // The same data, telegram only
      final byte telegramData[] = HexString.valueOf("BC 11 06 08 0A E1 00 81");

      final Telegram reqTelegram = TelegramFactory.createTelegram(telegramData);
      assertNotNull(reqTelegram);

      final TunnelingRequest req = (TunnelingRequest) FrameFactory.createFrame(data);
      assertNotNull(req);

      final EmiTelegramFrame frame = (EmiTelegramFrame) req.getFrame();
      assertNotNull(frame);

      assertEquals(reqTelegram.getDest(), frame.getTelegram().getDest());
      assertEquals(reqTelegram.getApplication(), frame.getTelegram().getApplication());

      final byte[] outData = req.toByteArray();
      assertArrayEquals(data, outData);
   }
}
