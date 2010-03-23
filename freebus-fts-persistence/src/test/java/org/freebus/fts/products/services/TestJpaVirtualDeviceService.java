package org.freebus.fts.products.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

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

   @Before
   public void setUp() throws Exception
   {
      final ProductsFactory productsFactory = getJpaProductsFactory();
      virtDevService = productsFactory.getVirtualDeviceService();

      final Manufacturer manu1 = new Manufacturer(1, "manu-1");
      productsFactory.getManufacturerService().save(manu1);

      final FunctionalEntity funcEnt = new FunctionalEntity(1234, manu1, "func-ent-1", "func-ent-desc-1");
      final Product product = new Product(1234, "prod-1", manu1);
      final CatalogEntry catEnt = new CatalogEntry(321, "cat-ent-1", manu1, product);

      productsFactory.getFunctionalEntityService().save(funcEnt);
      productsFactory.getCatalogEntryService().save(catEnt);
      virtDevService.save(new VirtualDevice(1, "virt-dev-1", "virt-dev-desc-1", funcEnt, catEnt));
      virtDevService.save(new VirtualDevice(2, "virt-dev-2", "virt-dev-desc-2", funcEnt, catEnt));

      DatabaseResources.getEntityManager().flush();
      DatabaseResources.getEntityManager().clear();
   }

   @Test
   public final void getVirtualDevices()
   {
      List<VirtualDevice> virtDevs = virtDevService.getVirtualDevices();
      assertNotNull(virtDevs);
      assertEquals(2, virtDevs.size());
      assertEquals("virt-dev-1", virtDevs.get(0).getName());
      assertEquals("virt-dev-2", virtDevs.get(1).getName());
   }

   @Test
   public final void getVirtualDevice()
   {
      assertNull(virtDevService.getVirtualDevice(-1));
      assertNull(virtDevService.getVirtualDevice(0));

      final VirtualDevice virtDev = virtDevService.getVirtualDevice(1);
      assertNotNull(virtDev);
   }
}
