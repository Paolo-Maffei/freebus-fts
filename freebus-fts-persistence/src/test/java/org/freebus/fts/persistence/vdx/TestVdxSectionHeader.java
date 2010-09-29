package org.freebus.fts.persistence.vdx;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestVdxSectionHeader
{
   @Test
   public final void testVdxSectionHeader()
   {
      final VdxSectionHeader hdr = new VdxSectionHeader("section-1", 123, 4567890, null, null, null);
      assertNotNull(hdr);

      assertEquals("section-1", hdr.name);
      assertEquals(123, hdr.id);
      assertEquals(4567890, hdr.offset);
      assertNull(hdr.fields);
      assertNotNull(hdr.toString());
   }

   @Test
   public final void testGetIndexOf()
   {
      final String[] fields = new String[] { "field1", "field2", "field3" };
      final VdxFieldType[] types = new VdxFieldType[] { VdxFieldType.STRING, VdxFieldType.STRING, VdxFieldType.STRING };
      final int[] sizes = new int[] { 10, 5, 8 };
      final VdxSectionHeader hdr = new VdxSectionHeader("section-1", 1, 0, fields, types, sizes);

      assertEquals(-1, hdr.getIndexOf(null));
      assertEquals(-1, hdr.getIndexOf("no-such-field"));
      assertEquals(1, hdr.getIndexOf("field2"));
      assertEquals(5, hdr.sizes[1]);
   }
}
