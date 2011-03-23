package org.freebus.fts.project;

import org.apache.log4j.Logger;
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
   /**
    * The device-id of the example virtual device.
    */
   public static final int sampleVirtualDeviceId = 1;

   private static String sampleImportFileName = "sample-products.vd_";

   /**
    * Import the example products. Called by {@link #newProject} if required.
    */
   public synchronized static void importSampleDevices(final String persistenceUnitName)
   {
      Logger.getLogger(SampleProjectFactory.class).info("Importing sample org.freebus.fts.products");
      ProductsManager.importResource(sampleImportFileName, persistenceUnitName, ProductsManager.getFactory());
   }

   /**
    * Creates a project that gets initialized with example values, using the
    * persistence unit "default".
    */
   public static Project newProject()
   {
      return newProject("default");
   }

   /**
    * Creates a project that gets initialized with example values. Calls
    * {@link #importSampleDevices} if required.
    * 
    * @param persistenceUnitName - the name of the persistence unit that is used
    *           for the import.
    */
   public static Project newProject(final String persistenceUnitName)
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
         ProductsManager.importResource(sampleImportFileName, persistenceUnitName, ProductsManager.getFactory());

         virtDev = virtDevService.getVirtualDevice(sampleVirtualDeviceId);
         if (virtDev == null)
         {
            // Should not happen, as importSampleDevices() imports the
            // device(s).
            throw new RuntimeException("Internal error: example device #" + sampleVirtualDeviceId
                  + " not found in database after import");
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
      midGroup2.setAddress(6);
      mainGroup1.add(midGroup2);

      final SubGroup group11 = new SubGroup();
      group11.setAddress(111);
      midGroup1.add(group11);

      final SubGroup group12 = new SubGroup();
      group12.setAddress(112);
      midGroup1.add(group12);

      final SubGroup group21 = new SubGroup();
      group21.setAddress(121);
      midGroup2.add(group21);

      return project;
   }

   //
   // It is not allowed to create objects of this class
   //
   private SampleProjectFactory()
   {
   }
}
