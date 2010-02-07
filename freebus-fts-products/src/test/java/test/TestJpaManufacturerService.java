package test;

import static org.junit.Assert.*;

import java.util.List;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.ManufacturerService;
import org.junit.Test;

import test.internal.PersistenceTestCase;

public class TestJpaManufacturerService extends PersistenceTestCase
{
   private ManufacturerService manuService;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();

      manuService = getJpaProductsFactory().getManufacturerService();

      manuService.save(new Manufacturer(1, "Manufacturer-1"));
      manuService.save(new Manufacturer(2, "Manufacturer-2"));
      manuService.save(new Manufacturer(3, "Manufacturer-3"));
      DatabaseResources.getEntityManager().flush();
   }

   @Test
   public final void getManufacturers()
   {
      List<Manufacturer> manus = manuService.getManufacturers();
      assertNotNull(manus);
      assertEquals(3, manus.size());
      assertEquals("Manufacturer-1", manus.get(0).getName());
   }

   @Test
   public final void getManufacturer()
   {
      manuService.save(new Manufacturer(10, "Manufacturer-10"));

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
