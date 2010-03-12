package org.freebus.fts.persistence.db;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import liquibase.Liquibase;

import org.freebus.fts.common.log.Logging;
import org.freebus.fts.test_utils.PersistenceTestCase;
import org.junit.Test;

public class TestDatabaseResources extends PersistenceTestCase
{
   static
   {
      Logging.setup();
   }

   public TestDatabaseResources()
   {
      super("test-empty");
   }

   @Test
   public void testCreateMigrator() throws Exception
   {
      Liquibase liq = DatabaseResources.createMigrator("db-changes/TestDatabaseResources-1.xml",
            DriverType.HSQL_MEM, "migrator-test", "sa", "");
      assertNotNull(liq);

      liq.dropAll();
      liq.update(null);
   }

   @Test
   public void testCreateEntityManagerFactory()
   {
      EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("test-entities", DriverType.HSQL_MEM, "test", "sa", "");
      assertNotNull(emf);

      EntityManager em = emf.createEntityManager();
      assertNotNull(em);
   }
}
