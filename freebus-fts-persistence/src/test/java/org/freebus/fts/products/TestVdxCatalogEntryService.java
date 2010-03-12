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
      Manufacturer manu = manuService.getManufacturer(1);
      assertNotNull(manu);
      assertEquals(1, manu.getId());
      assertEquals("Siemens", manu.getName());

      manu = manuService.getManufacturer(72);
      assertNotNull(manu);
      assertEquals(72, manu.getId());
      assertEquals("Theben AG", manu.getName());
   }

   @Test
   public final void testGetManufacturers()
   {
      List<Manufacturer> manus = manuService.getManufacturers();
      assertNotNull(manus);
      assertEquals(2, manus.size());
      assertEquals("Siemens", manus.get(0).getName());
      assertEquals("Theben AG", manus.get(1).getName());
   }

   @Test
   public final void testGetActiveManufacturers()
   {
      List<Manufacturer> manus = manuService.getActiveManufacturers();
      assertNotNull(manus);
      assertEquals(1, manus.size());
   }
}
