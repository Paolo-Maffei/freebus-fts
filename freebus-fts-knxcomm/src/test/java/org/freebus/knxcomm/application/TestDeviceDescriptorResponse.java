package org.freebus.knxcomm.application;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor0;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor2;
import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestDeviceDescriptorResponse
{
   @Test
   public final void testDeviceDescriptorResponse()
   {
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse();

      assertEquals(ApplicationType.DeviceDescriptor_Response, app.getType());
      assertEquals(DeviceDescriptor0.NULL, app.getDescriptor());
      assertNotNull(app.hashCode());
      assertNotNull(app.toString());
   }

   @Test
   public final void testDeviceDescriptorResponseIntIntArray()
   {
      final DeviceDescriptor desc = new DeviceDescriptor2();
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse(desc);

      assertEquals(ApplicationType.DeviceDescriptor_Response, app.getType());
      assertEquals(desc, app.getDescriptor());
      assertNotNull(app.hashCode());
      assertNotNull(app.toString());
   }

   @Test
   public final void testGetSetDescriptor()
   {
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse();
      final DeviceDescriptor desc = new DeviceDescriptor2();

      app.setDescriptor(desc);
      assertEquals(desc, app.getDescriptor());

      app.setDescriptor(null);
      assertNull(app.getDescriptor());
   }

   @Test
   public final void testFromRawData() throws InvalidDataException
   {
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse();
      final int[] rawData = new int[] { 0x40, 1, 8 };

      app.fromRawData(rawData, 0, 3);
      assertEquals(ApplicationType.DeviceDescriptor_Response, app.getType());
      assertEquals(0, app.getDescriptorType());
      assertEquals(DeviceDescriptor0.class, app.getDescriptor().getClass());
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
   public final void testFromRawData3() throws InvalidDataException
   {
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse();
      final int[] rawData = new int[] { 0x29, 0x90, 0x33, 0x07, 0, 0, 0x63, 0x43, 0x40, 0x00, 0x12 };

      app.fromRawData(rawData, 8, 3);
      assertEquals(ApplicationType.DeviceDescriptor_Response, app.getType());
      assertEquals(0, app.getDescriptorType());
      DeviceDescriptor0 desc = (DeviceDescriptor0) app.getDescriptor();
      assertEquals(0x12, desc.getMaskVersion());
   }

   @Test
   public final void testToRawData()
   {
      final DeviceDescriptorResponse app = new DeviceDescriptorResponse(new DeviceDescriptor0(0x1234));
      final int[] rawData = new int[3];

      assertEquals(3, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x40, 0x12, 0x34 }, rawData);
   }

   @Test
   public final void testEqualsObject()
   {
      final DeviceDescriptorResponse app1 = new DeviceDescriptorResponse(new DeviceDescriptor0(123));
      final DeviceDescriptorResponse app2 = new DeviceDescriptorResponse(new DeviceDescriptor0(123));
      final DeviceDescriptorResponse app3 = new DeviceDescriptorResponse(new DeviceDescriptor0(456));

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

      app1.setDescriptor(new DeviceDescriptor0(123));
      assertFalse(app1.equals(app2));

      app2.setDescriptor(new DeviceDescriptor0(456));
      assertFalse(app1.equals(app2));
   }
}
