package org.freebus.fts.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.test_utils.ProductsTestCase;
import org.junit.Before;
import org.junit.Test;


public class TestProductsImporter extends ProductsTestCase
{
   private static final File productsFile = new File("src/test/resources/test-device.vd_");

   @Before
   public final void setUp()
   {
      
   }

   @Test
   public final void importVirtualDevices() throws DAOException
   {
      final ProductsFactory vdxFactory = ProductsManager.getFactory(productsFile, "test-full");
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

      final Parameter param = jpaDev.getProgram().getParameter(42854);
      assertEquals(150, param.getDisplayOrder());

      final ParameterType paramType = param.getParameterType();
      assertEquals("Anzahl der Temp. Schwellen", paramType.getName());
      assertEquals(ParameterAtomicType.ENUM, paramType.getAtomicType());

      final Set<ParameterValue> values = paramType.getValues();
      assertEquals(3, values.size());
   }
}
