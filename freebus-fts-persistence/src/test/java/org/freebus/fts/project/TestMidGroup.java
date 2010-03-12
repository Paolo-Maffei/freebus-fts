package org.freebus.fts.project;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.freebus.fts.project.SubGroup;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;

public class TestMidGroup extends TestCase
{

   public final void testMidGroup()
   {
      final MidGroup midGroup = new MidGroup();
      assertNotNull(midGroup);
      assertEquals(0, midGroup.getId());
      assertEquals(0, midGroup.getAddress());
      assertNull(midGroup.getMainGroup());
      assertNotNull(midGroup.getSubGroups());
      assertTrue(midGroup.getSubGroups().isEmpty());
      assertNotNull(midGroup.toString());
   }

   public final void testGetSetId()
   {
      final MidGroup midGroup = new MidGroup();
      assertEquals(0, midGroup.getId());

      midGroup.setId(123);
      assertEquals(123, midGroup.getId());

      midGroup.setId(-11);
      assertEquals(-11, midGroup.getId());

      midGroup.setId(0);
      assertEquals(0, midGroup.getId());
   }

   public final void testGetSetMainGroup()
   {
      final MidGroup midGroup = new MidGroup();
      assertEquals(0, midGroup.getId());

      final MainGroup mainGroup = new MainGroup();
      assertNotNull(mainGroup);

      midGroup.setMainGroup(mainGroup);
      assertEquals(mainGroup, midGroup.getMainGroup());
      assertNotNull(midGroup.toString());

      midGroup.setMainGroup(null);
      assertNull(midGroup.getMainGroup());

      mainGroup.add(midGroup);
      assertEquals(mainGroup, midGroup.getMainGroup());
   }

   public final void testGetSetName()
   {
      final MidGroup midGroup = new MidGroup();

      midGroup.setName("midGroup-1");
      assertEquals("midGroup-1", midGroup.getName());

      midGroup.setName("midGroup-2");
      assertEquals("midGroup-2", midGroup.getName());

      midGroup.setName("");
      assertEquals("", midGroup.getName());
   }

   public final void testGetSetAddress()
   {
      final MidGroup midGroup = new MidGroup();
      assertEquals(0, midGroup.getAddress());

      midGroup.setAddress(12);
      assertEquals(12, midGroup.getAddress());

      midGroup.setAddress(3);
      assertEquals(3, midGroup.getAddress());

      midGroup.setAddress(0);
      assertEquals(0, midGroup.getAddress());
   }

   public final void testGetSetGroups()
   {
      final MidGroup midGroup = new MidGroup();

      final Set<SubGroup> newGroups = new HashSet<SubGroup>();
      midGroup.setSubGroups(newGroups);

      assertEquals(newGroups, midGroup.getSubGroups());
   }

   public final void testAdd()
   {
      final MidGroup midGroup = new MidGroup(1);
      assertTrue(midGroup.getSubGroups().isEmpty());

      final SubGroup group = new SubGroup(1);
      midGroup.add(group);

      assertFalse(midGroup.getSubGroups().isEmpty());
      assertEquals(1, midGroup.getSubGroups().size());
      assertEquals(group, midGroup.getSubGroups().iterator().next());

      midGroup.add(new SubGroup(2));
      assertEquals(2, midGroup.getSubGroups().size());      
   }

   public final void testEquals()
   {
      final MidGroup o1 = new MidGroup(1);
      final MidGroup o2 = new MidGroup(1);
      final MidGroup o3 = new MidGroup(3);

      assertTrue(o1.equals(o2));
      assertFalse(o1.equals(o3));
      assertFalse(o1.equals(null));
      assertFalse(o1.equals(new Object()));
   }
}
