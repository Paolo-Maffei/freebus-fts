package org.freebus.knxcomm.emi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.freebus.fts.common.HexString;
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
}
