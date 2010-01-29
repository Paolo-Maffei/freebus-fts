package test;

import static org.junit.Assert.*;

import java.util.List;

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
