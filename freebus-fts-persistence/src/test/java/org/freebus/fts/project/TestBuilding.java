package org.freebus.fts.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class TestBuilding
{
   @Test
   public final void testBuilding()
   {
      final Building building = new Building();
      assertNotNull(building);
      assertEquals(0, building.getId());
      assertNull(building.getProject());
      assertNotNull(building.getRooms());
      assertTrue(building.getRooms().isEmpty());
      assertNotNull(building.hashCode());
      assertNotNull(building.toString());
   }

   @Test
   public final void testGetSetId()
   {
      final Building building = new Building();

      building.setId(1234);
      assertEquals(1234, building.getId());

      building.setId(4567);
      assertEquals(4567, building.getId());

      building.setId(0);
      assertEquals(0, building.getId());
   }

   @Test
   public final void testGetSetProject()
   {
      final Building building = new Building();
      assertNull(building.getProject());

      final Project project = new Project();
      building.setProject(project);
      assertEquals(project, building.getProject());

      building.setProject(null);
      assertNull(building.getProject());
   }

   @Test
   public final void testGetSetName()
   {
      final Building building = new Building();

      building.setName("building-1");
      assertEquals("building-1", building.getName());

      building.setName("");
      assertEquals("", building.getName());

      building.setName("building-2");
      assertEquals("building-2", building.getName());
   }

   @Test
   public final void testGetSetDescription()
   {
      final Building building = new Building();

      building.setDescription("building-desc-1");
      assertEquals("building-desc-1", building.getDescription());

      building.setDescription("");
      assertEquals("", building.getDescription());

      building.setDescription("building-desc-2");
      assertEquals("building-desc-2", building.getDescription());
   }

   @Test
   public final void testGetSetRooms()
   {
      final Building building = new Building();
      final Set<Room> newRooms = new TreeSet<Room>();

      building.setRooms(newRooms);
      assertEquals(newRooms, building.getRooms());
   }

   @Test
   public final void testAdd()
   {
      final Building building = new Building();
      assertTrue(building.getRooms().isEmpty());

      final Room room = new Room();
      room.setId(1);
      building.add(room);
      assertEquals(1, building.getRooms().size());
      assertEquals(room, building.getRooms().iterator().next());

      building.add(new Room());
      assertEquals(2, building.getRooms().size());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testAddTwice()
   {
      final Building building = new Building();
      final Room room = new Room();
      building.add(room);
      building.add(room);
   }

   @Test
   public final void testEquals()
   {
      final Building building1 = new Building();
      final Building building2 = new Building();

      assertFalse(building1.equals(null));
      assertFalse(building1.equals(new Object()));
      assertTrue(building1.equals(building1));

      assertTrue(building1.equals(building2));
      assertTrue(building2.equals(building1));

      building1.add(new Room());
      assertFalse(building1.equals(building2));
      assertFalse(building2.equals(building1));

      building2.add(new Room());
      assertTrue(building1.equals(building2));
      assertTrue(building2.equals(building1));

      building1.setDescription("desc-1");
      building2.setDescription("desc-2");
      assertFalse(building1.equals(building2));
      assertFalse(building2.equals(building1));
   }
}
