package org.freebus.fts.persistence.vdx;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestVdxSection
{
   private VdxSection section;
   private VdxSectionHeader header;

   private String[] values1 = new String[] { "name-1", "101", "string-1" };
   private String[] values2 = new String[] { "name-3", "", "" };
   private String[] values3 = new String[] { "name-2", "202", "string-2" };
   private String[] values4 = new String[] { "name-8", "808", "string-8" };

   @Before
   public final void setUp()
   {
      final String[] fields = new String[] { "field1", "field2", "field3" };
      header = new VdxSectionHeader("section-1", 1, 0, fields);

      section = new VdxSection(header);

      section.addElementValues(values1);
      section.addElementValues(values2);
      section.addElementValues(values3);
      section.addElementValues(values4);
   }

   @Test
   public final void testGetHeader()
   {
      assertNotNull(section);
      assertEquals(header, section.getHeader());
   }

   @Test
   public final void testGetNumElements()
   {
      assertEquals(4, section.getNumElements());

      final VdxSection section2 = new VdxSection(header);
      assertEquals(0, section2.getNumElements());
   }

   @Test
   public final void testGetElementValues()
   {
      assertArrayEquals(values1, section.getElementValues(0));
      assertArrayEquals(values4, section.getElementValues(3));
   }

   @Test(expected = ArrayIndexOutOfBoundsException.class)
   public final void testGetElementValuesBounds()
   {
      assertNull(section.getElementValues(-1));
   }

   @Test(expected = ArrayIndexOutOfBoundsException.class)
   public final void testGetElementValuesBounds2()
   {
      assertNull(section.getElementValues(999999));
   }

   @Test
   public final void testAddElementValues()
   {
      final String[] values = new String[] { "name-99", "9999", "string-99" };
      section.addElementValues(values);

      assertEquals(5, section.getNumElements());
      assertArrayEquals(values, section.getElementValues(4));
   }

   @Test
   public final void testGetValueIntInt()
   {
      assertEquals("name-3", section.getValue(1, 0));
      assertEquals("string-8", section.getValue(3, 2));
   }

   @Test
   public final void testGetIntValueIntInt()
   {
      assertEquals(0, section.getIntValue(1, 1));
      assertEquals(808, section.getIntValue(3, 1));
   }

   @Test
   public final void testGetValueIntString()
   {
      assertEquals("name-3", section.getValue(1, "field1"));
   }

   @Test
   public final void testGetIntValueIntString()
   {
      assertEquals(0, section.getIntValue(1, "field2"));
      assertEquals(202, section.getIntValue(2, "field2"));
   }

   @Test
   public final void testClear()
   {
      assertNotSame(0, section.getNumElements());

      section.clear();
      assertEquals(0, section.getNumElements());
   }

}
