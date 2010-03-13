package org.freebus.fts.persistence.db;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import liquibase.Liquibase;

import org.freebus.fts.common.log.Logging;
import org.freebus.fts.persistence.test_entities.SampleFunctionalEntity;
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
   public void testCreateEntityManagerFactory()
   {
      EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("test-entities", DriverType.HSQL_MEM, "test", "sa", "");
      assertNotNull(emf);

      EntityManager em = emf.createEntityManager();
      assertNotNull(em);
   }

   /**
    * Let Liquibase work on an empty database scheme that does not exist.
    */
   @Test
   public void testCreateMigrator() throws Exception
   {
      Liquibase liq = DatabaseResources.createMigrator("db-changes/TestDatabaseResources-1.xml",
            DriverType.HSQL_MEM, "migrator-test", "sa", "");
      assertNotNull(liq);

      liq.dropAll();
      liq.update(null);
   }

   /**
    * Let Liquibase work on an empty database scheme that does not exist.
    */
   @Test
   public void testCreateMigratorEmptyDatabase() throws Exception
   {
      Liquibase liq = DatabaseResources.createMigrator("db-changes/TestDatabaseResources-1.xml",
            DriverType.HSQL_MEM, "migrator-test-empty", "sa", "");
      assertNotNull(liq);
      
      liq.checkDatabaseChangeLogTable();
   }

   /**
    * Let JPA create database tables, then tell Liquibase to drop them all
    * and create it's own scheme. 
    */
   @Test
   public void testMigrateOldDatabase() throws Exception
   {
      final String dbName = "migrator-test-exists";
      EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("test-entities", DriverType.HSQL_MEM, dbName, "sa", "");
      assertNotNull(emf);

      EntityManager em = emf.createEntityManager();
      em.persist(new SampleFunctionalEntity());
      em.close();

      Liquibase liq = DatabaseResources.createMigrator("db-changes/TestDatabaseResources-1.xml",
            DriverType.HSQL_MEM, dbName, "sa", "");
      assertNotNull(liq);

      liq.validate();
      liq.dropAll();
      liq.update(null);
   }
}
