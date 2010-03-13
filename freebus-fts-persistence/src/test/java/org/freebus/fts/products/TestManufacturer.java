package org.freebus.fts.products;

import org.freebus.fts.products.Manufacturer;

import junit.framework.TestCase;

public class TestManufacturer extends TestCase
{
   public final void testHashCode()
   {
      Manufacturer manu1 = new Manufacturer(1234, "manu-1234");
      Manufacturer manu2 = new Manufacturer(1234, "manu-1234");
      assertEquals(manu1.hashCode(), manu2.hashCode());
   }

   public final void testManufacturer()
   {
      Manufacturer manu = new Manufacturer();
      assertEquals(0, manu.getId());
      assertEquals("", manu.getName());
      assertNotNull(manu.toString());
   }

   public final void testManufacturerIntString()
   {
      Manufacturer manu = new Manufacturer(12, "Tester");
      assertEquals(12, manu.getId());
      assertEquals("Tester", manu.getName());
   }

   public final void testGetSetId()
   {
      Manufacturer manu = new Manufacturer();
      assertEquals(0, manu.getId());

      manu.setId(1234);
      assertEquals(1234, manu.getId());

      manu.setId(88);
      assertEquals(88, manu.getId());
   }

   public final void testGetSetName()
   {
      Manufacturer manu = new Manufacturer();
      assertEquals("", manu.getName());

      manu.setName("Tester-2");
      assertEquals("Tester-2", manu.getName());

      manu.setName("");
      assertEquals("", manu.getName());

      manu.setName(null);
      assertEquals("", manu.getName());
   }

   public final void testEqualsObject()
   {
      Manufacturer manuA = new Manufacturer(12, "Tester-A");
      Manufacturer manuB = new Manufacturer(77, "Tester-B");
      Manufacturer manuA2 = new Manufacturer(12, "Tester-A");

      assertTrue(manuA.equals(manuA));
      assertTrue(manuA.equals(manuA2));
      assertTrue(manuA2.equals(manuA));
      assertFalse(manuA.equals(manuB));
      assertFalse(manuB.equals(manuA2));
      assertFalse(manuA.equals(null));
   }
}
