package org.freebus.fts.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ManufacturerService;
import org.freebus.fts.test_utils.ProductsTestCase;
import org.junit.Before;
import org.junit.Test;


public class TestVdxFunctionalEntityService extends ProductsTestCase
{
   private FunctionalEntityService funcService;
   private ManufacturerService manuService;

   @Before
   public void setUp() throws Exception
   {
      if (funcService == null)
         funcService = getVdxProductsFactory().getFunctionalEntityService();

      if (manuService == null)
         manuService = getVdxProductsFactory().getManufacturerService();
   }

   @Test
   public final void testGetFunctionalEntities()
   {
      List<FunctionalEntity> ents = funcService.getFunctionalEntities();
      assertNotNull(ents);
      assertEquals(2, ents.size());
      assertEquals("Eingabe", ents.get(0).getName());
      assertEquals("Binäreingang, 8fach", ents.get(1).getName());
      assertEquals(ents.get(0), ents.get(1).getParent());
   }

   @Test
   public final void testGetFunctionalEntitiesManufacturer()
   {
      List<FunctionalEntity> ents;

      ents = funcService.getFunctionalEntities(null);
      assertNotNull(ents);
      assertEquals(0, ents.size());

      final Manufacturer manu4 = manuService.getManufacturer(4);
      assertNotNull(manu4);

      ents = funcService.getFunctionalEntities(manu4);
      assertNotNull(ents);
      assertEquals(2, ents.size());

      assertEquals(funcService.getFunctionalEntity(22804), ents.get(0));
      assertEquals(funcService.getFunctionalEntity(160234), ents.get(1));

      final Manufacturer manu10 = manuService.getManufacturer(10);
      assertNotNull(manu10);

      ents = funcService.getFunctionalEntities(manu10);
      assertNotNull(ents);
      assertEquals(0, ents.size());
   }

   @Test
   public final void testGetFunctionalEntity()
   {
      assertNull(funcService.getFunctionalEntity(0));
      assertNull(funcService.getFunctionalEntity(-1));

      FunctionalEntity ent = funcService.getFunctionalEntity(22804);
      assertNotNull(ent);
      assertEquals(22804, ent.getId());

      ent = funcService.getFunctionalEntity(160234);
      assertNotNull(ent);
      assertEquals(160234, ent.getId());

      ent = funcService.getFunctionalEntity(22804);
      assertNotNull(ent);
      assertEquals(22804, ent.getId());
   }
}
