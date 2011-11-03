package org.freebus.fts.test_utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;

import org.freebus.fts.common.Environment;
import org.freebus.fts.persistence.db.ConnectionDetails;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for persistence test cases that require an entity manager.
 */
public abstract class PersistenceTestCase
{
   private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceTestCase.class);

   private static final String DATABASE_NAME = "Persistence-Test-Case";
   private static final DriverType DRIVER_TYPE = DriverType.H2_MEM;

   static
   {
      Environment.init();
   }
   
//   private static final String DATABASE_NAME = "/tmp/PersistenceTestCase.db";
//   private static final DriverType DRIVER_TYPE = DriverType.H2;

   @BeforeClass
   public static void beforeClass() throws SQLException, ClassNotFoundException, LiquibaseException
   {
      LOGGER.debug("Creating database");
      final ConnectionDetails conDetails = new ConnectionDetails(DRIVER_TYPE, DATABASE_NAME);
      final Connection con = DatabaseResources.createConnection(conDetails);
      final Liquibase liq = DatabaseResources.createMigrator("db-changes/changelog-0.1.xml", con);
      liq.update("");
      con.commit();

      final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("default", conDetails);
      DatabaseResources.setEntityManagerFactory(emf);
   }

   @AfterClass
   public static void afterClass()
   {
      LOGGER.debug("Closing database");
      DatabaseResources.close();
   }

   /**
    * Create a persistence test case.
    *
    * @param persistenceUnitName - the name of the persistence unit to use.
    */
   public PersistenceTestCase()
   {
   }

   /**
    * Start a transaction and prepare for rollback only.
    */
   @Before
   public final void setUpPersistenceTestCase()
   {
      LOGGER.debug("starting database");

      final EntityTransaction trans = DatabaseResources.getEntityManager().getTransaction();
      trans.begin();
      trans.setRollbackOnly();
   }

   /**
    * Rollback the database transaction.
    */
   @After
   public final void tearDownPersistenceTestCase()
   {
      DatabaseResources.getEntityManager().getTransaction().rollback();
   }
}
