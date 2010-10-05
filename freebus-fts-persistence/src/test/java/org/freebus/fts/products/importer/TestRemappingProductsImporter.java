package org.freebus.fts.products.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.freebus.fts.persistence.db.ConnectionDetails;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.ProductsImporter;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.ProgramDescription;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.ProductsFactory;
import org.junit.Test;

public class TestRemappingProductsImporter
{
   private static final File productsFile = new File("src/test/resources/test-device.vd_");
   private static final File productsFile2 = new File("src/test/resources/test-device-2.vd_");

   @Test
   public final void importVirtualDevices() throws DAOException
   {
      final ConnectionDetails conDetails = new ConnectionDetails(DriverType.HSQL_MEM, "TestProductsImporter.importTwice");
      final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("test-full", conDetails);
      DatabaseResources.setEntityManagerFactory(emf);

      final EntityTransaction trans = DatabaseResources.getEntityManager().getTransaction();
      trans.begin();

      final ProductsFactory vdxFactory = ProductsManager.getFactory(productsFile, "test-full");
      assertNotNull(vdxFactory);

      final ProductsFactory jpaFactory = ProductsManager.getFactory();
      assertNotNull(jpaFactory);

      final ProductsImporter importer = new RemappingProductsImporter(vdxFactory, jpaFactory);
      assertNotNull(importer);

      List<VirtualDevice> devs = vdxFactory.getVirtualDeviceService().getVirtualDevices();
      assertNotNull(devs);
      assertFalse(devs.isEmpty());

      final VirtualDevice vdxDev = devs.get(0);
      assertNotNull(vdxDev);
      assertNotNull(vdxDev.getFunctionalEntity());
      final String vdxDevName = vdxDev.getName();
      final Manufacturer vdxDevManu = vdxDev.getCatalogEntry().getManufacturer();
      assertFalse(vdxDevName.isEmpty());

      // Do the import
      List<VirtualDevice> vdxDevs = new LinkedList<VirtualDevice>();
      vdxDevs.add(vdxDev);
      importer.copy(vdxDevs);
      DatabaseResources.getEntityManager().flush();

      // Verify the imported objects
      final Manufacturer jpaDevManu = jpaFactory.getManufacturerService().getManufacturer(vdxDevManu.getId());
      final VirtualDevice jpaDev = jpaFactory.getVirtualDeviceService().getVirtualDevice(jpaDevManu, vdxDevName);
      assertNotNull(jpaDev);

      assertEquals(vdxDevName, jpaDev.getName());
      assertEquals(vdxDev.getFunctionalEntity(), jpaDev.getFunctionalEntity());
      assertEquals(vdxDev.getCatalogEntry(), jpaDev.getCatalogEntry());

      final Program prog = jpaDev.getProgram();
      assertNotNull(prog);
      final ProgramDescription progDesc = prog.getDescription();
      assertNotNull(progDesc);
      assertFalse(progDesc.getDescription().isEmpty());

      trans.rollback();
      DatabaseResources.close();
   }

   @Test
   public final void importTwice() throws Exception
   {
      final String persistenceUnitName = "test-full-keep-tables";
      final ConnectionDetails conDetails = new ConnectionDetails(DriverType.HSQL_MEM, "TestProductsImporter.importTwice");

      // Create a database connection so that the database does not get deleted within the
      // test steps
      final Connection con = DatabaseResources.createConnection(conDetails);

      String vdxDevName = null;
      Manufacturer vdxDevManu = null;

      {
         final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(persistenceUnitName, conDetails);
         DatabaseResources.setEntityManagerFactory(emf);

         final ProductsFactory jpaFactory = ProductsManager.getFactory();
         final ProductsFactory vdxFactory = ProductsManager.getFactory(productsFile, persistenceUnitName);
         final ProductsImporter importer = new RemappingProductsImporter(vdxFactory, jpaFactory);

         List<VirtualDevice> devs = vdxFactory.getVirtualDeviceService().getVirtualDevices();
         assertNotNull(devs);
         assertFalse(devs.isEmpty());

         final VirtualDevice vdxDev = devs.get(0);
         assertNotNull(vdxDev);
         assertNotNull(vdxDev.getFunctionalEntity());
         vdxDevName = vdxDev.getName();
         vdxDevManu = vdxDev.getCatalogEntry().getManufacturer();
         assertFalse(vdxDevName.isEmpty());

         // Do the import
         final EntityTransaction trans = DatabaseResources.getEntityManager().getTransaction();
         trans.begin();
         List<VirtualDevice> vdxDevs = new LinkedList<VirtualDevice>();
         vdxDevs.add(vdxDev);
         importer.copy(vdxDevs);
         trans.commit();

         // Close the entity manager
         DatabaseResources.close();
      }

      {
         final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(persistenceUnitName, conDetails);
         DatabaseResources.setEntityManagerFactory(emf);

         final ProductsFactory jpaFactory = ProductsManager.getFactory();
         final ProductsFactory vdxFactory = ProductsManager.getFactory(productsFile2, persistenceUnitName);
         final ProductsImporter importer = new RemappingProductsImporter(vdxFactory, jpaFactory);

         // Ensure that the device from the previous part of the test is still in the
         // database
         final Manufacturer jpaDevManu = jpaFactory.getManufacturerService().getManufacturer(vdxDevManu.getId());
         assertNotNull(jpaFactory.getVirtualDeviceService().getVirtualDevice(jpaDevManu, vdxDevName));

         List<VirtualDevice> devs = vdxFactory.getVirtualDeviceService().getVirtualDevices();
         assertNotNull(devs);
         assertFalse(devs.isEmpty());

         final VirtualDevice vdxDev = devs.get(0);
         assertNotNull(vdxDev);
         assertNotNull(vdxDev.getFunctionalEntity());
         final int vdxDevId2 = vdxDev.getId();
         assertTrue(vdxDevId2 != 0);

         // Do another import. Should not cause problems.
         final EntityTransaction trans = DatabaseResources.getEntityManager().getTransaction();
         trans.begin();
         List<VirtualDevice> vdxDevs = new LinkedList<VirtualDevice>();
         vdxDevs.add(vdxDev);
         importer.copy(vdxDevs);
         trans.commit();
      }

      con.close();
   }
}
