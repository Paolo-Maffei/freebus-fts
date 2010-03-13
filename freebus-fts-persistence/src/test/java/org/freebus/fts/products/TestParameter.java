package org.freebus.fts.products;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestParameter
{
   @Test
   public final void testGetSetId()
   {
      final Parameter param = new Parameter();
      assertEquals(0, param.getId());

      param.setId(12);
      assertEquals(12, param.getId());
   }

   @Test
   public final void testGetSetName()
   {
      final Parameter param = new Parameter();
      assertEquals("", param.getName());

      param.setName("param-1");
      assertEquals("param-1", param.getName());

      param.setName("");
      assertEquals("", param.getName());

      param.setName(null);
      assertEquals("", param.getName());
   }

   @Test
   public final void testGetSetLabel()
   {
      final Parameter param = new Parameter();
      assertEquals(null, param.getLabel());

      param.setLabel("str-1");
      assertEquals("str-1", param.getLabel());

      param.setLabel("");
      assertEquals("", param.getLabel());

      param.setLabel(null);
      assertEquals(null, param.getLabel());
   }

   @Test
   public final void testGetSetDescription()
   {
      final Parameter param = new Parameter();
      assertEquals(null, param.getDescription());

      param.setDescription("str-1");
      assertEquals("str-1", param.getDescription());

      param.setDescription("");
      assertEquals("", param.getDescription());

      param.setDescription(null);
      assertEquals(null, param.getDescription());
   }

   @Test
   public final void testToString()
   {
      final Parameter param = new Parameter();
      assertNotNull(param.toString());
   }

   @Test
   public final void testEqualsObject()
   {
      final Parameter param1 = new Parameter();
      final Parameter param2 = new Parameter();

      assertFalse(param1.equals(null));
      assertFalse(param1.equals(new Object()));

      assertTrue(param1.equals(param1));
      assertTrue(param1.equals(param2));
      assertTrue(param2.equals(param1));
   }
}
