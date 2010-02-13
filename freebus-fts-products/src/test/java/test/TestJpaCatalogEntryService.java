package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.ManufacturerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.internal.PersistenceTestCase;

/**
 * Unit-tests for CatalogEntry persistence using JPA.
 */
public class TestJpaCatalogEntryService extends PersistenceTestCase
{
   private CatalogEntryService catService;
   private ManufacturerService manuService;
   private Manufacturer manu1, manu2;

   @Before
   @Override
   public void setUp() throws Exception
   {
      super.setUp();

      if (manuService == null)
         manuService = getJpaProductsFactory().getManufacturerService();

      if (catService == null)
          catService = getJpaProductsFactory().getCatalogEntryService();

      manu1 = new Manufacturer(1, "Manufacturer-1");
      manu2 = new Manufacturer(2, "Manufacturer-2");
      manuService.save(manu1);
      manuService.save(manu2);

      catService.save(new CatalogEntry("CatalogEntry-1", manu1));
      catService.save(new CatalogEntry("CatalogEntry-2", manu1));
      catService.save(new CatalogEntry("CatalogEntry-3", manu2));

      DatabaseResources.getEntityManager().flush();
   }

   @After
   @Override
   public void tearDown() throws Exception
   {
      super.tearDown();
   }

   @Test
   public final void getCatalogEntries()
   {
      final List<CatalogEntry> cats = catService.getCatalogEntries();
      assertNotNull(cats);
      assertEquals(3, cats.size());
   }

   @Test
   public final void saveGetCatalogEntry()
   {
      final CatalogEntry cat = new CatalogEntry("CatalogEntry-4", manu2);
      catService.save(cat);

      assertTrue(cat.getId() != 0);
      assertEquals("CatalogEntry-4", cat.getName());

      final CatalogEntry cat2 = catService.getCatalogEntry(cat.getId());
      assertNotNull(cat2);
      assertEquals(cat.getId(), cat2.getId());
      assertEquals(cat.getName(), cat2.getName());
   }
}
