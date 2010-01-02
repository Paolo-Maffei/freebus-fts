package test;

import junit.framework.TestCase;

import org.freebus.fts.project.Group;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.knxcomm.telegram.GroupAddress;

public class TestGroup extends TestCase
{
   public final void testGroup()
   {
      final Group group = new Group();
      assertNotNull(group);
      assertEquals(0, group.getId());
      assertEquals(0, group.getAddress());
      assertNull(group.getMidGroup());
      assertEquals(GroupAddress.BROADCAST, group.getGroupAddress());
   }

   public final void testGetSetId()
   {
      final Group group = new Group();

      group.setId(1234);
      assertEquals(1234, group.getId());

      group.setId(0);
      assertEquals(0, group.getId());
   }

   public final void testGetSetAddress()
   {
      final Group group = new Group();

      group.setAddress(123);
      assertEquals(123, group.getAddress());

      group.setAddress(0);
      assertEquals(0, group.getAddress());
   }

   public final void testGetSetName()
   {
      final Group group = new Group();

      group.setName("group-1");
      assertEquals("group-1", group.getName());

      group.setName("group-2");
      assertEquals("group-2", group.getName());

      group.setName("");
      assertEquals("", group.getName());
   }

   public final void testGetPhysicalAddr()
   {
      final Group group = new Group();
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
      final Group group = new Group();
      final MidGroup midGroup = new MidGroup();

      group.setMidGroup(midGroup);
      assertEquals(midGroup, group.getMidGroup());

      group.setMidGroup(null);
      assertNull(group.getMidGroup());
   }
}
