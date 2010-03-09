package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ParameterAtomicType;
import org.freebus.fts.products.ParameterType;
import org.freebus.fts.products.ParameterValue;
import org.freebus.fts.products.ProductsImporter;
import org.freebus.fts.products.ProductsManager;
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
      final ProductsFactory vdxFactory = ProductsManager.getFactory("src/test/resources/230V in LPC_.vd_");
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

      final Parameter param = jpaDev.getProgram().getParameter(161483);
      assertEquals(9001, param.getDisplayOrder());

      final ParameterType paramType = param.getParameterType();
      assertEquals("Funktion_Auswahl", paramType.getName());
      assertEquals(ParameterAtomicType.ENUM, paramType.getAtomicType());

      final Set<ParameterValue> values = paramType.getValues();
      assertEquals(5, values.size());
//      assertTrue(values.iterator().next().getDisplayOrder() != 0);
   }
}
