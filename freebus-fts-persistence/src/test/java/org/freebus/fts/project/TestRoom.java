package org.freebus.fts.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Vector;

import org.junit.Test;

public class TestRoom
{
   @Test
   public final void testRoom()
   {
      final Room room = new Room();
      assertNotNull(room);
      assertEquals(0, room.getId());
      assertNull(room.getBuilding());
      assertNotNull(room.getDevices());
      assertTrue(room.getDevices().isEmpty());
      assertNotNull(room.hashCode());
      assertNotNull(room.toString());
   }

   @Test
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

   @Test
   public final void testGetSetNumber()
   {
      final Room room = new Room();
      assertEquals("", room.getNumber());

      room.setNumber("12.34");
      assertEquals("12.34", room.getNumber());

      room.setNumber("45-67");
      assertEquals("45-67", room.getNumber());

      room.setNumber("");
      assertEquals("", room.getNumber());

      room.setNumber("");
      assertEquals("", room.getNumber());

      room.setNumber(null);
      assertEquals("", room.getNumber());
   }

   @Test
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

   @Test
   public final void testGetSetName()
   {
      final Room room = new Room();

      room.setName("room-1");
      assertEquals("room-1", room.getName());

      room.setName("");
      assertEquals("", room.getName());

      room.setName(null);
      assertEquals("", room.getName());

      room.setName("room-2");
      assertEquals("room-2", room.getName());
   }

   @Test
   public final void testGetSetDescription()
   {
      final Room room = new Room();

      room.setDescription("room-desc-1");
      assertEquals("room-desc-1", room.getDescription());

      room.setDescription("");
      assertEquals("", room.getDescription());

      room.setDescription(null);
      assertEquals("", room.getDescription());

      room.setDescription("room-desc-2");
      assertEquals("room-desc-2", room.getDescription());
   }

   @Test
   public final void testGetSetDevices()
   {
      final Room room = new Room();
      final List<Device> newDevices = new Vector<Device>();

      room.setDevices(newDevices);
      assertEquals(newDevices, room.getDevices());
   }

   @Test
   public final void testAddRemove()
   {
      final Room room = new Room();
      assertTrue(room.getDevices().isEmpty());

      final Device device = new Device(1, null, null);
      room.add(device);
      assertNotNull(device.getRoom());
      assertEquals(1, room.getDevices().size());
      assertEquals(device, room.getDevices().iterator().next());

      room.add(new Device(2, null, null));
      assertEquals(2, room.getDevices().size());

      room.remove(device);
      assertNull(device.getRoom());
      assertEquals(1, room.getDevices().size());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testAddTwice()
   {
      final Room room = new Room();
      final Device device = new Device();
      room.add(device);
      room.add(device);
   }

   @Test
   public final void testEquals()
   {
      final Room room1 = new Room();
      final Room room2 = new Room();

      assertFalse(room1.equals(null));
      assertFalse(room1.equals(new Object()));
      assertTrue(room1.equals(room1));

      assertTrue(room1.equals(room2));
      assertTrue(room2.equals(room1));

      room1.add(new Device());
      assertFalse(room1.equals(room2));
      assertFalse(room2.equals(room1));

      room2.add(new Device());
      assertTrue(room1.equals(room2));
      assertTrue(room2.equals(room1));

      room1.setDescription("desc-1");
      room2.setDescription("desc-2");
      assertFalse(room1.equals(room2));
      assertFalse(room2.equals(room1));
   }
}
