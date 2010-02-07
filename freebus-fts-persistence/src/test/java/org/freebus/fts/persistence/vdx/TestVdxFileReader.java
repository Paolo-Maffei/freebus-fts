package org.freebus.fts.persistence.vdx;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.freebus.fts.persistence.test_entities.SampleBaggage;
import org.freebus.fts.persistence.test_entities.SampleManufacturer;
import org.junit.Before;
import org.junit.Test;

public final class TestVdxFileReader
{
   private final static String vdxFileName = "src/test/resources/test-file.vd_";
   private VdxFileReader reader;

   @Before
   public void setUp() throws IOException
   {
      reader = new VdxFileReader(vdxFileName);
      assertEquals("5.10", reader.getVersion());
   }

   @Test
   public void testVdxFileReader() throws Exception
   {
      assertNotNull(reader);
      assertEquals(vdxFileName, reader.getFileName());
   }

   @Test
   public void testFinalizeClosed() throws Exception
   {
      reader.close();
      reader.finalize(); // this looks strange, but it seems to work
   }

   @Test(expected = RuntimeException.class)
   public void testFinalizeNotClosed() throws Exception
   {
      (new VdxFileReader(vdxFileName)).finalize(); // this looks strange, but it seems to to work
   }

   @Test
   public void testHasSection()
   {
      assertTrue(reader.hasSection("Manufacturer"));
      assertTrue(reader.hasSection("functional_ENTity"));
      assertFalse(reader.hasSection("no-such-section"));
      assertFalse(reader.hasSection(""));
   }

   @Test
   public void testGetSection() throws IOException
   {
      assertNotNull(reader.getSection("MANUfacturer"));
      assertNotNull(reader.getSection("manufacturer"));
      assertNotNull(reader.getSection("ApplicationProgramBaggage"));
      assertNotNull(reader.getSection("EmptySection"));
      assertNull(reader.getSection("no-such-section"));
      assertNull(reader.getSection(""));
   }

   @Test
   public void testRemoveSectionContents() throws IOException
   {
      assertNotNull(reader.getSection("manufacturer"));
      reader.removeSectionContents("manufacturer");
      assertNotNull(reader.getSection("manufacturer"));
   }

   @Test
   public void testGetSetLanguage()
   {
      reader.setLanguageId(1);
      assertEquals(1, reader.getLanguageId());

      reader.setLanguageId(0);
      assertEquals(0, reader.getLanguageId());

      reader.setLanguageId(-1);
      reader.setLanguage("German");
      assertEquals(1031, reader.getLanguageId());

      reader.setLanguage("NoSuchLanguage");
      assertEquals(0, reader.getLanguageId());
   }

   @Test
   public void testGetSectionEntries() throws IOException
   {
      reader.setLanguageId(1031);

      final Object[] entries = reader.getSectionEntries("ApplicationProgramBaggage", SampleBaggage.class);
      assertNotNull(entries);
      assertEquals(2, entries.length);
      assertEquals(SampleBaggage.class, entries[0].getClass());

      final SampleBaggage obj1 = (SampleBaggage) entries[0];
      assertEquals(2348887, obj1.id, 0.5);
      assertEquals("2330886", obj1.appId);
      assertEquals(1031, obj1.langId);
      assertEquals(4, obj1.anyKey);
      assertEquals(15, obj1.data[0]);
      assertEquals(0, obj1.data[1]);
      assertEquals(4, obj1.data[2]);
      assertEquals(-1, obj1.data[3]);
      assertEquals(-128, obj1.data[4]);

      final SampleBaggage obj2 = (SampleBaggage) entries[1];
      assertEquals(0, obj2.id, 0.5);
      assertEquals("", obj2.appId);
      assertEquals(1031, obj2.langId);
      assertEquals(0, obj2.anyKey);
   }

   @Test
   public void testGetSectionEntries2() throws IOException
   {
      reader.setLanguageId(1031);

      final Object[] entries = reader.getSectionEntries("manufacturer", SampleManufacturer.class);
      assertNotNull(entries);
      assertEquals(3, entries.length);
      assertEquals(SampleManufacturer.class, entries[0].getClass());
   }

   @Test
   public void testScan() throws Exception
   {
      final Set<String> sectionNames = reader.getSectionNames();

      assertEquals(7, sectionNames.size());
      assertEquals(true, sectionNames.contains("manufacturer"));

      final VdxSectionHeader sectionHeader3 = reader.getSectionHeader("manufacturer");
      assertNotNull(sectionHeader3);
      assertEquals("manufacturer", sectionHeader3.name);
      assertEquals(3, sectionHeader3.id);
      assertEquals(122, sectionHeader3.offset);
      assertEquals(3, sectionHeader3.fields.length);

      final VdxSectionHeader sectionHeader4 = reader.getSectionHeader("functional_entity");
      assertNotNull(sectionHeader4);
      assertEquals("functional_entity", sectionHeader4.name);
      assertEquals(4, sectionHeader4.id);
      assertEquals(9, sectionHeader4.fields.length);
      assertEquals("fun_functional_entity_id", sectionHeader4.fields[5]);

      final VdxSectionHeader sectionHeader5 = reader.getSectionHeader("emptysection");
      assertNotNull(sectionHeader5);
      assertEquals("emptysection", sectionHeader5.name);
      assertEquals(67, sectionHeader5.id);
      assertEquals(2, sectionHeader5.fields.length);
   }

   @Test
   public void testEmptySection() throws Exception
   {
      final VdxSection section = reader.getSection("emptysection");
      assertNotNull(section);
      assertEquals(0, section.getNumElements());

      final VdxSection section2 = reader.getSection("emptysection2");
      assertNotNull(section2);
      assertEquals(0, section2.getNumElements());
   }
}
