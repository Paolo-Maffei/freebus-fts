package test;

import static org.junit.Assert.*;

import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Room;
import org.freebus.knxcomm.telegram.PhysicalAddress;
import org.junit.Test;

public class TestDevice
{
   @Test
   public final void testDevice()
   {
      final Device device = new Device();
      assertNotNull(device);
      assertEquals(0, device.getId());
      assertEquals(0, device.getAddress());
      assertNull(device.getVirtualDevice());
      assertNull(device.getLine());
      assertNull(device.getRoom());
   }

   @Test
   public final void testGetSetId()
   {
      final Device device = new Device();

      device.setId(1234);
      assertEquals(1234, device.getId());

      device.setId(0);
      assertEquals(0, device.getId());
   }

   @Test
   public final void testGetSetAddress()
   {
      final Device device = new Device(33, null);

      device.setAddress(123);
      assertEquals(123, device.getAddress());

      device.setAddress(0);
      assertEquals(0, device.getAddress());
   }

   @Test
   public final void testGetPhysicalAddr()
   {
      final Device device = new Device(null);
      device.setAddress(12);
      assertEquals(PhysicalAddress.NULL, device.getPhysicalAddress());

      final Line line = new Line();
      line.setAddress(9);
      line.add(device);
      assertEquals(PhysicalAddress.NULL, device.getPhysicalAddress());

      final Area area = new Area();
      area.setAddress(7);
      area.add(line);
      assertEquals(new PhysicalAddress(7, 9, 12), device.getPhysicalAddress());
   }

   @Test
   public final void testGetSetVirtualDevice()
   {
      final Device device = new Device();
      final VirtualDevice virtualDevice = new VirtualDevice();

      device.setVirtualDevice(virtualDevice);
      assertEquals(virtualDevice, device.getVirtualDevice());

      device.setVirtualDevice(null);
      assertNull(device.getVirtualDevice());
   }

   @Test
   public final void testGetSetLine()
   {
      final Device device = new Device();
      final Line line = new Line();

      device.setLine(line);
      assertEquals(line, device.getLine());

      device.setLine(null);
      assertNull(device.getLine());
   }

   @Test
   public final void testGetSetRoom()
   {
      final Device device = new Device();
      final Room room = new Room();

      device.setRoom(room);
      assertEquals(room, device.getRoom());

      device.setRoom(null);
      assertNull(device.getRoom());
   }

   @Test
   public final void testEquals()
   {
      final Device device1 = new Device(1, new VirtualDevice());
      final Device device2 = new Device(1, new VirtualDevice());
      final Device device3 = new Device(2, null);

      assertTrue(device1.equals(device1));
      assertTrue(device1.equals(device2));
      assertFalse(device1.equals(null));
      assertFalse(device1.equals(device3));
      assertFalse(device1.equals(new Object()));
   }

   @Test
   public final void testToString()
   {
      final Device device = new Device();
      assertNotNull(device.toString());
   }
}
