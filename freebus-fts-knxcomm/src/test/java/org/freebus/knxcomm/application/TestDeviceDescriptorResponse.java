package org.freebus.knxcomm.application;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestDeviceDescriptorResponse
{
   @Test
   public final void testDeviceDescriptorResponse()
   {
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse();

      assertEquals(ApplicationType.DeviceDescriptor_Response, app.getType());
      assertEquals(0, app.getDescriptorType());
      assertNull(app.getDescriptor());
      assertNotNull(app.hashCode());
      assertNotNull(app.toString());
   }

   @Test
   public final void testDeviceDescriptorResponseIntIntArray()
   {
      final int[] descr = new int[] { 4, 7, 12 };
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse(3, descr);

      assertEquals(ApplicationType.DeviceDescriptor_Response, app.getType());
      assertEquals(3, app.getDescriptorType());
      assertFalse(descr == app.getDescriptor());
      assertArrayEquals(descr, app.getDescriptor());
      assertNotNull(app.hashCode());
      assertNotNull(app.toString());
   }

   @Test
   public final void testGetSetDescriptor()
   {
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse();

      app.setDescriptor(new int[] { 7, 4, 1 });
      assertArrayEquals(new int[] { 7, 4, 1 }, app.getDescriptor());

      app.setDescriptor(null);
      assertNull(app.getDescriptor());
   }

   @Test
   public final void testGetSetDescriptorType()
   {
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse();

      app.setDescriptorType(63);
      assertEquals(63, app.getDescriptorType());

      app.setDescriptorType(0);
      assertEquals(0, app.getDescriptorType());
   }

   @Test
   public final void testFromRawData() throws InvalidDataException
   {
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse();
      final int[] rawData = new int[] { 0x48, 1, 8, 17 };

      app.fromRawData(rawData, 0, 4);
      assertEquals(ApplicationType.DeviceDescriptor_Response, app.getType());
      assertEquals(8, app.getDescriptorType());
      assertArrayEquals(new int[] { 1, 8, 17 }, app.getDescriptor());
   }

   @Test
   public final void testFromRawData2() throws InvalidDataException
   {
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse();
      final int[] rawData = new int[] { 0x7f };

      app.fromRawData(rawData, 0, 1);
      assertEquals(ApplicationType.DeviceDescriptor_Response, app.getType());
      assertEquals(DeviceDescriptorRead.INVALID_DESCRIPTOR_TYPE, app.getDescriptorType());
      assertNull(app.getDescriptor());
   }

   @Test
   public final void testToRawData()
   {
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse(8, new int[] { 1, 8, 17 });
      final int[] rawData = new int[4];

      assertEquals(4, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x48, 1, 8, 17 }, rawData);
   }

   @Test
   public final void testEqualsObject()
   {
      final DeviceDescriptorResponse app1 = new DeviceDescriptorResponse(8, new int[] { 1, 8, 17 });
      final DeviceDescriptorResponse app2 = new DeviceDescriptorResponse(8, new int[] { 1, 8, 17 });
      final DeviceDescriptorResponse app3 = new DeviceDescriptorResponse(8, new int[] { 2, 6 });

      assertFalse(app1.equals(null));
      assertFalse(app1.equals(new Object()));
      assertTrue(app1.equals(app1));
      assertTrue(app1.equals(app2));
      assertFalse(app1.equals(app3));

      app2.setDescriptor(null);
      assertFalse(app1.equals(app2));
      assertFalse(app2.equals(app1));

      app1.setDescriptor(null);
      assertTrue(app1.equals(app2));

      app1.setDescriptor(new int[] { 1, 3, 7 });
      assertFalse(app1.equals(app2));

      app2.setDescriptorType(12);
      assertFalse(app1.equals(app2));
   }
}
