package org.freebus.fts.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.freebus.fts.products.ProductsImporter;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.VirtualDeviceService;
import org.freebus.fts.project.internal.I18n;

/**
 * Factory that creates a project that gets initialized with example values.
 */
public final class SampleProjectFactory
{
   private static final String persistenceUnitName = "test-full";
   private static final int sampleVirtualDeviceId = 1000000000;
   private static String sampleImportFileName = "sample-products.vd_";

   /**
    * Import the example org.freebus.fts.products.
    */
   private synchronized static void importSampleDevices()
   {
      Logger.getLogger(SampleProjectFactory.class).info("Importing sample org.freebus.fts.products");

      final InputStream inStream = SampleProjectFactory.class.getClassLoader()
            .getResourceAsStream(sampleImportFileName);
      if (inStream == null)
         throw new RuntimeException("Could not find example products file \"" + sampleImportFileName + "\" in class path");

      File tempFile = null;
      try
      {
         tempFile = File.createTempFile("fts-sample-import", ".vd_");
         tempFile.deleteOnExit();
         final OutputStream outStream = new FileOutputStream(tempFile);

         final byte[] buffer = new byte[8192];
         int rlen;
         while (true)
         {
            rlen = inStream.read(buffer, 0, buffer.length);
            if (rlen <= 0) break;
            outStream.write(buffer, 0, rlen);
         }

         outStream.close();
         inStream.close();
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not create temp file for sample org.freebus.fts.products import");
      }

      final ProductsFactory vdxFactory = ProductsManager.getFactory(tempFile, persistenceUnitName);
      final ProductsFactory productsFactory = ProductsManager.getFactory();
      final ProductsImporter importer = new ProductsImporter(vdxFactory, productsFactory);

      final List<VirtualDevice> devs = vdxFactory.getVirtualDeviceService().getVirtualDevices();

      final boolean ownTransaction = !productsFactory.getTransaction().isActive();

      if (ownTransaction)
         productsFactory.getTransaction().begin();

      importer.copy(devs);

      if (ownTransaction)
         productsFactory.getTransaction().commit();

      if (tempFile != null) tempFile.delete();
   }

   /**
    * Creates a project that gets initialized with example values.
    */
   public static Project newProject()
   {
      final ProductsFactory productsFactory = ProductsManager.getFactory();
      final VirtualDeviceService virtDevService = productsFactory.getVirtualDeviceService();

      final Project project = new Project();
      project.setName(I18n.getMessage("SampleProjectFactory.ProjectName"));

      final Area area = new Area();
      area.setName(I18n.getMessage("SampleProjectFactory.Area1"));
      area.setAddress(1);
      project.add(area);

      final Line line1 = new Line();
      line1.setName(I18n.getMessage("SampleProjectFactory.Line1"));
      line1.setAddress(1);
      area.add(line1);

      final Line line2 = new Line();
      line2.setName(I18n.getMessage("SampleProjectFactory.Line2"));
      line2.setAddress(2);
      area.add(line2);

      final Building building = new Building();
      building.setName(I18n.getMessage("SampleProjectFactory.Building1"));
      project.add(building);

      final Room room1 = new Room();
      room1.setName(I18n.getMessage("SampleProjectFactory.Room1"));
      building.add(room1);

      final Room room2 = new Room();
      room2.setName(I18n.getMessage("SampleProjectFactory.Room2"));
      building.add(room2);

      VirtualDevice virtDev = virtDevService.getVirtualDevice(sampleVirtualDeviceId);
      if (virtDev == null)
      {
         importSampleDevices();

         virtDev = virtDevService.getVirtualDevice(sampleVirtualDeviceId);
         if (virtDev == null)
         {
            // should not happen, as importSampleDevices() imports the
            // device(s).
            throw new RuntimeException("Internal error: example device not found in database after import");
         }
      }

      final Device device1 = new Device(0, virtDev);
      device1.setAddress(31);
      line1.add(device1);
      room1.add(device1);

      final Device device2 = new Device(0, virtDev);
      device2.setAddress(32);
      line1.add(device2);
      room2.add(device2);

      final Device device3 = new Device(0, virtDev);
      device3.setAddress(33);
      line2.add(device3);
      room2.add(device3);

      final MainGroup mainGroup1 = new MainGroup();
      mainGroup1.setAddress(4);
      project.add(mainGroup1);

      final MidGroup midGroup1 = new MidGroup();
      midGroup1.setAddress(5);
      mainGroup1.add(midGroup1);

      final MidGroup midGroup2 = new MidGroup();
      midGroup1.setAddress(6);
      mainGroup1.add(midGroup2);

      final SubGroup group1 = new SubGroup();
      group1.setAddress(101);
      midGroup1.add(group1);

      final SubGroup group2 = new SubGroup();
      group2.setAddress(102);
      midGroup1.add(group2);

      return project;
   }

   //
   // It is not allowed to create objects of this class
   //
   private SampleProjectFactory()
   {
   }
}
