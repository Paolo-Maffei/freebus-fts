package org.freebus.fts.products.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.ManufacturerService;
import org.freebus.fts.test_utils.ProductsTestCase;
import org.junit.Before;
import org.junit.Test;


public class TestJpaManufacturerService extends ProductsTestCase
{
   private ManufacturerService manuService;

   @Before
   public void setUp() throws Exception
   {
      manuService = getJpaProductsFactory().getManufacturerService();

      manuService.persist(new Manufacturer(1, "Manufacturer-1"));
      manuService.persist(new Manufacturer(2, "Manufacturer-2"));
      manuService.persist(new Manufacturer(3, "Manufacturer-3"));
      DatabaseResources.getEntityManager().flush();
   }

   @Test
   public final void getManufacturers()
   {
      List<Manufacturer> manus = manuService.getManufacturers();
      assertNotNull(manus);
      assertEquals(3, manus.size());
      assertTrue(manus.contains(new Manufacturer(1, "Manufacturer-1")));
   }

   @Test
   public final void getManufacturer()
   {
      manuService.merge(new Manufacturer(10, "Manufacturer-10"));

      Manufacturer manu = manuService.getManufacturer(10);
      assertNotNull(manu);
      assertEquals(10, manu.getId());
      assertEquals("Manufacturer-10", manu.getName());
   }

   @Test
   public final void getActiveManufacturers()
   {
      List<Manufacturer> manus = manuService.getActiveManufacturers();
      assertNotNull(manus);
      assertEquals(0, manus.size());
   }
}
