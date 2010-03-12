package org.freebus.fts.project;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.freebus.fts.project.Building;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.Room;

public class TestBuilding extends TestCase
{

   public final void testBuilding()
   {
      final Building building = new Building();
      assertNotNull(building);
      assertEquals(0, building.getId());
      assertNull(building.getProject());
      assertNotNull(building.getRooms());
      assertTrue(building.getRooms().isEmpty());
   }

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

   public final void testGetSetRooms()
   {
      final Building building = new Building();
      final Set<Room> newRooms = new HashSet<Room>();

      building.setRooms(newRooms);
      assertEquals(newRooms, building.getRooms());
   }

   public final void testAdd()
   {
      final Building building = new Building();
      assertTrue(building.getRooms().isEmpty());

      final Room room = new Room();
      building.add(room);
      assertEquals(1, building.getRooms().size());
      assertEquals(room, building.getRooms().iterator().next());

      building.add(room);
      assertEquals(1, building.getRooms().size());

      building.add(new Room());
      assertEquals(2, building.getRooms().size());
   }
}
