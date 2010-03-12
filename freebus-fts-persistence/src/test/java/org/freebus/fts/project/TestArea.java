package org.freebus.fts.project;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.freebus.fts.project.Area;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;

public class TestArea extends TestCase
{

   public final void testArea()
   {
      final Area area = new Area();
      assertNotNull(area);
      assertEquals(0, area.getId());
      assertEquals(0, area.getAddress());
      assertNull(area.getProject());
      assertNotNull(area.getLines());
      assertEquals("", area.getName());
   }

   public final void testGetSetId()
   {
      final Area area = new Area();
      assertEquals(0, area.getId());

      area.setId(123);
      assertEquals(123, area.getId());

      area.setId(-11);
      assertEquals(-11, area.getId());

      area.setId(0);
      assertEquals(0, area.getId());
   }

   public final void testGetSetProject()
   {
      final Area area = new Area();
      assertEquals(0, area.getId());

      final Project project = new Project();
      assertNotNull(project);

      area.setProject(project);
      assertEquals(project, area.getProject());

      area.setProject(null);
      assertNull(area.getProject());

      project.add(area);
      assertEquals(project, area.getProject());
   }

   public final void testGetSetName()
   {
      final Area area = new Area();

      area.setName("area-1");
      assertEquals("area-1", area.getName());

      area.setName("area-2");
      assertEquals("area-2", area.getName());

      area.setName("");
      assertEquals("", area.getName());
   }

   public final void testGetSetAddress()
   {
      final Area area = new Area();
      assertEquals(0, area.getAddress());

      area.setAddress(12);
      assertEquals(12, area.getAddress());

      area.setAddress(3);
      assertEquals(3, area.getAddress());

      area.setAddress(0);
      assertEquals(0, area.getAddress());
   }

   public final void testGetSetLines()
   {
      final Area area = new Area();

      final Set<Line> newLines = new HashSet<Line>();
      area.setLines(newLines);

      assertEquals(newLines, area.getLines());
   }

   public final void testAdd()
   {
      final Area area = new Area();
      assertTrue(area.getLines().isEmpty());

      final Line line = new Line();
      area.add(line);

      assertFalse(area.getLines().isEmpty());
      assertEquals(1, area.getLines().size());
      assertEquals(line, area.getLines().iterator().next());

      area.add(new Line());
      assertEquals(2, area.getLines().size());      
   }
}
