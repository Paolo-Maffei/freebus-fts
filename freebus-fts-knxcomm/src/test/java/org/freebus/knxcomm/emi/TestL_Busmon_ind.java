package org.freebus.knxcomm.emi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.freebus.fts.common.HexString;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.GroupValueWrite;
import org.freebus.knxcomm.telegram.Telegram;
import org.junit.Test;

public class TestL_Busmon_ind
{
   @Test
   public final void testReadDataNack() throws IOException
   {
      final byte[] data = HexString.valueOf("2b 01 22 33 90 11 06 11 ff 60 cb 3d");

      final L_Busmon_ind frame = (L_Busmon_ind) EmiFrameFactory.createFrame(data);
      assertNotNull(frame);

      assertEquals(1, frame.getStatus());
      assertEquals(0x2233, frame.getTimestamp());
   }

   @Test
   public final void testGroupValueWrite() throws IOException
   {
      final byte[] data = HexString.valueOf("2b 01 22 33 bc 11 0c 08 0a e1 00 81 3c");

      final L_Busmon_ind frame = (L_Busmon_ind) EmiFrameFactory.createFrame(data);
      assertNotNull(frame);

      final Telegram telegram = frame.getTelegram();
      assertNotNull(telegram);
      assertEquals(ApplicationType.GroupValue_Write, telegram.getApplicationType());

      final GroupValueWrite app = (GroupValueWrite) telegram.getApplication();
      assertNotNull(app);

      assertEquals(1, app.getApciValue());
      
   }
}
