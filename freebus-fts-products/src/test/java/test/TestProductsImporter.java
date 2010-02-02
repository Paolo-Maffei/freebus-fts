package test;

import static org.junit.Assert.*;

import java.util.List;

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.products.Products;
import org.freebus.fts.products.ProductsImporter;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.ProductsFactory;
import org.junit.Test;

import test.internal.PersistenceTestCase;

public class TestProductsImporter extends PersistenceTestCase
{
   @Test
   public final void importVirtualDevices() throws DAOException
   {
      final ProductsFactory vdxFactory = Products.getFactory("src/test/resources/230V in LPC_.vd_");
      assertNotNull(vdxFactory);

      final ProductsFactory jpaFactory = getJpaProductsFactory();
      assertNotNull(jpaFactory);

      final ProductsImporter importer = new ProductsImporter(vdxFactory, jpaFactory);
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
      importer.copy(vdxDev);
      DatabaseResources.getEntityManager().flush();

      // Verify the imported objects
      final VirtualDevice jpaDev = jpaFactory.getVirtualDeviceService().getVirtualDevice(vdxDevId);
      assertNotNull(jpaDev);

      assertEquals(vdxDevId, jpaDev.getId());
      assertEquals(vdxDev.getFunctionalEntity(), jpaDev.getFunctionalEntity());
      assertEquals(vdxDev.getCatalogEntry(), jpaDev.getCatalogEntry());
   }
}
