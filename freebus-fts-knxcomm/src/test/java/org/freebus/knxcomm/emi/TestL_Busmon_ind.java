package org.freebus.knxcomm.emi;

import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestL_Busmon_ind
{

   @Test
   public final void testFromRawDataNack() throws InvalidDataException
   {
      final L_Busmon_ind frame = new L_Busmon_ind();
      final int[] data = new int[] { 0x2b, 0x01, 0x22, 0x33, 0x90, 0x11, 0x06, 0x11, 0xff, 0x60, 0xcb, 0x3d };
      frame.fromRawData(data, 0);
   }
}
