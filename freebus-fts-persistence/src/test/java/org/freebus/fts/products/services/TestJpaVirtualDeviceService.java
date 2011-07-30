package org.freebus.fts.products.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Product;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.test_utils.ProductsTestCase;
import org.junit.Before;
import org.junit.Test;


public class TestJpaVirtualDeviceService extends ProductsTestCase
{
   private VirtualDeviceService virtDevService;
   private VirtualDevice virtDev1;
   private Manufacturer manu;

   @Before
   public void setUp() throws Exception
   {
      Logger.getLogger(getClass()).debug("TestJpaVirtualDeviceService.setUp");

      final ProductsFactory productsFactory = getJpaProductsFactory();
      virtDevService = productsFactory.getVirtualDeviceService();

      manu = new Manufacturer(100, "manu-100");
      productsFactory.getManufacturerService().persist(manu);

      final FunctionalEntity funcEnt = new FunctionalEntity(1234, manu, "func-ent-1", "func-ent-desc-1");
      final Product product = new Product(1234, "prod-1", manu);
      final CatalogEntry catEnt = new CatalogEntry(321, "cat-ent-1", manu, product);

      productsFactory.getFunctionalEntityService().persist(funcEnt);
      productsFactory.getCatalogEntryService().persist(catEnt);
      virtDev1 = new VirtualDevice(1, "virt-dev-1", "virt-dev-desc-1", funcEnt, catEnt);
      virtDevService.persist(virtDev1);
      virtDevService.persist(new VirtualDevice(2, "virt-dev-2", "virt-dev-desc-2", funcEnt, catEnt));

      DatabaseResources.getEntityManager().flush();
      DatabaseResources.getEntityManager().clear();
   }

   // Test fails if all freebus-fts-persistence unit tests are started e.g. from within
   // Eclipse.
   //@Test
   public final void getVirtualDevices()
   {
      List<VirtualDevice> virtDevs = virtDevService.getVirtualDevices();
      assertNotNull(virtDevs);
      assertEquals(2, virtDevs.size());
      assertTrue(virtDevs.contains(virtDev1));
   }

   @Test
   public final void getVirtualDeviceId()
   {
      assertNull(virtDevService.getVirtualDevice(-1));
      assertNull(virtDevService.getVirtualDevice(0));
      assertNotNull(virtDevService.getVirtualDevice(1));
   }

   @Test
   public final void getVirtualDeviceName()
   {
      final VirtualDevice virtDev = virtDevService.getVirtualDevice(manu, "virt-dev-1");
      assertNotNull(virtDev);
      assertEquals("virt-dev-1", virtDev.getName());
   }

   @Test(expected = NoResultException.class)
   public final void getVirtualDeviceNameInvalidName()
   {
      virtDevService.getVirtualDevice(manu, "no-such-device");
   }

   @Test(expected = NoResultException.class)
   public final void getVirtualDeviceNameInvalidManufacturer()
   {
      virtDevService.getVirtualDevice(new Manufacturer(7715, "manu-7715"), "virt-dev-1");
   }
}
