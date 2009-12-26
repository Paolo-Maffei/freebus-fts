package test;

import java.util.LinkedList;
import java.util.List;

import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.ManufacturerService;

public class TestManufacturerService extends PersistanceTestCase
{
   private ManufacturerService manuService;

   @Override
   public void setUp() throws Exception
   {
      if (manuService == null)
         manuService = getProductsFactory().getManufacturerService();

      final List<Manufacturer> manus = new LinkedList<Manufacturer>();
      manus.add(new Manufacturer(1, "Manufacturer-1"));
      manus.add(new Manufacturer(2, "Manufacturer-2"));
      manus.add(new Manufacturer(3, "Manufacturer-3"));
      manuService.save(manus);
   }

   public final void testGetManufacturer()
   {
      manuService.save(new Manufacturer(1, "Manufacturer-1"));

      Manufacturer manu = manuService.getManufacturer(1);
      assertNotNull(manu);
      assertEquals(1, manu.getId());
      assertEquals("Manufacturer-1", manu.getName());
   }

   public final void testGetManufacturers()
   {
      manuService.save(new Manufacturer(1, "Manufacturer-1"));

      List<Manufacturer> manus = manuService.getManufacturers();
      assertNotNull(manus);
      assertEquals(3, manus.size());
      assertEquals("Manufacturer-1", manus.get(0).getName());
   }

   public final void testGetActiveManufacturers()
   {
      List<Manufacturer> manus = manuService.getActiveManufacturers();
      assertNotNull(manus);
      assertEquals(0, manus.size());
   }
}
