package org.freebus.fts.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.ManufacturerService;
import org.freebus.fts.test_utils.ProductsTestCase;
import org.junit.Before;
import org.junit.Test;


public class TestVdxCatalogEntryService extends ProductsTestCase
{
   private ManufacturerService manuService;

   @Before
   public void setUp() throws Exception
   {
      if (manuService == null)
         manuService = getVdxProductsFactory().getManufacturerService();
   }

   @Test
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

   @Test
   public final void testGetManufacturers()
   {
      List<Manufacturer> manus = manuService.getManufacturers();
      assertNotNull(manus);
      assertEquals(2, manus.size());
      assertEquals("Albrecht Jung", manus.get(0).getName());
      assertEquals("INSTA ELEKTRO", manus.get(1).getName());
   }

   @Test
   public final void testGetActiveManufacturers()
   {
      List<Manufacturer> manus = manuService.getActiveManufacturers();
      assertNotNull(manus);
      assertEquals(1, manus.size());
   }
}
