package test;

import static org.junit.Assert.*;

import java.util.List;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.VirtualDeviceService;
import org.junit.Test;

import test.internal.PersistenceTestCase;

public class TestVdxVirtualDeviceService extends PersistenceTestCase
{
   private VirtualDeviceService virtDevService;
   private FunctionalEntityService funcEntService;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();

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

      final VirtualDevice virtDev = virtDevService.getVirtualDevice(160245);

      assertNotNull(virtDev);
      assertEquals(160245, virtDev.getId());

      assertNotNull(virtDev.getCatalogEntry());
      assertEquals(160239, virtDev.getCatalogEntry().getId());

      assertNotNull(virtDev.getFunctionalEntity());
      assertEquals(160234, virtDev.getFunctionalEntity().getId());
   }

   @Test
   public final void testGetVirtualDevices()
   {
      final List<VirtualDevice> virtDevs = virtDevService.getVirtualDevices();

      assertNotNull(virtDevs);
      assertEquals(1, virtDevs.size());
      assertEquals(160245, virtDevs.get(0).getId());
   }

   @Test
   public final void testGetVirtualDevicesFunctionalEntityArray()
   {
      List<VirtualDevice> virtDevs;

      virtDevs = virtDevService.getVirtualDevices(new FunctionalEntity[0]);
      assertNotNull(virtDevs);
      assertEquals(0, virtDevs.size());

      final FunctionalEntity funcEnt = funcEntService.getFunctionalEntity(160234);
      assertNotNull(funcEnt);

      virtDevs = virtDevService.getVirtualDevices(new FunctionalEntity[] { new FunctionalEntity() });
      assertNotNull(virtDevs);
      assertEquals(0, virtDevs.size());

      virtDevs = virtDevService.getVirtualDevices(new FunctionalEntity[] { funcEnt });
      assertNotNull(virtDevs);
      assertEquals(1, virtDevs.size());
      assertEquals(160245, virtDevs.get(0).getId());
   }

}
