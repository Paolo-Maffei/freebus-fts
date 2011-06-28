package org.freebus.fts.project.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.ManufacturerService;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.test_utils.ProjectTestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestProjectImporter extends ProjectTestCase
{
   private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>";

   @BeforeClass
   public static void beforeClass()
   {
      System.err.println("@BeforeClass");
   }
   
   @Before
   public void setupData()
   {
      final ManufacturerService manuService = getProductsFactory().getManufacturerService();

      System.err.println("@Before");
      
      manuService.saveIfMissing(new Manufacturer(1, "Manufacturer-1"));
      manuService.saveIfMissing(new Manufacturer(2, "Manufacturer-2"));
   }

   @Test
   public void readProjectMinimal() throws FileNotFoundException
   {
      final String data = XML_HEADER + "<project name=\"project-1\" />";
      final InputStream in = new ByteArrayInputStream(data.getBytes());

      final ProjectImporter importer = new ProjectImporter();
      final Project project = importer.readProject(in); 
      assertNotNull(project);
      assertEquals("project-1", project.getName());
   }

   // TODO fix @Test
   public void readProject() throws FileNotFoundException
   {
      final String data = XML_HEADER + 
         "<project name=\"project-1\">\n" +
         " <description>description-1</description>\n" +
         " <areas>\n" +
         "  <area name=\"area-1\" address=\"2\">\n" +
         "   <line name=\"line-1\" address=\"3\">\n" +
         "    <device name=\"device-1\" address=\"1\">\n" +
         "     <virtualDevice name=\"virtual-device-1\" manufacturer=\"Manufacturer-1\" />\n" +
         "     <program name=\"program-1\" manufacturer=\"Manufacturer-1\" deviceType=\"0\" />\n" +
         "    </device>\n" +
         "   </line>\n" +
         "  </area>\n"+
         " </areas>\n" +
         "</project>";
      final InputStream in = new ByteArrayInputStream(data.getBytes());

      final ProjectImporter importer = new ProjectImporter(getProductsFactory());
      final Project project = importer.readProject(in); 
      assertNotNull(project);
      assertEquals("project-1", project.getName());
      assertEquals("description-1", project.getDescription());

      assertEquals(1, project.getAreas().size());
      final Area area = project.getAreas().iterator().next();
      assertEquals("area-1", area.getName());
      assertEquals(2, area.getAddress());

      assertEquals(1, area.getLines().size());
      final Line line = area.getLines().iterator().next();
      assertEquals("line-1", line.getName());
      assertEquals(3, line.getAddress());

      assertEquals(1, line.getDevices().size());
      final Device device = line.getDevices().iterator().next();
      assertEquals("device-1", device.getName());
      assertEquals(1, device.getAddress());
      assertEquals(new PhysicalAddress(2, 3, 1), device.getPhysicalAddress());
   }

   // TODO fix @Test
   public void readExampleProject() throws FileNotFoundException
   {
      final ProjectImporter importer = new ProjectImporter(getProductsFactory());
      final Project project = importer.readProject(new File("src/main/resources/examples/project-1.xml")); 
      assertNotNull(project);
   }
}
