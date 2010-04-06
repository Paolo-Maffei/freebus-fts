package org.freebus.fts.components.parametereditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.persistence.EntityManagerFactory;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.VirtualDeviceService;
import org.freebus.fts.project.SampleProjectFactory;
import org.freebus.fts.test_utils.PersistenceTestCase;
import org.junit.Test;

public class TestParamData extends PersistenceTestCase
{
   private static final String persistenceUnitName = "test-full";
   private VirtualDevice virtualDevice;

   public TestParamData()
   {
      super(persistenceUnitName);

      final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(persistenceUnitName, conDetails);
      DatabaseResources.setEntityManagerFactory(emf);

      final ProductsFactory productsFactory = ProductsManager.getFactory();
      final VirtualDeviceService virtDevService = productsFactory.getVirtualDeviceService();

      virtualDevice = virtDevService.getVirtualDevice(SampleProjectFactory.sampleVirtualDeviceId);
      if (virtualDevice == null)
      {
         SampleProjectFactory.importSampleDevices(persistenceUnitName);

         virtualDevice = virtDevService.getVirtualDevice(SampleProjectFactory.sampleVirtualDeviceId);
         if (virtualDevice == null)
            throw new RuntimeException("sample virtual-device is missing");
      }
   }

   @Test
   public final void testParamData()
   {
      final Parameter param = virtualDevice.getProgram().getParameters().iterator().next();
      final ParamData data = new ParamData(param);

      assertEquals(param, data.getParameter());
      assertNull(data.getParentData());
   }

   @Test
   public final void testGetParentData()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testGetValue()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testSetValue()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testGetParameter()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testIsVisible()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testGetExpectedValue()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testGetParentValue()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testIsPage()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testGetDisplayOrder()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testAddDependent()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testGetDependents()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testRemoveAllDependents()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testHasDependents()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testEqualsObject()
   {
      fail("Not yet implemented");
   }

   @Test
   public final void testToString()
   {
      fail("Not yet implemented");
   }

}
