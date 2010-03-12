package org.freebus.fts.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.VirtualDeviceService;
import org.freebus.fts.test_utils.ProductsTestCase;
import org.junit.Before;
import org.junit.Test;


public class TestVdxVirtualDeviceService extends ProductsTestCase
{
   private VirtualDeviceService virtDevService;
   private FunctionalEntityService funcEntService;

   @Before
   public void setUp() throws Exception
   {
      if (virtDevService == null)
         virtDevService = getVdxProductsFactory().getVirtualDeviceService();

      if (funcEntService == null)
         funcEntService = getVdxProductsFactory().getFunctionalEntityService();
   }

   @Test
   public final void testGetVirtualDevice()
   {
      assertNull(virtDevService.getVirtualDevice(-1));
      assertNull(virtDevService.getVirtualDevice(0));

      final VirtualDevice virtDev = virtDevService.getVirtualDevice(23652);

      assertNotNull(virtDev);
      assertEquals(23652, virtDev.getId());

      assertNotNull(virtDev.getCatalogEntry());
      assertEquals(23101, virtDev.getCatalogEntry().getId());

      assertNotNull(virtDev.getFunctionalEntity());
      assertEquals(22536, virtDev.getFunctionalEntity().getId());
   }

   @Test
   public final void testGetVirtualDevices()
   {
      final List<VirtualDevice> virtDevs = virtDevService.getVirtualDevices();

      assertNotNull(virtDevs);
      assertEquals(1, virtDevs.size());
      assertEquals(23652, virtDevs.get(0).getId());
   }

   @Test
   public final void testGetVirtualDevicesFunctionalEntityArray()
   {
      List<VirtualDevice> virtDevs;

      virtDevs = virtDevService.getVirtualDevices(new FunctionalEntity[0]);
      assertNotNull(virtDevs);
      assertEquals(0, virtDevs.size());

      final FunctionalEntity funcEnt = funcEntService.getFunctionalEntity(22536);
      assertNotNull(funcEnt);

      virtDevs = virtDevService.getVirtualDevices(new FunctionalEntity[] { new FunctionalEntity() });
      assertNotNull(virtDevs);
      assertEquals(0, virtDevs.size());

      virtDevs = virtDevService.getVirtualDevices(new FunctionalEntity[] { funcEnt });
      assertNotNull(virtDevs);
      assertEquals(1, virtDevs.size());
      assertEquals(23652, virtDevs.get(0).getId());
   }

}
