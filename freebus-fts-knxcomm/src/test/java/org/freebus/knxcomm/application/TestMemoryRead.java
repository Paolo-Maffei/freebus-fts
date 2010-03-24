package org.freebus.knxcomm.application;

import static org.junit.Assert.*;

import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestMemoryRead
{
   @Test
   public final void testMemoryRead()
   {
      final MemoryRead app = new MemoryRead();

      assertEquals(ApplicationType.Memory_Read, app.getType());
      assertEquals(0, app.getAddress());
      assertEquals(0, app.getCount());
      assertNotNull(app.toString());
      assertNotNull(app.hashCode());
   }

   @Test
   public final void testMemoryReadIntInt()
   {
      final MemoryRead app = new MemoryRead(12345, 17);
      assertEquals(12345, app.getAddress());
      assertEquals(17, app.getCount());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testMemoryReadIntIntTooSmall()
   {
      new MemoryRead(12345, -1);
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testMemoryReadIntIntTooLarge()
   {
      new MemoryRead(12345, 64);
   }

   @Test
   public final void testEqualsObject()
   {
      final MemoryRead app1 = new MemoryRead(12345, 5);
      final MemoryRead app2 = new MemoryRead(12345, 5);

      assertFalse(app1.equals(null));
      assertFalse(app1.equals(new Object()));
      assertTrue(app1.equals(app1));
      assertTrue(app1.equals(app2));

      app2.setCount(7);
      assertFalse(app1.equals(app2));

      app2.setAddress(0);
      assertFalse(app1.equals(app2));
   }

   @Test
   public final void testGetSetCount()
   {
      final MemoryRead app = new MemoryRead();

      app.setCount(41);
      assertEquals(41, app.getCount());

      app.setCount(0);
      assertEquals(0, app.getCount());

      app.setCount(63);
      assertEquals(63, app.getCount());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testGetSetCountTooSmall()
   {
      final MemoryRead app = new MemoryRead();
      app.setCount(-1);
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testGetSetCountTooLarge()
   {
      final MemoryRead app = new MemoryRead();
      app.setCount(64);
   }

   @Test
   public final void testFromRawData() throws InvalidDataException
   {
      final MemoryRead app = new MemoryRead();
      app.fromRawData(new int[] { 0x03, 0x10, 0x20 }, 0, 3);

      assertEquals(3, app.getCount());
      assertEquals(0x1020, app.getAddress());
   }

   @Test
   public final void testToRawData()
   {
      final MemoryRead app = new MemoryRead(0x1020, 3);
      final int[] rawData = new int[3];

      assertEquals(3, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x03, 0x10, 0x20 }, rawData);
   }

   @Test
   public final void testGetSetAddress()
   {
      final MemoryRead app = new MemoryRead();

      app.setAddress(1704);
      assertEquals(1704, app.getAddress());

      app.setAddress(0);
      assertEquals(0, app.getAddress());
   }
}
