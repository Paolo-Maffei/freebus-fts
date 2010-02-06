package test;

import static org.junit.Assert.*;

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Product;
import org.junit.Test;

public class TestCatalogEntry
{

   @Test
   public final void testHashCode()
   {
      final CatalogEntry catEnt1 = new CatalogEntry(123);
      final CatalogEntry catEnt2 = new CatalogEntry(123);
      assertEquals(catEnt1.hashCode(), catEnt2.hashCode());
   }

   @Test
   public final void testCatalogEntry()
   {
      final CatalogEntry catEnt1 = new CatalogEntry();
      assertEquals(0, catEnt1.getId());
      assertNull(catEnt1.getName());
      assertNull(catEnt1.getManufacturer());
      assertNull(catEnt1.getProduct());
      assertNotNull(catEnt1.toString());

      final Manufacturer manu = new Manufacturer(1, "manu-1");
      final Product prod = new Product(11, "prod-11", manu);

      final CatalogEntry catEnt2 = new CatalogEntry(123, "cat-ent-123", manu, prod);
      assertEquals(123, catEnt2.getId());
      assertEquals("cat-ent-123", catEnt2.getName());
      assertEquals(manu, catEnt2.getManufacturer());
      assertEquals(prod, catEnt2.getProduct());

      final CatalogEntry catEnt3 = new CatalogEntry("cat-ent-17", manu);
      assertEquals(0, catEnt3.getId());
      assertEquals("cat-ent-17", catEnt3.getName());
      assertEquals(manu, catEnt3.getManufacturer());
      assertNull(catEnt3.getProduct());
   }

   @Test
   public final void testGetSetIdName()
   {
      final CatalogEntry catEnt = new CatalogEntry();

      catEnt.setId(11);
      assertEquals(11, catEnt.getId());

      catEnt.setId(0);
      assertEquals(0, catEnt.getId());

      catEnt.setName("cat-ent-11");
      assertEquals("cat-ent-11", catEnt.getName());

      catEnt.setName(null);
      assertNull(catEnt.getName());
   }

   @Test
   public final void testGetSetWidthModulesMM()
   {
      final CatalogEntry catEnt = new CatalogEntry();

      catEnt.setWidthModules(4);
      assertEquals(4, catEnt.getWidthModules());

      catEnt.setWidthMM(36);
      assertEquals(36, catEnt.getWidthMM());

      catEnt.setWidthModules(0);
      assertEquals(0, catEnt.getWidthModules());

      catEnt.setWidthMM(0);
      assertEquals(0, catEnt.getWidthMM());
   }

   @Test
   public final void testGetSetDINOrderNumber()
   {
      final CatalogEntry catEnt = new CatalogEntry();

      catEnt.setDIN(true);
      assertTrue(catEnt.getDIN());

      catEnt.setDIN(false);
      assertFalse(catEnt.getDIN());

      catEnt.setOrderNumber("1234-123b");
      assertEquals("1234-123b", catEnt.getOrderNumber());
   }

   @Test
   public final void testGetSetColorSeries()
   {
      final CatalogEntry catEnt = new CatalogEntry();

      catEnt.setColor("mahagoni");
      assertEquals("mahagoni", catEnt.getColor());

      catEnt.setSeries("testing");
      assertEquals("testing", catEnt.getSeries());
   }

   @Test
   public final void testEqualsObject()
   {
      final Manufacturer manu1 = new Manufacturer(1, "manu-1");
      final Manufacturer manu2 = new Manufacturer(1, "manu-1");

      final Product prod1 = new Product(1, "prod-1", manu1);
      final Product prod2 = new Product(2, "prod-2", manu2);

      final CatalogEntry catEnt1 = new CatalogEntry(1, "cat-ent-1", manu1, prod1);
      final CatalogEntry catEnt2 = new CatalogEntry(1, "cat-ent-1", manu2, prod1);
      final CatalogEntry catEnt3 = new CatalogEntry(2, "cat-ent-2", manu2, prod2);

      assertTrue(catEnt1.equals(catEnt1));
      assertFalse(catEnt1.equals(null));
      assertFalse(catEnt1.equals(new Object()));
      assertTrue(catEnt1.equals(catEnt2));
      assertFalse(catEnt1.equals(catEnt3));
   }
}
