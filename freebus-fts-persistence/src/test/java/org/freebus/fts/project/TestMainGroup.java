package org.freebus.fts.project;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.fts.project.Project;

public class TestMainGroup extends TestCase
{

   public final void testMainGroup()
   {
      final MainGroup mainGroup = new MainGroup();
      assertNotNull(mainGroup);
      assertEquals(0, mainGroup.getId());
      assertEquals(0, mainGroup.getAddress());
      assertNull(mainGroup.getProject());
      assertNotNull(mainGroup.getMidGroups());
      assertTrue(mainGroup.getMidGroups().isEmpty());
      assertNotNull(mainGroup.toString());
   }

   public final void testGetSetId()
   {
      final MainGroup mainGroup = new MainGroup();
      assertEquals(0, mainGroup.getId());

      mainGroup.setId(123);
      assertEquals(123, mainGroup.getId());

      mainGroup.setId(-11);
      assertEquals(-11, mainGroup.getId());

      mainGroup.setId(0);
      assertEquals(0, mainGroup.getId());
   }

   public final void testGetSetProject()
   {
      final MainGroup mainGroup = new MainGroup();
      assertEquals(0, mainGroup.getId());

      final Project project = new Project();
      assertNotNull(project);

      mainGroup.setProject(project);
      assertEquals(project, mainGroup.getProject());

      mainGroup.setProject(null);
      assertNull(mainGroup.getProject());

      project.add(mainGroup);
      assertEquals(project, mainGroup.getProject());
   }

   public final void testGetSetName()
   {
      final MainGroup mainGroup = new MainGroup();

      mainGroup.setName("mainGroup-1");
      assertEquals("mainGroup-1", mainGroup.getName());

      mainGroup.setName("mainGroup-2");
      assertEquals("mainGroup-2", mainGroup.getName());

      mainGroup.setName("");
      assertEquals("", mainGroup.getName());
   }

   public final void testGetSetAddress()
   {
      final MainGroup mainGroup = new MainGroup();
      assertEquals(0, mainGroup.getAddress());

      mainGroup.setAddress(12);
      assertEquals(12, mainGroup.getAddress());

      mainGroup.setAddress(3);
      assertEquals(3, mainGroup.getAddress());

      mainGroup.setAddress(0);
      assertEquals(0, mainGroup.getAddress());
   }

   public final void testGetSetMidGroups()
   {
      final MainGroup mainGroup = new MainGroup();

      final Set<MidGroup> newMidGroups = new HashSet<MidGroup>();
      mainGroup.setMidGroups(newMidGroups);

      assertEquals(newMidGroups, mainGroup.getMidGroups());
   }

   public final void testAdd()
   {
      final MainGroup mainGroup = new MainGroup(1);
      assertTrue(mainGroup.getMidGroups().isEmpty());

      final MidGroup midGroup = new MidGroup(2);
      mainGroup.add(midGroup);

      assertFalse(mainGroup.getMidGroups().isEmpty());
      assertEquals(1, mainGroup.getMidGroups().size());
      assertEquals(midGroup, mainGroup.getMidGroups().iterator().next());

      mainGroup.add(midGroup);
      assertEquals(1, mainGroup.getMidGroups().size());

      mainGroup.add(new MidGroup(3));
      assertEquals(2, mainGroup.getMidGroups().size());      
   }

   public final void testEquals()
   {
      final MainGroup o1 = new MainGroup(1);
      final MainGroup o2 = new MainGroup(1);
      final MainGroup o3 = new MainGroup(3);

      assertTrue(o1.equals(o2));
      assertFalse(o1.equals(o3));
      assertFalse(o1.equals(null));
      assertFalse(o1.equals(new Object()));
   }
}
