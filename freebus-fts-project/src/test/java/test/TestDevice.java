package test;

import junit.framework.TestCase;

import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Room;
import org.freebus.knxcomm.telegram.PhysicalAddress;

public class TestDevice extends TestCase
{

   public final void testDevice()
   {
      final Device device = new Device();
      assertNotNull(device);
      assertEquals(0, device.getId());
      assertEquals(0, device.getAddress());
      assertEquals(0, device.getVirtualDeviceId());
      assertNull(device.getLine());
      assertNull(device.getRoom());
   }

   public final void testGetSetId()
   {
      final Device device = new Device();

      device.setId(1234);
      assertEquals(1234, device.getId());

      device.setId(0);
      assertEquals(0, device.getId());
   }

   public final void testGetSetAddress()
   {
      final Device device = new Device();

      device.setAddress(123);
      assertEquals(123, device.getAddress());

      device.setAddress(0);
      assertEquals(0, device.getAddress());
   }

   public final void testGetPhysicalAddr()
   {
      final Device device = new Device();
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

   public final void testGetSetVirtualDeviceId()
   {
      final Device device = new Device();
      assertEquals(0, device.getVirtualDeviceId());

      device.setVirtualDeviceId(1234);
      assertEquals(1234, device.getVirtualDeviceId());

      device.setVirtualDeviceId(0);
      assertEquals(0, device.getVirtualDeviceId());
   }

   public final void testGetSetLine()
   {
      final Device device = new Device();
      final Line line = new Line();

      device.setLine(line);
      assertEquals(line, device.getLine());

      device.setLine(null);
      assertNull(device.getLine());
   }

   public final void testGetSetRoom()
   {
      final Device device = new Device();
      final Room room = new Room();

      device.setRoom(room);
      assertEquals(room, device.getRoom());

      device.setRoom(null);
      assertNull(device.getRoom());
   }
}
