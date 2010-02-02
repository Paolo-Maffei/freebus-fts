package test;

import static org.junit.Assert.*;

import java.util.List;

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Product;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.VirtualDeviceService;
import org.junit.Test;

import test.internal.PersistenceTestCase;

public class TestJpaVirtualDeviceService extends PersistenceTestCase
{
   private VirtualDeviceService virtDevService;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();

      final ProductsFactory productsFactory = getJpaProductsFactory();
      virtDevService = productsFactory.getVirtualDeviceService();

      final Manufacturer manu = new Manufacturer(1, "manu-1");
      final FunctionalEntity funcEnt = new FunctionalEntity(1, manu, "func-ent-1", "func-ent-desc-1");
      final Product product = new Product(1, "prod-1", manu);
      final CatalogEntry catEnt = new CatalogEntry(3, "cat-ent-1", manu, product);

      productsFactory.getFunctionalEntityService().save(funcEnt);
      productsFactory.getCatalogEntryService().save(catEnt);
      virtDevService.save(new VirtualDevice(1, "virt-dev-1", "virt-dev-desc-1", funcEnt, catEnt));

      DatabaseResources.getEntityManager().flush();
   }

   @Test
   public final void getVirtualDevices()
   {
      List<VirtualDevice> virtDevs = virtDevService.getVirtualDevices();
      assertNotNull(virtDevs);
      assertEquals(1, virtDevs.size());
      assertEquals("virt-dev-1", virtDevs.get(0).getName());
   }

   @Test
   public final void getVirtualDevice()
   {
      assertNull(virtDevService.getVirtualDevice(-1));
      assertNull(virtDevService.getVirtualDevice(0));

      final VirtualDevice virtDev = virtDevService.getVirtualDevice(1);
      assertNotNull(virtDev);
   }

   @Test
   public final void save()
   {
      final VirtualDevice virtDev = new VirtualDevice(0, "virt-dev-2", "virt-dev-desc-2", null, null);
      virtDevService.save(virtDev);
      assertTrue(virtDev.getId() != 0);
   }
}
