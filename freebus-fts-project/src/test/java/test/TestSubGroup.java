package test;

import junit.framework.TestCase;

import org.freebus.fts.project.SubGroup;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.knxcomm.telegram.GroupAddress;

public class TestSubGroup extends TestCase
{
   public final void testGroup()
   {
      final SubGroup group = new SubGroup();
      assertNotNull(group);
      assertEquals(0, group.getId());
      assertEquals(0, group.getAddress());
      assertNull(group.getMidGroup());
      assertEquals(GroupAddress.BROADCAST, group.getGroupAddress());
   }

   public final void testGetSetId()
   {
      final SubGroup group = new SubGroup();

      group.setId(1234);
      assertEquals(1234, group.getId());

      group.setId(0);
      assertEquals(0, group.getId());
   }

   public final void testGetSetAddress()
   {
      final SubGroup group = new SubGroup();

      group.setAddress(123);
      assertEquals(123, group.getAddress());

      group.setAddress(0);
      assertEquals(0, group.getAddress());
   }

   public final void testGetSetName()
   {
      final SubGroup group = new SubGroup();

      group.setName("group-1");
      assertEquals("group-1", group.getName());

      group.setName("group-2");
      assertEquals("group-2", group.getName());

      group.setName("");
      assertEquals("", group.getName());
   }

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

   public final void testGetSetMidGroup()
   {
      final SubGroup group = new SubGroup();
      final MidGroup midGroup = new MidGroup();

      group.setMidGroup(midGroup);
      assertEquals(midGroup, group.getMidGroup());

      group.setMidGroup(null);
      assertNull(group.getMidGroup());
   }
}
