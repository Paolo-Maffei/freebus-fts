package org.freebus.knxcomm.application;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestMemoryResponse
{
   @Test
   public final void testMemoryResponse()
   {
      final MemoryResponse app = new MemoryResponse();

      assertEquals(ApplicationType.Memory_Response, app.getType());
      assertEquals(0, app.getAddress());
      assertEquals(0, app.getCount());
      assertNull(app.getData());
      assertNotNull(app.toString());
      assertNotNull(app.hashCode());
   }

   @Test
   public final void testMemoryResponseIntIntArray()
   {
      final int data[] = new int[] { 1, 2, 3 };
      final MemoryResponse app = new MemoryResponse(1230, data);

      assertNotNull(app.toString());
      assertNotNull(app.hashCode());

      assertEquals(1230, app.getAddress());
      assertEquals(3, app.getCount());
      assertArrayEquals(new int[] { 1, 2, 3 }, app.getData());

      data[0] = 0;
      assertArrayEquals(new int[] { 1, 2, 3 }, app.getData());

      new MemoryResponse(10900, null);
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testMemoryResponseIntIntArrayTooLarge()
   {
      new MemoryResponse(4000, new int[64]);
   }

   @Test
   public final void testEqualsObject()
   {
      final MemoryResponse app1 = new MemoryResponse(1230, new int[] { 1, 2, 3 });
      final MemoryResponse app2 = new MemoryResponse(1230, new int[] { 1, 2, 3 });

      assertFalse(app1.equals(null));
      assertFalse(app1.equals(new Object()));
      assertTrue(app1.equals(app1));
      assertTrue(app1.equals(app2));

      app2.setData(new int[] { 1, 2, 3, 4 });
      assertFalse(app1.equals(app2));

      app1.setData(null);
      assertFalse(app1.equals(app2));

      app1.setData(new int[] { 1, 2, 3, 4 });
      app2.setData(null);
      assertFalse(app1.equals(app2));

      app1.setData(null);
      assertTrue(app1.equals(app2));

      app2.setAddress(0);
      assertFalse(app1.equals(app2));
   }

   @Test
   public final void testGetSetData()
   {
      final int data[] = new int[] { 1, 2, 3 };
      final MemoryResponse app = new MemoryResponse();

      app.setData(data);
      assertEquals(3, app.getCount());
      assertArrayEquals(new int[] { 1, 2, 3 }, app.getData());

      data[0] = 0;
      assertArrayEquals(new int[] { 1, 2, 3 }, app.getData());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testSetDataTooLarge()
   {
      final MemoryResponse app = new MemoryResponse();
      app.setData(new int[64]);
   }

   @Test
   public final void testFromRawData() throws InvalidDataException
   {
      final MemoryResponse app = new MemoryResponse();
      app.fromRawData(new int[] { 0x43, 0x10, 0x20, 0x11, 0x12, 0x13, 0x14 }, 0, 7);

      assertEquals(ApplicationType.Memory_Response, app.getType());
      assertEquals(3, app.getCount());
      assertEquals(0x1020, app.getAddress());
      assertArrayEquals(new int[] { 0x11, 0x12, 0x13 }, app.getData());
   }

   @Test
   public final void testToRawData()
   {
      final MemoryResponse app = new MemoryResponse(0x1020, new int[] { 0x11, 0x12, 0x13 });
      final int[] rawData = new int[6];

      assertEquals(6, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x43, 0x10, 0x20, 0x11, 0x12, 0x13 }, rawData);
   }
}
