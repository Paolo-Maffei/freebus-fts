package org.freebus.fts.products.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.freebus.fts.persistence.db.ConnectionDetails;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;
import org.freebus.fts.persistence.exception.DAOException;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ParameterAtomicType;
import org.freebus.fts.products.ParameterType;
import org.freebus.fts.products.ParameterValue;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.importer.DirectProductsImporter;
import org.freebus.fts.products.services.ProductsFactory;
import org.junit.Test;

public class TestDirectProductsImporter
{
   private static final File productsFile = new File("src/test/resources/test-device.vd_");
   private static final File productsFile2 = new File("src/test/resources/test-device-2.vd_");

   @Test
   public final void importVirtualDevices() throws DAOException
   {
      final ConnectionDetails conDetails = new ConnectionDetails(DriverType.H2_MEM, "TestProductsImporter.importTwice");
      final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("test-full", conDetails);
      DatabaseResources.setEntityManagerFactory(emf);

      final EntityTransaction trans = DatabaseResources.getEntityManager().getTransaction();
      trans.begin();

      final ProductsFactory vdxFactory = ProductsManager.getFactory(productsFile, "test-full");
      assertNotNull(vdxFactory);

      final ProductsFactory jpaFactory = ProductsManager.getFactory();
      assertNotNull(jpaFactory);

      final ProductsImporter importer = new DirectProductsImporter(vdxFactory, jpaFactory);
      assertNotNull(importer);

      List<VirtualDevice> devs = vdxFactory.getVirtualDeviceService().getVirtualDevices();
      assertNotNull(devs);
      assertFalse(devs.isEmpty());

      final VirtualDevice vdxDev = devs.get(0);
      assertNotNull(vdxDev);
      assertNotNull(vdxDev.getFunctionalEntity());
      final int vdxDevId = vdxDev.getId();
      assertTrue(vdxDevId != 0);
      assertNull(jpaFactory.getVirtualDeviceService().getVirtualDevice(vdxDevId));

      // Do the import
      List<VirtualDevice> vdxDevs = new LinkedList<VirtualDevice>();
      vdxDevs.add(vdxDev);
      importer.copy(vdxDevs);
      DatabaseResources.getEntityManager().flush();

      // Verify the imported objects
      final VirtualDevice jpaDev = jpaFactory.getVirtualDeviceService().getVirtualDevice(vdxDevId);
      assertNotNull(jpaDev);

      assertEquals(vdxDevId, jpaDev.getId());
      assertEquals(vdxDev.getFunctionalEntity(), jpaDev.getFunctionalEntity());
      assertEquals(vdxDev.getCatalogEntry(), jpaDev.getCatalogEntry());

      final Parameter param = jpaDev.getProgram().getParameter(42854);
      assertEquals(150, param.getDisplayOrder());

      final ParameterType paramType = param.getParameterType();
      assertEquals("Anzahl der Temp. Schwellen", paramType.getName());
      assertEquals(ParameterAtomicType.ENUM, paramType.getAtomicType());

      final Set<ParameterValue> values = paramType.getValues();
      assertEquals(3, values.size());

      trans.rollback();
      DatabaseResources.close();
   }

   @Test
   public final void importTwice() throws Exception
   {
      final String persistenceUnitName = "test-full-keep-tables";
      final ConnectionDetails conDetails = new ConnectionDetails(DriverType.H2_MEM, "TestProductsImporter.importTwice");

      // Create a database connection so that the database does not get deleted within the
      // test steps
      final Connection con = DatabaseResources.createConnection(conDetails);

      int vdxDevId = 0;

      {
         final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(persistenceUnitName, conDetails);
         DatabaseResources.setEntityManagerFactory(emf);

         final ProductsFactory jpaFactory = ProductsManager.getFactory();
         final ProductsFactory vdxFactory = ProductsManager.getFactory(productsFile, persistenceUnitName);
         final ProductsImporter importer = new DirectProductsImporter(vdxFactory, jpaFactory);

         List<VirtualDevice> devs = vdxFactory.getVirtualDeviceService().getVirtualDevices();
         assertNotNull(devs);
         assertFalse(devs.isEmpty());

         final VirtualDevice vdxDev = devs.get(0);
         assertNotNull(vdxDev);
         assertNotNull(vdxDev.getFunctionalEntity());
         vdxDevId = vdxDev.getId();
         assertTrue(vdxDevId != 0);
         assertNull(jpaFactory.getVirtualDeviceService().getVirtualDevice(vdxDevId));

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
         final ProductsImporter importer = new DirectProductsImporter(vdxFactory, jpaFactory);

         // Ensure that the device from the previous part of the test is still in the
         // database
         assertTrue(vdxDevId != 0);
         assertNotNull(jpaFactory.getVirtualDeviceService().getVirtualDevice(vdxDevId));

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
