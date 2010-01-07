package test;

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
      final MidGroup midGroup = new MidGroup();
      assertTrue(midGroup.getSubGroups().isEmpty());

      final SubGroup group = new SubGroup();
      midGroup.add(group);

      assertFalse(midGroup.getSubGroups().isEmpty());
      assertEquals(1, midGroup.getSubGroups().size());
      assertEquals(group, midGroup.getSubGroups().iterator().next());

      midGroup.add(new SubGroup());
      assertEquals(2, midGroup.getSubGroups().size());      
   }
}
