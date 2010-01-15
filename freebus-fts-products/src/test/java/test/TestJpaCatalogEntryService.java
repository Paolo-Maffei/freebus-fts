package test;

import java.util.List;

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.ManufacturerService;

/**
 * Unit-tests for CatalogEntry persistence using JPA.
 */
public class TestJpaCatalogEntryService extends PersistenceTestCase
{
   private CatalogEntryService catService;
   private ManufacturerService manuService;
   private Manufacturer manu1, manu2;
   private int catId;

   @Override
   public void setUp() throws Exception
   {
      if (manuService == null)
         manuService = getJpaProductsFactory().getManufacturerService();

      manu1 = new Manufacturer(1, "Manufacturer-1");
      manu2 = new Manufacturer(2, "Manufacturer-2");
      manuService.save(manu1);
      manuService.save(manu2);

      if (catService == null)
    	  catService = getJpaProductsFactory().getCatalogEntryService();

      final CatalogEntry cat = new CatalogEntry("CatalogEntry-1", manu1);
      catService.save(cat);
      catId = cat.getId();

      catService.save(new CatalogEntry("CatalogEntry-2", manu1));
      catService.save(new CatalogEntry("CatalogEntry-3", manu2));
   }

   public final void testGetCatalogEntries()
   {
      final List<CatalogEntry> cats = catService.getCatalogEntries();
      assertNotNull(cats);
      assertEquals(3, cats.size());
   }

   public final void getCatalogEntriesManufacturerFunctionalEntity()
   {
      final List<CatalogEntry> cats = catService.getCatalogEntries(manu1, null);
      assertNotNull(cats);
      assertEquals(2, cats.size());
   }

   public final void testSave()
   {
      final CatalogEntry cat = new CatalogEntry("CatalogEntry-4", manu2);
      catService.save(cat);

      assertTrue(cat.getId() != 0);
      assertEquals("CatalogEntry-4", cat.getName());
   }

   public final void testGetCatalogEntry()
   {
      final CatalogEntry cat = catService.getCatalogEntry(catId);
      assertNotNull(cat);
      assertEquals(catId, cat.getId());
      assertEquals("CatalogEntry-1", cat.getName());
   }
}
