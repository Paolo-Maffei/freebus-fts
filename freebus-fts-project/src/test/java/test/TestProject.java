package test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.freebus.fts.project.Area;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.Project;

public class TestProject extends TestCase
{

   public final void testProject()
   {
      final Project project = new Project();
      assertNotNull(project);
      assertEquals(0, project.getId());
      assertNotNull(project.getAreas());
      assertNotNull(project.getBuildings());
      assertNotNull(project.getMainGroups());
   }

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

   public final void testGetSetName()
   {
      final Project project = new Project();
      assertNotNull(project);

      project.setName("project-1");
      assertEquals("project-1", project.getName());

      project.setName("");
      assertEquals("", project.getName());
   }

   public final void testGetSetDescription()
   {
      final Project project = new Project();
      assertNotNull(project);

      project.setDescription("project-desc-1");
      assertEquals("project-desc-1", project.getDescription());

      project.setDescription("");
      assertEquals("", project.getDescription());
   }

   public final void testGetSetLastModified()
   {
      final Project project = new Project();
      assertNotNull(project);

      final Date date1 = new Date(1262900127);
      project.setLastModified(date1);
      assertEquals(date1, project.getLastModified());
   }

   public final void testGetSetAreas()
   {
      final Project project = new Project();
      assertNotNull(project);

      Set<Area> areas = project.getAreas();
      assertNotNull(areas);
      assertTrue(areas.isEmpty());

      Set<Area> newAreas = new HashSet<Area>();
      project.setAreas(newAreas);
      assertEquals(newAreas, project.getAreas());
   }

   public final void testGetSetBuildings()
   {
      final Project project = new Project();
      assertNotNull(project);

      Set<Building> buildings = project.getBuildings();
      assertNotNull(buildings);
      assertTrue(buildings.isEmpty());

      Set<Building> newBuildings = new HashSet<Building>();
      project.setBuildings(newBuildings);
      assertEquals(newBuildings, project.getBuildings());
   }

   public final void testGetSetMainGroups()
   {
      final Project project = new Project();
      assertNotNull(project);

      Set<MainGroup> mainGroups = project.getMainGroups();
      assertNotNull(mainGroups);
      assertTrue(mainGroups.isEmpty());

      Set<MainGroup> newMainGroups = new HashSet<MainGroup>();
      project.setMainGroups(newMainGroups);
      assertEquals(newMainGroups, project.getBuildings());
   }

   public final void testAddArea()
   {
      final Project project = new Project();
      assertNotNull(project);
      assertTrue(project.getAreas().isEmpty());

      final Area area = new Area();
      project.add(area);

      assertNotNull(project.getAreas());
      assertFalse(project.getAreas().isEmpty());
      assertEquals(1, project.getAreas().size());
      assertEquals(area, project.getAreas().iterator().next());

      project.add(area);
      assertEquals(1, project.getAreas().size());
      assertEquals(area, project.getAreas().iterator().next());

      final Area area2 = new Area();
      project.add(area2);
      assertEquals(2, project.getAreas().size());
   }

   public final void testAddBuilding()
   {
      final Project project = new Project();
      assertNotNull(project);
      assertTrue(project.getBuildings().isEmpty());

      final Building building = new Building();
      project.add(building);

      assertNotNull(project.getBuildings());
      assertFalse(project.getBuildings().isEmpty());
      assertEquals(1, project.getBuildings().size());
      assertEquals(building, project.getBuildings().iterator().next());

      Building building2 = new Building();
      project.add(building2);
      assertEquals(2, project.getBuildings().size());
   }

   public final void testAddMainGroup()
   {
      final Project project = new Project();
      assertNotNull(project);
      assertTrue(project.getAreas().isEmpty());

      final MainGroup mainGroup = new MainGroup();
      project.add(mainGroup);

      assertNotNull(project.getMainGroups());
      assertFalse(project.getMainGroups().isEmpty());
      assertEquals(1, project.getMainGroups().size());
      assertEquals(mainGroup, project.getMainGroups().iterator().next());

      project.add(mainGroup);
      assertEquals(1, project.getMainGroups().size());
      assertEquals(mainGroup, project.getMainGroups().iterator().next());

      final MainGroup mainGroup2 = new MainGroup();
      project.add(mainGroup2);
      assertEquals(2, project.getMainGroups().size());
   }
}
