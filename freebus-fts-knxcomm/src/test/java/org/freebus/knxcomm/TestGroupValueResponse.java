package org.freebus.knxcomm;

import static org.junit.Assert.assertArrayEquals;

import org.freebus.fts.common.HexString;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.application.GroupValueResponse;
import org.junit.Test;

public class TestGroupValueResponse
{
   @Test
   public final void testToByteArray1()
   {
      final Application app = new GroupValueResponse(12);

      final byte[] expected = HexString.valueOf("00 4c");
      final byte[] rawData = app.toByteArray();

      assertArrayEquals(expected, rawData);
   }

   @Test
   public final void testToByteArray2()
   {
      final Application app = new GroupValueResponse();
      app.setApciValue(12);

      final byte[] expected = HexString.valueOf("00 4c");
      final byte[] rawData = app.toByteArray();

      assertArrayEquals(expected, rawData);
   }

   @Test
   public final void testToByteArray3()
   {
      final Application app = new GroupValueResponse(new int[] { 12 });

      final byte[] expected = HexString.valueOf("00 40 0c");
      final byte[] rawData = app.toByteArray();

      assertArrayEquals(expected, rawData);
   }
}
