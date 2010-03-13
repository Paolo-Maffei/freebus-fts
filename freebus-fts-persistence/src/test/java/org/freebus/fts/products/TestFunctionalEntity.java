package org.freebus.fts.products;

import junit.framework.TestCase;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.junit.Test;

public class TestFunctionalEntity extends TestCase
{
   @Test
   public final void testFunctionalEntity()
   {
      final FunctionalEntity fun = new FunctionalEntity();
      assertNotNull(fun);

      assertEquals(0, fun.getId());
      assertEquals("", fun.getName());
      assertEquals("", fun.getDescription());
      assertEquals(null, fun.getManufacturer());
      assertNotNull(fun.toString());
   }

   @Test
   public final void testFunctionalEntityIntManufacturerStringString()
   {
      final Manufacturer manu = new Manufacturer(17, "manu-17");

      final FunctionalEntity fun = new FunctionalEntity(123, manu, "fun-123", "descr-123");
      assertNotNull(fun);

      assertEquals(123, fun.getId());
      assertEquals("fun-123", fun.getName());
      assertEquals("descr-123", fun.getDescription());
      assertEquals(manu, fun.getManufacturer());
   }

   @Test
   public final void testGetSetParent()
   {
      final FunctionalEntity parentFun = new FunctionalEntity();
      final FunctionalEntity fun = new FunctionalEntity();

      assertNull(fun.getParent());

      fun.setParent(parentFun);
      assertEquals(parentFun, fun.getParent());

      fun.setParent(null);
      assertEquals(null, fun.getParent());
   }

   @Test
   public final void testEquals()
   {
      final FunctionalEntity fun1 = new FunctionalEntity();
      final FunctionalEntity fun2 = new FunctionalEntity();

      assertFalse(fun1.equals(null));
      assertFalse(fun1.equals(new Object()));

      assertTrue(fun1.equals(fun1));
      assertTrue(fun1.equals(fun2));
      assertTrue(fun2.equals(fun1));
      assertEquals(fun1.hashCode(), fun2.hashCode());

      fun1.setManufacturer(new Manufacturer(1, "manu-1"));
      assertFalse(fun1.equals(fun2));
      assertFalse(fun2.equals(fun1));

      fun2.setManufacturer(new Manufacturer(1, "manu-1"));
      assertTrue(fun1.equals(fun2));
      assertTrue(fun2.equals(fun1));

      fun1.setId(1);
      assertFalse(fun1.equals(fun2));
      assertFalse(fun2.equals(fun1));
   }
}
