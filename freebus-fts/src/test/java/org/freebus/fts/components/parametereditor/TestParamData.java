package org.freebus.fts.components.parametereditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
      final Parameter param = virtualDevice.getProgram().getParameter(40229);
      assertNotNull(param);

      final ParamData data = new ParamData(param);

      assertEquals(param, data.getParameter());
      assertNull(data.getParent());
      assertEquals(param.getDefault(), data.getValue());
      assertFalse(data.hasChildren());
      assertTrue(data.isPage());
      assertEquals(0, data.getChildren().size());
      assertEquals(param.getDisplayOrder(), data.getDisplayOrder());
      assertNotNull(data.toString());
   }

   @Test
   public final void testGetSetValue()
   {
      final Parameter param = virtualDevice.getProgram().getParameter(40229);
      assertNotNull(param);
      final ParamData data = new ParamData(param);

      data.setValue(4711);
      assertEquals(4711, data.getValue());
   }

   @Test
   public final void testIsVisible()
   {
      final Parameter parentParam = virtualDevice.getProgram().getParameter(42877);
      assertNotNull(parentParam);
      final ParamData parentData = new ParamData(parentParam);

      final Parameter param = virtualDevice.getProgram().getParameter(44912);
      assertNotNull(param);
      final ParamData data = new ParamData(param);
      assertFalse(data.isPage());

      parentData.addChild(data);
      assertEquals(parentData, data.getParent());
      assertTrue(parentData.hasChildren());
      assertTrue(parentData.getChildren().contains(data));

      assertTrue(parentData.isVisible());

      assertFalse(data.isVisible());
      assertFalse(data.getExpectedValue().equals(parentData.getValue()));

      parentData.setValue(data.getExpectedValue());

      assertTrue(data.isVisible());
      assertTrue(data.getExpectedValue().equals(parentData.getValue()));
   }

   @Test
   public final void testAddRemoveChildren()
   {
      final Parameter parentParam = virtualDevice.getProgram().getParameter(42877);
      assertNotNull(parentParam);
      final ParamData parentData1 = new ParamData(parentParam);
      final ParamData parentData2 = new ParamData(parentParam);

      final Parameter param1 = virtualDevice.getProgram().getParameter(44912);
      final ParamData data1 = new ParamData(param1);

      final Parameter param2 = virtualDevice.getProgram().getParameter(42878);
      final ParamData data2 = new ParamData(param2);

      parentData1.addChild(data1);
      assertTrue(parentData1.hasChildren());
      assertEquals(parentData1, data1.getParent());
      assertTrue(parentData1.getChildren().contains(data1));
      assertFalse(parentData1.getChildren().contains(data2));

      parentData1.addChild(data2);
      assertTrue(parentData1.hasChildren());
      assertEquals(parentData1, data2.getParent());
      assertTrue(parentData1.getChildren().contains(data1));
      assertTrue(parentData1.getChildren().contains(data2));

      parentData2.addChild(data1);
      assertEquals(parentData2, data1.getParent());
      assertTrue(parentData1.hasChildren());
      assertTrue(parentData2.hasChildren());
      assertTrue(parentData2.getChildren().contains(data1));
      assertFalse(parentData1.getChildren().contains(data1));

      parentData1.removeAllChildren();
      assertFalse(parentData1.hasChildren());

      parentData1.addChild(data2);
      assertTrue(parentData1.hasChildren());
   }

   @Test
   public final void testGetParentValue()
   {
      final Parameter parentParam = virtualDevice.getProgram().getParameter(42877);
      assertNotNull(parentParam);
      final ParamData parentData = new ParamData(parentParam);

      final Parameter param = virtualDevice.getProgram().getParameter(44912);
      assertNotNull(param);
      final ParamData data = new ParamData(param);
      parentData.addChild(data);

      assertNull(parentData.getParentValue());
      assertEquals(parentData.getValue(), data.getParentValue());
   }

   @Test
   public final void testEqualsObject()
   {
      final Parameter param1 = virtualDevice.getProgram().getParameter(44912);
      final ParamData data1 = new ParamData(param1);
      final ParamData data2 = new ParamData(param1);

      final Parameter param3 = virtualDevice.getProgram().getParameter(42878);
      final ParamData data3 = new ParamData(param3);

      assertTrue(data1.equals(data1));
      assertFalse(data1.equals(null));
      assertFalse(data1.equals(new Object()));
      assertTrue(data1.equals(data2));
      assertTrue(data2.equals(data1));
      assertFalse(data1.equals(data3));
   }
}
