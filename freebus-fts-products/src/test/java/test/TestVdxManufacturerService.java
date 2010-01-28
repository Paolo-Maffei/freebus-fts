package test;

import java.util.List;

import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.ManufacturerService;

import test.internal.PersistenceTestCase;

public class TestVdxManufacturerService extends PersistenceTestCase
{
   private ManufacturerService manuService;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();

      if (manuService == null)
         manuService = getVdxProductsFactory().getManufacturerService();
   }

   public final void testGetManufacturer()
   {
      Manufacturer manu = manuService.getManufacturer(4);
      assertNotNull(manu);
      assertEquals(4, manu.getId());
      assertEquals("Albrecht Jung", manu.getName());

      manu = manuService.getManufacturer(10);
      assertNotNull(manu);
      assertEquals(10, manu.getId());
      assertEquals("INSTA ELEKTRO", manu.getName());
   }

   public final void testGetManufacturers()
   {
      List<Manufacturer> manus = manuService.getManufacturers();
      assertNotNull(manus);
      assertEquals(2, manus.size());
      assertEquals("Albrecht Jung", manus.get(0).getName());
      assertEquals("INSTA ELEKTRO", manus.get(1).getName());
   }

   public final void testGetActiveManufacturers()
   {
      List<Manufacturer> manus = manuService.getActiveManufacturers();
      assertNotNull(manus);
      assertEquals(1, manus.size());
   }
}