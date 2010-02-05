package org.freebus.fts.persistence.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestDriverType
{
   @Test
   public final void testGetDefault()
   {
      assertNotNull(DriverType.getDefault());
      assertEquals(DriverType.class, DriverType.getDefault().getClass());
   }

   @Test
   public final void testGetConnectURL()
   {
      assertEquals("jdbc:hsqldb:mem:loc;create=true;shutdown=true", DriverType.HSQL_MEM.getConnectURL("loc"));
   }

   @Test
   public final void testGetConfigPrefix()
   {
      assertEquals("databaseHSQL_MEM", DriverType.HSQL_MEM.getConfigPrefix());
   }

   @Test
   public final void testValueOf()
   {
      assertEquals(DriverType.HSQL, DriverType.valueOf("HSQL"));
   }
}
