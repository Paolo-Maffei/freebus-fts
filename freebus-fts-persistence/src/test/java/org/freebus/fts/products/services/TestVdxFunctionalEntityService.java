package org.freebus.fts.products.services;

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
      assertEquals("Phys. Sensoren", ents.get(0).getName());
      assertEquals("Helligkeit und Temperatur", ents.get(1).getName());
      assertEquals(ents.get(0), ents.get(1).getParent());
   }

   @Test
   public final void testGetFunctionalEntitiesManufacturer()
   {
      List<FunctionalEntity> ents;

      ents = funcService.getFunctionalEntities(null);
      assertNotNull(ents);
      assertEquals(0, ents.size());

      final Manufacturer manu = manuService.getManufacturer(1);
      assertNotNull(manu);

      ents = funcService.getFunctionalEntities(manu);
      assertNotNull(ents);
      assertEquals(2, ents.size());

      assertEquals(funcService.getFunctionalEntity(22482), ents.get(0));
      assertEquals(funcService.getFunctionalEntity(22536), ents.get(1));

      final Manufacturer manu72 = manuService.getManufacturer(72);
      assertNotNull(manu72);

      ents = funcService.getFunctionalEntities(manu72);
      assertNotNull(ents);
      assertEquals(0, ents.size());
   }

   @Test
   public final void testGetFunctionalEntity()
   {
      assertNull(funcService.getFunctionalEntity(0));
      assertNull(funcService.getFunctionalEntity(-1));

      FunctionalEntity ent = funcService.getFunctionalEntity(22482);
      assertNotNull(ent);
      assertEquals(22482, ent.getId());

      ent = funcService.getFunctionalEntity(22536);
      assertNotNull(ent);
      assertEquals(22536, ent.getId());
   }
}
