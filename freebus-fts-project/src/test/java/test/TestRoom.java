package test;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Room;

public class TestRoom extends TestCase
{

   public final void testRoom()
   {
      final Room room = new Room();
      assertNotNull(room);
      assertEquals(0, room.getId());
      assertNull(room.getBuilding());
      assertNotNull(room.getDevices());
      assertTrue(room.getDevices().isEmpty());
   }

   public final void testGetSetId()
   {
      final Room room = new Room();

      room.setId(1234);
      assertEquals(1234, room.getId());

      room.setId(4567);
      assertEquals(4567, room.getId());

      room.setId(0);
      assertEquals(0, room.getId());
   }

   public final void testGetSetBuilding()
   {
      final Room room = new Room();
      assertNull(room.getBuilding());

      final Building building = new Building();
      room.setBuilding(building);
      assertEquals(building, room.getBuilding());

      room.setBuilding(null);
      assertNull(room.getBuilding());
   }

   public final void testGetSetName()
   {
      final Room room = new Room();

      room.setName("room-1");
      assertEquals("room-1", room.getName());

      room.setName("");
      assertEquals("", room.getName());

      room.setName("room-2");
      assertEquals("room-2", room.getName());
   }

   public final void testGetSetDescription()
   {
      final Room room = new Room();

      room.setDescription("room-desc-1");
      assertEquals("room-desc-1", room.getDescription());

      room.setDescription("");
      assertEquals("", room.getDescription());

      room.setDescription("room-desc-2");
      assertEquals("room-desc-2", room.getDescription());
   }

   public final void testGetSetDevices()
   {
      final Room room = new Room();
      final Set<Device> newDevices = new HashSet<Device>();

      room.setDevices(newDevices);
      assertEquals(newDevices, room.getDevices());
   }

   public final void testAdd()
   {
      final Room room = new Room();
      assertTrue(room.getDevices().isEmpty());

      final Device device = new Device();
      room.add(device);
      assertEquals(1, room.getDevices().size());
      assertEquals(device, room.getDevices().iterator().next());

      room.add(device);
      assertEquals(1, room.getDevices().size());

      room.add(new Device());
      assertEquals(2, room.getDevices().size());
   }
}
