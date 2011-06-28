package org.freebus.fts.persistence.db;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import liquibase.Liquibase;

import org.freebus.fts.common.log.Logging;
import org.freebus.fts.persistence.test_entities.SampleFunctionalEntity;
import org.freebus.fts.test_utils.OldPersistenceTestCase;
import org.junit.Test;

public class TestDatabaseResources extends OldPersistenceTestCase
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
      final ConnectionDetails cd = new ConnectionDetails(DriverType.HSQL_MEM, "test");
      EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("test-entities", cd);
      assertNotNull(emf);

      EntityManager em = emf.createEntityManager();
      assertNotNull(em);
   }

   @Test
   public void testCreateConnection() throws SQLException, ClassNotFoundException
   {
      final Connection con = DatabaseResources.createConnection(new ConnectionDetails(DriverType.HSQL_MEM, "testCreateConnection"));
      con.close();
   }

   /**
    * Let Liquibase work on an empty database scheme that does not exist.
    */
   @Test
   public void testCreateMigrator() throws Exception
   {
      final Connection con = DatabaseResources.createConnection(new ConnectionDetails(DriverType.HSQL_MEM, "migrator-test", "", "sa", ""));
      Liquibase liq = DatabaseResources.createMigrator("db-changes/TestDatabaseResources-1.xml", con);
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
      final Connection con = DatabaseResources.createConnection(new ConnectionDetails(DriverType.HSQL_MEM, "migrator-test-empty", "", "sa", ""));
      Liquibase liq = DatabaseResources.createMigrator("db-changes/TestDatabaseResources-1.xml", con);
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
      final ConnectionDetails cd = new ConnectionDetails(DriverType.HSQL_MEM, dbName);

      EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("test-entities", cd);
      assertNotNull(emf);

      EntityManager em = emf.createEntityManager();
      em.persist(new SampleFunctionalEntity());
      em.close();

      final Connection con = DatabaseResources.createConnection(cd);
      Liquibase liq = DatabaseResources.createMigrator("db-changes/TestDatabaseResources-1.xml", con);
      assertNotNull(liq);

      liq.validate();
      liq.dropAll();
      liq.update(null);
   }
}
