package test;


import java.util.Set;

import junit.framework.TestCase;

import org.freebus.fts.persistence.vdx.VdxFileReader;
import org.freebus.fts.persistence.vdx.VdxSection;
import org.freebus.fts.persistence.vdx.VdxSectionHeader;

public class TestVdxFileReader extends TestCase
{
   public void testScan() throws Exception
   {
      VdxFileReader reader = new VdxFileReader("src/test/resources/test-file.vd_");
      final Set<String> sectionNames = reader.getSectionNames();

      assertEquals(6, sectionNames.size());
      assertEquals(true, sectionNames.contains("manufacturer"));

      final VdxSectionHeader sectionHeader3 = reader.getSectionHeader("manufacturer");
      assertNotNull(sectionHeader3);
      assertEquals("manufacturer", sectionHeader3.name);
      assertEquals(3, sectionHeader3.id);
      assertTrue(sectionHeader3.offset > 80);
      assertEquals(3, sectionHeader3.fields.length);

      final VdxSectionHeader sectionHeader4 = reader.getSectionHeader("functional_entity");
      assertNotNull(sectionHeader4);
      assertEquals("functional_entity", sectionHeader4.name);
      assertEquals(4, sectionHeader4.id);
      assertTrue(sectionHeader4.offset > sectionHeader3.offset);
      assertEquals(9, sectionHeader4.fields.length);
      assertEquals("fun_functional_entity_id", sectionHeader4.fields[5]);

      final VdxSectionHeader sectionHeader5 = reader.getSectionHeader("emptysection");
      assertNotNull(sectionHeader5);
      assertEquals("emptysection", sectionHeader5.name);
      assertEquals(67, sectionHeader5.id);
      assertTrue(sectionHeader5.offset > sectionHeader4.offset);
      assertEquals(2, sectionHeader5.fields.length);
   }

   public void testEmptySection() throws Exception
   {
      VdxFileReader reader = new VdxFileReader("src/test/resources/test-file.vd_");

      final VdxSection section = reader.getSection("emptysection");
      assertNotNull(section);
      assertEquals(0, section.getNumElements());

      final VdxSection section2 = reader.getSection("emptysection2");
      assertNotNull(section2);
      assertEquals(0, section2.getNumElements());
   }
}
