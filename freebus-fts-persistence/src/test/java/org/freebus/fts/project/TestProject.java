package org.freebus.fts.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class TestProject
{
   @Test
   public final void testProject()
   {
      final Project project = new Project();
      assertNotNull(project);
      assertEquals(0, project.getId());
      assertNotNull(project.getAreas());
      assertNotNull(project.getBuildings());
      assertNotNull(project.getMainGroups());
      assertNotNull(project.toString());
      assertNotNull(project.hashCode());
   }

   @Test
   public final void testAddArea()
   {
      final Project project = new Project();

      final Area area1 = new Area(1);
      area1.setAddress(20);
      project.add(area1);
      assertEquals(project, area1.getProject());

      final Area area2 = new Area(2);
      area2.setAddress(10);
      project.add(area2);
      assertEquals(project, area2.getProject());

      assertEquals(2, project.getAreas().size());
      assertEquals(area2, project.getAreas().iterator().next());
   }

   @Test
   public final void testGetSetId()
   {
      final Project project = new Project();
      assertNotNull(project);
      assertEquals(0, project.getId());

      project.setId(1234);
      assertEquals(1234, project.getId());

      project.setId(4567);
      assertEquals(4567, project.getId());
   }

   @Test
   public final void testGetSetName()
   {
      final Project project = new Project();
      assertNotNull(project);

      project.setName("project-1");
      assertEquals("project-1", project.getName());

      project.setName("");
      assertEquals("", project.getName());
   }

   @Test
   public final void testGetSetDescription()
   {
      final Project project = new Project();
      assertNotNull(project);

      project.setDescription("project-desc-1");
      assertEquals("project-desc-1", project.getDescription());

      project.setDescription("");
      assertEquals("", project.getDescription());
   }

   @Test
   public final void testGetSetLastModified()
   {
      final Project project = new Project();
      assertNotNull(project);

      final Date date1 = new Date(1262900127);
      project.setLastModified(date1);
      assertEquals(date1, project.getLastModified());
   }

   @Test
   public final void testGetSetAreas()
   {
      final Project project = new Project();
      assertNotNull(project);

      Collection<Area> areas = project.getAreas();
      assertNotNull(areas);
      assertTrue(areas.isEmpty());

      TreeSet<Area> newAreas = new TreeSet<Area>();
      newAreas.add(new Area(123));
      project.setAreas(newAreas);
      assertEquals(newAreas, project.getAreas());
   }

   @Test
   public final void testGetSetBuildings()
   {
      final Project project = new Project();
      assertNotNull(project);

      Set<Building> buildings = project.getBuildings();
      assertNotNull(buildings);
      assertTrue(buildings.isEmpty());

      TreeSet<Building> newBuildings = new TreeSet<Building>();
      project.setBuildings(newBuildings);
      assertEquals(newBuildings, project.getBuildings());
   }

   @Test
   public final void testGetSetMainGroups()
   {
      final Project project = new Project();
      assertNotNull(project);

      Collection<MainGroup> mainGroups = project.getMainGroups();
      assertNotNull(mainGroups);
      assertTrue(mainGroups.isEmpty());

      TreeSet<MainGroup> newMainGroups = new TreeSet<MainGroup>();
      newMainGroups.add(new MainGroup());
      project.setMainGroups(newMainGroups);
      assertEquals(newMainGroups, project.getMainGroups());
   }

   @Test
   public final void testAddRemoveArea()
   {
      final Project project = new Project();
      assertNotNull(project);
      assertTrue(project.getAreas().isEmpty());

      final Area area = new Area(1);
      project.add(area);

      assertNotNull(area.getProject());
      assertNotNull(project.getAreas());
      assertFalse(project.getAreas().isEmpty());
      assertEquals(1, project.getAreas().size());
      assertEquals(area, project.getAreas().iterator().next());

      final Area area2 = new Area(2);
      project.add(area2);
      assertEquals(2, project.getAreas().size());

      project.remove(area);
      assertNull(area.getProject());
      assertEquals(1, project.getAreas().size());

      project.remove(area);
      assertEquals(1, project.getAreas().size());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testAddAreaTwice()
   {
      final Project project = new Project();
      final Area area = new Area(1);
      project.add(area);
      project.add(area);
   }

   @Test
   public final void testAddRemoveBuilding()
   {
      final Project project = new Project();
      assertNotNull(project);
      assertTrue(project.getBuildings().isEmpty());

      final Building building = new Building(1);
      project.add(building);

      assertNotNull(project.getBuildings());
      assertFalse(project.getBuildings().isEmpty());
      assertEquals(1, project.getBuildings().size());
      assertEquals(building, project.getBuildings().iterator().next());

      Building building2 = new Building(2);
      project.add(building2);
      assertEquals(2, project.getBuildings().size());

      project.remove(building);
      assertNull(building.getProject());
      assertEquals(1, project.getBuildings().size());

      project.remove(building);
      assertEquals(1, project.getBuildings().size());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testAddBuildingTwice()
   {
      final Project project = new Project();
      final Building building = new Building(1);
      project.add(building);
      project.add(building);
   }

   @Test
   public final void testAddRemoveMainGroup()
   {
      final Project project = new Project();
      assertNotNull(project);
      assertTrue(project.getAreas().isEmpty());

      final MainGroup mainGroup = new MainGroup(1);
      project.add(mainGroup);

      assertNotNull(project.getMainGroups());
      assertFalse(project.getMainGroups().isEmpty());
      assertEquals(1, project.getMainGroups().size());
      assertEquals(mainGroup, project.getMainGroups().iterator().next());

      final MainGroup mainGroup2 = new MainGroup(2);
      project.add(mainGroup2);
      assertEquals(2, project.getMainGroups().size());

      project.remove(mainGroup2);
      assertNull(mainGroup2.getProject());
      assertEquals(1, project.getMainGroups().size());

      project.remove(mainGroup2);
      assertEquals(1, project.getMainGroups().size());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testAddMainGroupTwice()
   {
      final Project project = new Project();
      final Building building = new Building(1);
      project.add(building);
      project.add(building);
   }

   @Test
   public final void testEquals()
   {
      final Project project1 = new Project();
      final Project project2 = new Project();

      assertFalse(project1.equals(null));
      assertFalse(project1.equals(new Object()));

      assertTrue(project1.equals(project1));
      assertTrue(project1.equals(project2));
      assertTrue(project2.equals(project1));

      project1.add(new MainGroup(1));
      assertFalse(project1.equals(project2));
      project2.add(new MainGroup(1));
      assertTrue(project1.equals(project2));

      project1.add(new Building(1));
      assertFalse(project1.equals(project2));
      project2.add(new Building(1));
      assertTrue(project1.equals(project2));

      project1.add(new Area(1));
      assertFalse(project1.equals(project2));
      project2.add(new Area(1));
      assertTrue(project1.equals(project2));

      project1.setDescription("desc-1");
      project2.setDescription("desc-2");
      assertFalse(project1.equals(project2));

      project1.setName("project-1");
      project2.setName("project-2");
      assertFalse(project1.equals(project2));

      project1.setId(1);
      project2.setId(2);
      assertFalse(project1.equals(project2));
   }
}
