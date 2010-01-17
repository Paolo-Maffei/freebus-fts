package test;

import java.util.List;

import org.freebus.fts.products.Products;
import org.freebus.fts.products.ProductsImporter;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.ProductsFactory;

public class TestProductsImporter extends PersistenceTestCase
{
   public final void testImportVirtualDevices() throws DAOException
   {
      final ProductsFactory vdFactory = Products.getFactory("src/test/resources/230V in LPC_.vd_");
      assertNotNull(vdFactory);

      final ProductsImporter importer = new ProductsImporter(vdFactory, getJpaProductsFactory());

      List<VirtualDevice> devs = vdFactory.getVirtualDeviceService().getVirtualDevices();
      assertNotNull(devs);
      assertFalse(devs.isEmpty());

      importer.copy(devs.get(0));
   }
}
