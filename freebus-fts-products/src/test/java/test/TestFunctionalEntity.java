package test;

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
      assertEquals(null, fun.getName());
      assertEquals(null, fun.getDescription());
      assertEquals(null, fun.getManufacturer());
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
}
