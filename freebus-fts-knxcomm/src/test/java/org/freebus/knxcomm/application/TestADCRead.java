package org.freebus.knxcomm.application;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestADCRead
{
   @Test
   public final void testADCRead()
   {
      final ADCRead app = new ADCRead();

      assertEquals(ApplicationType.ADC_Read, app.getType());
      assertEquals(0, app.getChannel());
      assertEquals(1, app.getCount());
      assertNotNull(app.toString());
      assertNotNull(app.hashCode());
   }

   @Test
   public final void testADCReadIntInt()
   {
      final ADCRead app = new ADCRead(12, 7);

      assertEquals(ApplicationType.ADC_Read, app.getType());
      assertEquals(12, app.getChannel());
      assertEquals(7, app.getCount());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testADCReadInvalid()
   {
      new ADCRead(64, 7);
   }

   @Test
   public final void testGetSetChannel()
   {
      final ADCRead app = new ADCRead();

      app.setChannel(1);
      assertEquals(1, app.getChannel());

      app.setChannel(63);
      assertEquals(63, app.getChannel());

      app.setChannel(0);
      assertEquals(0, app.getChannel());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testGetSetChannelTooHigh()
   {
      final ADCRead app = new ADCRead();
      app.setChannel(64);
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testGetSetChannelTooLow()
   {
      final ADCRead app = new ADCRead();
      app.setChannel(-1);
   }

   @Test
   public final void testGetSetCount()
   {
      final ADCRead app = new ADCRead();

      app.setCount(1);
      assertEquals(1, app.getCount());

      app.setCount(88);
      assertEquals(88, app.getCount());
   }

   @Test
   public final void testFromRawData() throws InvalidDataException
   {
      final ADCRead app = new ADCRead();
      app.fromRawData(new int[] { 0xc6, 0x10 }, 0, 2);

      assertEquals(6, app.getChannel());
      assertEquals(0x10, app.getCount());
   }

   @Test
   public final void testToRawData()
   {
      final ADCRead app = new ADCRead(6, 16);
      final int[] rawData = new int[2];

      assertEquals(2, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x86, 0x10 }, rawData);
   }

   @Test
   public final void testEqualsObject()
   {
      final ADCRead app1 = new ADCRead(6, 16);
      final ADCRead app2 = new ADCRead(6, 16);

      assertTrue(app1.equals(app1));
      assertFalse(app1.equals(null));
      assertFalse(app1.equals(new Object()));
      assertTrue(app1.equals(app2));

      app1.setCount(17);
      assertFalse(app1.equals(app2));

      app1.setCount(16);
      app1.setChannel(4);
      assertFalse(app1.equals(app2));
   }
}
