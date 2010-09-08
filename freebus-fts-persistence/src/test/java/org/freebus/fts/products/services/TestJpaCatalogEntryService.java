package org.freebus.fts.products.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.ManufacturerService;
import org.freebus.fts.test_utils.ProductsTestCase;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit-tests for CatalogEntry persistence using JPA.
 */
public class TestJpaCatalogEntryService extends ProductsTestCase
{
   private CatalogEntryService catService;
   private ManufacturerService manuService;
   private Manufacturer manu1, manu2;

   @Before
   public void setUp() throws Exception
   {
      manuService = getJpaProductsFactory().getManufacturerService();
      catService = getJpaProductsFactory().getCatalogEntryService();

      manu1 = manuService.merge(new Manufacturer(1, "Manufacturer-1"));
      manu2 = manuService.merge(new Manufacturer(2, "Manufacturer-2"));

      catService.persist(new CatalogEntry("CatalogEntry-1", manu1));
      catService.persist(new CatalogEntry("CatalogEntry-2", manu1));
      catService.persist(new CatalogEntry("CatalogEntry-3", manu2));

      DatabaseResources.getEntityManager().flush();
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
      catService.persist(cat);

      assertTrue(cat.getId() != 0);
      assertEquals("CatalogEntry-4", cat.getName());

      final CatalogEntry cat2 = catService.getCatalogEntry(cat.getId());
      assertNotNull(cat2);
      assertEquals(cat.getId(), cat2.getId());
      assertEquals(cat.getName(), cat2.getName());
   }
}
