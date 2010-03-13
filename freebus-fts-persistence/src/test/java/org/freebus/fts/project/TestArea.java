package org.freebus.fts.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Vector;

import org.junit.Test;

public class TestArea
{
   @Test
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

   @Test
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

   @Test
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

   @Test
   public final void testGetSetName()
   {
      final Area area = new Area();
      assertEquals("", area.getName());

      area.setName("area-1");
      assertEquals("area-1", area.getName());

      area.setName("area-2");
      assertEquals("area-2", area.getName());

      area.setName("");
      assertEquals("", area.getName());

      area.setName(null);
      assertEquals("", area.getName());
   }

   @Test
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

   @Test
   public final void testGetSetLines()
   {
      final Area area = new Area();

      final List<Line> newLines = new Vector<Line>();
      area.setLines(newLines);

      assertEquals(newLines, area.getLines());
   }

   @Test
   public final void testAdd()
   {
      final Area area = new Area();
      assertTrue(area.getLines().isEmpty());

      final Line line = new Line();
      line.setId(2);
      area.add(line);

      assertFalse(area.getLines().isEmpty());
      assertEquals(1, area.getLines().size());
      assertEquals(line, area.getLines().iterator().next());

      area.add(new Line());
      assertEquals(2, area.getLines().size());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testAddTwice()
   {
      final Area area = new Area();
      final Line line = new Line();
      area.add(line);
      area.add(line);
   }

   @Test
   public final void testEquals()
   {
      final Area area1 = new Area();
      final Area area2 = new Area();

      assertTrue(area1.equals(area1));
      assertTrue(area1.equals(area2));
      assertTrue(area2.equals(area1));

      area1.add(new Line());
      assertFalse(area1.equals(area2));
      assertFalse(area2.equals(area1));

      area2.add(new Line());
      assertTrue(area1.equals(area2));
      assertTrue(area2.equals(area1));

      area1.setName("area-1");
      area2.setName("area-2");
      assertFalse(area1.equals(area2));
      assertFalse(area2.equals(area1));

      area1.setAddress(1);
      area2.setAddress(2);
      assertFalse(area1.equals(area2));
      assertFalse(area2.equals(area1));

      area1.setId(1);
      area2.setId(2);
      assertFalse(area1.equals(area2));
      assertFalse(area2.equals(area1));
   }
}
