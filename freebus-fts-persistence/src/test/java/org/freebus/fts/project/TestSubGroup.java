package org.freebus.fts.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.freebus.fts.common.address.GroupAddress;
import org.junit.Test;

public class TestSubGroup
{
   @Test
   public final void testSubGroup()
   {
      final SubGroup group = new SubGroup();
      assertNotNull(group);
      assertEquals(0, group.getId());
      assertEquals(0, group.getAddress());
      assertNull(group.getMidGroup());
      assertEquals(GroupAddress.BROADCAST, group.getGroupAddress());
      assertNotNull(group.toString());
   }

   @Test
   public final void testGetSetId()
   {
      final SubGroup group = new SubGroup(1);

      group.setId(1234);
      assertEquals(1234, group.getId());

      group.setId(0);
      assertEquals(0, group.getId());
   }

   @Test
   public final void testGetSetAddress()
   {
      final SubGroup group = new SubGroup();

      group.setAddress(123);
      assertEquals(123, group.getAddress());

      group.setAddress(0);
      assertEquals(0, group.getAddress());
   }

   @Test
   public final void testGetSetName()
   {
      final SubGroup group = new SubGroup();
      assertEquals("", group.getName());

      group.setName("group-1");
      assertEquals("group-1", group.getName());

      group.setName("group-2");
      assertEquals("group-2", group.getName());

      group.setName("");
      assertEquals("", group.getName());

      group.setName(null);
      assertEquals("", group.getName());
   }

   @Test
   public final void testGetPhysicalAddr()
   {
      final SubGroup group = new SubGroup();
      group.setAddress(88);
      assertEquals(GroupAddress.BROADCAST, group.getGroupAddress());

      final MidGroup midGroup = new MidGroup();
      midGroup.setAddress(3);
      midGroup.add(group);
      assertEquals(GroupAddress.BROADCAST, group.getGroupAddress());

      final MainGroup mainGroup = new MainGroup();
      mainGroup.setAddress(2);
      mainGroup.add(midGroup);
      assertEquals(new GroupAddress(2, 3, 88), group.getGroupAddress());
   }

   @Test
   public final void testGetSetMidGroup()
   {
      final SubGroup group = new SubGroup();
      final MidGroup midGroup = new MidGroup();

      group.setMidGroup(midGroup);
      assertEquals(midGroup, group.getMidGroup());

      group.setMidGroup(null);
      assertNull(group.getMidGroup());
   }

   @Test
   public final void testEquals()
   {
      final SubGroup sg1 = new SubGroup(1);
      final SubGroup sg2 = new SubGroup(1);
      final SubGroup sg3 = new SubGroup(3);

      assertTrue(sg1.equals(sg2));
      assertFalse(sg1.equals(sg3));
      assertFalse(sg1.equals(null));
      assertFalse(sg1.equals(new Object()));
   }
}
