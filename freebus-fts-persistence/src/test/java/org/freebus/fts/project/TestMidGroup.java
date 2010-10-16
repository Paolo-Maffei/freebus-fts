package org.freebus.fts.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.TreeSet;

import org.junit.Test;

public class TestMidGroup
{
   @Test
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

   @Test
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

   @Test
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

   @Test
   public final void testGetSetName()
   {
      final MidGroup midGroup = new MidGroup();
      assertEquals("", midGroup.getName());

      midGroup.setName("midGroup-1");
      assertEquals("midGroup-1", midGroup.getName());

      midGroup.setName("midGroup-2");
      assertEquals("midGroup-2", midGroup.getName());

      midGroup.setName("");
      assertEquals("", midGroup.getName());

      midGroup.setName(null);
      assertEquals("", midGroup.getName());
   }

   @Test
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

   @Test
   public final void testGetSetGroups()
   {
      final MidGroup midGroup = new MidGroup();
      assertTrue(midGroup.getSubGroups().isEmpty());

      final TreeSet<SubGroup> newGroups = new TreeSet<SubGroup>();
      newGroups.add(new SubGroup(17));
      midGroup.setSubGroups(newGroups);

      assertEquals(newGroups, midGroup.getSubGroups());
   }

   @Test
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

   @Test
   public final void testEquals()
   {
      final MidGroup midGroup1 = new MidGroup();
      final MidGroup midGroup2 = new MidGroup();

      assertTrue(midGroup1.equals(midGroup1));
      assertTrue(midGroup1.equals(midGroup2));
      assertTrue(midGroup2.equals(midGroup1));

      midGroup1.add(new SubGroup());
      assertFalse(midGroup1.equals(midGroup2));
      assertFalse(midGroup2.equals(midGroup1));

      midGroup2.add(new SubGroup());
      assertTrue(midGroup1.equals(midGroup2));
      assertTrue(midGroup2.equals(midGroup1));

      midGroup1.setName("midGroup-1");
      midGroup2.setName("midGroup-2");
      assertFalse(midGroup1.equals(midGroup2));
      assertFalse(midGroup2.equals(midGroup1));

      midGroup1.setAddress(1);
      midGroup2.setAddress(2);
      assertFalse(midGroup1.equals(midGroup2));
      assertFalse(midGroup2.equals(midGroup1));

      midGroup1.setId(1);
      midGroup2.setId(2);
      assertFalse(midGroup1.equals(midGroup2));
      assertFalse(midGroup2.equals(midGroup1));
   }
}
