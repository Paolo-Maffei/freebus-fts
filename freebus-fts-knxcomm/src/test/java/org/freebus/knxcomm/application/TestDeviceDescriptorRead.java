package org.freebus.knxcomm.application;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestDeviceDescriptorRead
{
   @Test
   public final void testDeviceDescriptorRead()
   {
      final DeviceDescriptorRead app = new DeviceDescriptorRead();

      assertEquals(ApplicationType.DeviceDescriptor_Read, app.getType());
      assertEquals(0, app.getDescriptorType());
      assertNotNull(app.hashCode());
      assertNotNull(app.toString());
   }

   @Test
   public final void testDeviceDescriptorReadIntIntArray()
   {
      final DeviceDescriptorRead app = new DeviceDescriptorRead(3);

      assertEquals(ApplicationType.DeviceDescriptor_Read, app.getType());
      assertEquals(3, app.getDescriptorType());
      assertNotNull(app.hashCode());
      assertNotNull(app.toString());
   }

   @Test
   public final void testGetSetDescriptorType()
   {
      final DeviceDescriptorRead app = new DeviceDescriptorRead();

      app.setDescriptorType(63);
      assertEquals(63, app.getDescriptorType());

      app.setDescriptorType(0);
      assertEquals(0, app.getDescriptorType());
   }

   @Test
   public final void testFromRawData() throws InvalidDataException
   {
      final DeviceDescriptorRead app = new DeviceDescriptorRead();
      final int[] rawData = new int[] { 0x07, 1, 8, 17 };

      app.fromRawData(rawData, 0, 4);
      assertEquals(ApplicationType.DeviceDescriptor_Read, app.getType());
      assertEquals(7, app.getDescriptorType());
   }

   @Test
   public final void testFromRawData2() throws InvalidDataException
   {
      final DeviceDescriptorRead app = new DeviceDescriptorRead();
      final int[] rawData = new int[] { 0x3f };

      app.fromRawData(rawData, 0, 1);
      assertEquals(ApplicationType.DeviceDescriptor_Read, app.getType());
      assertEquals(DeviceDescriptorRead.INVALID_DESCRIPTOR_TYPE, app.getDescriptorType());
   }

   @Test
   public final void testToRawData()
   {
      final DeviceDescriptorRead app = new DeviceDescriptorRead(7);
      final int[] rawData = new int[1];

      assertEquals(1, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x07 }, rawData);
   }

   @Test
   public final void testEqualsObject()
   {
      final DeviceDescriptorRead app1 = new DeviceDescriptorRead(8);
      final DeviceDescriptorRead app2 = new DeviceDescriptorRead(8);
      final DeviceDescriptorRead app3 = new DeviceDescriptorRead(5);

      assertFalse(app1.equals(null));
      assertFalse(app1.equals(new Object()));
      assertTrue(app1.equals(app1));
      assertTrue(app1.equals(app2));
      assertFalse(app1.equals(app3));

      app2.setDescriptorType(12);
      assertFalse(app1.equals(app2));
   }
}
