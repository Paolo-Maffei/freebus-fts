package org.freebus.fts.persistence.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.freebus.fts.common.SimpleConfig;
import org.junit.Test;

public class TestConnectionDetails
{
   @Test
   public final void testConnectionDetailsDriverTypeStringStringStringString()
   {
      final ConnectionDetails cd = new ConnectionDetails(DriverType.HSQL_MEM, "dbname", "host", "user", "passwd");
      assertEquals(DriverType.HSQL_MEM, cd.getType());
      assertEquals("dbname", cd.getDbName());
      assertEquals("host", cd.getHost());
      assertEquals("user", cd.getUser());
      assertEquals("passwd", cd.getPassword());
   }

   @Test
   public final void testSetDefaults()
   {
      final ConnectionDetails cd = new ConnectionDetails();
      assertEquals(DriverType.getDefault(), cd.getType());
      assertEquals("sa", cd.getUser());
   }

   @Test
   public final void testFromConfig()
   {
      final ConnectionDetails cd = new ConnectionDetails();
      final SimpleConfig cfg = new SimpleConfig();

      assertFalse(cd.fromConfig(cfg));
      assertEquals(DriverType.getDefault(), cd.getType());
      assertEquals("sa", cd.getUser());
   }

   @Test
   public final void testToFromConfig()
   {
      final SimpleConfig cfg = new SimpleConfig();
      assertEquals("", cfg.getStringValue(ConnectionDetails.DRIVER_TYPE_CONFIG_KEY));

      final ConnectionDetails cd = new ConnectionDetails();
      cd.toConfig(cfg);

      assertEquals(DriverType.getDefault().toString(), cfg.getStringValue(ConnectionDetails.DRIVER_TYPE_CONFIG_KEY));
      assertEquals(cd.getUser(), cfg.getStringValue(cd.getType().getConfigPrefix() + ".user"));

      final ConnectionDetails cd2 = new ConnectionDetails();
      assertTrue(cd2.fromConfig(cfg));

      assertEquals(cd.getType(), cd2.getType());
      assertEquals(cd.getDbName(), cd2.getDbName());
      assertEquals(cd.getHost(), cd2.getHost());
      assertEquals(cd.getUser(), cd2.getUser());
      assertEquals(cd.getPassword(), cd2.getPassword());
   }

   @Test
   public final void testGetSetType()
   {
      final ConnectionDetails cd = new ConnectionDetails();

      cd.setType(DriverType.HSQL_MEM);
      assertEquals(DriverType.HSQL_MEM, cd.getType());

      cd.setType(DriverType.NONE);
      assertEquals(DriverType.NONE, cd.getType());
   }

   @Test
   public final void testGetSetDbName()
   {
      final ConnectionDetails cd = new ConnectionDetails();
      assertNotNull(cd.getDbName());

      cd.setDbName("test");
      assertEquals("test", cd.getDbName());

      cd.setDbName("");
      assertEquals("", cd.getDbName());

      cd.setDbName(null);
      assertEquals("", cd.getDbName());
   }

   @Test
   public final void testGetSetHost()
   {
      final ConnectionDetails cd = new ConnectionDetails();
      assertNotNull(cd.getHost());

      cd.setHost("test");
      assertEquals("test", cd.getHost());

      cd.setHost("");
      assertEquals("", cd.getHost());

      cd.setHost(null);
      assertEquals("", cd.getHost());
   }

   @Test
   public final void testGetSetUser()
   {
      final ConnectionDetails cd = new ConnectionDetails();
      assertNotNull(cd.getUser());

      cd.setUser("test");
      assertEquals("test", cd.getUser());

      cd.setUser("");
      assertEquals("", cd.getUser());

      cd.setUser(null);
      assertEquals("", cd.getUser());
   }

   @Test
   public final void testGetSetPassword()
   {
      final ConnectionDetails cd = new ConnectionDetails();
      assertNotNull(cd.getPassword());

      cd.setPassword("test");
      assertEquals("test", cd.getPassword());

      cd.setPassword("");
      assertEquals("", cd.getPassword());

      cd.setPassword(null);
      assertEquals("", cd.getPassword());
   }

   @Test
   public final void testGetConnectURL()
   {
      final ConnectionDetails cd = new ConnectionDetails(DriverType.HSQL_MEM);

      cd.setDbName("testdb");
      assertEquals("jdbc:hsqldb:mem:testdb;create=true;shutdown=true", cd.getConnectURL());

      cd.setHost("testhost");
      assertEquals("jdbc:hsqldb:mem:testhost/testdb;create=true;shutdown=true", cd.getConnectURL());
   }
}
