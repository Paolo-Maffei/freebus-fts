package org.freebus.knxcomm.application;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestADCResponse
{
   @Test
   public final void testADCResponse()
   {
      final ADCResponse app = new ADCResponse();

      assertEquals(ApplicationType.ADC_Response, app.getType());
      assertEquals(0, app.getChannel());
      assertEquals(1, app.getCount());
      assertEquals(0, app.getValue());
      assertNotNull(app.toString());
      assertNotNull(app.hashCode());
   }

   @Test
   public final void testADCResponseIntIntInt()
   {
      final ADCResponse app = new ADCResponse(1, 2, 3);

      assertEquals(ApplicationType.ADC_Response, app.getType());
      assertEquals(1, app.getChannel());
      assertEquals(2, app.getCount());
      assertEquals(3, app.getValue());
   }

   @Test
   public final void testGetSetValue()
   {
      final ADCResponse app = new ADCResponse();

      app.setValue(41);
      assertEquals(41, app.getValue());

      app.setValue(32767);
      assertEquals(32767, app.getValue());
   }

   @Test
   public final void testFromRawData() throws InvalidDataException
   {
      final ADCResponse app = new ADCResponse();
      app.fromRawData(new int[] { 0xc6, 0x10, 0x04, 0x05 }, 0, 4);

      assertEquals(6, app.getChannel());
      assertEquals(0x10, app.getCount());
      assertEquals(0x0405, app.getValue());
   }

   @Test
   public final void testToRawData()
   {
      final ADCResponse app = new ADCResponse(6, 0x10, 0x0405);
      final int[] rawData = new int[4];

      assertEquals(4, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0xc6, 0x10, 0x04, 0x05 }, rawData);
   }

   @Test
   public final void testEqualsObject()
   {
      final ADCResponse app1 = new ADCResponse(6, 16, 120);
      final ADCResponse app2 = new ADCResponse(6, 16, 120);

      assertTrue(app1.equals(app1));
      assertFalse(app1.equals(null));
      assertFalse(app1.equals(new Object()));
      assertTrue(app1.equals(app2));

      app1.setCount(17);
      assertFalse(app1.equals(app2));

      app1.setCount(16);
      app1.setChannel(4);
      assertFalse(app1.equals(app2));

      app1.setChannel(6);
      app1.setValue(70);
      assertFalse(app1.equals(app2));
   }
}