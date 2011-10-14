package org.freebus.fts.test_utils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.freebus.fts.common.Environment;
import org.freebus.fts.persistence.db.ConnectionDetails;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for persistence test cases that require an entity manager.
 */
public abstract class PersistenceTestCase
{
   private final static Logger LOGGER = LoggerFactory.getLogger(PersistenceTestCase.class);

   static
   {
      Environment.init();
   }

   /**
    * The name of the persistence unit, as given in the constructor.
    */
   protected final String persistenceUnitName;

   /**
    * The connection details for the test connection.
    */
   protected final ConnectionDetails conDetails;

   /**
    * The name of the database.
    */
   protected final String databaseName = getClass().getSimpleName();

   /**
    * Create a persistence test case.
    *
    * @param persistenceUnitName - the name of the persistence unit to use.
    */
   public PersistenceTestCase(final String persistenceUnitName)
   {
      this.persistenceUnitName = persistenceUnitName;
      conDetails = new ConnectionDetails(DriverType.H2_MEM, databaseName);
   }

   /**
    * Start a transaction and prepare for rollback only.
    */
   @Before
   public final void setUpPersistenceTestCase()
   {
      LOGGER.debug("starting database");
      final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(persistenceUnitName, conDetails);
      DatabaseResources.setEntityManagerFactory(emf);

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
      LOGGER.debug("shutting down database");
      DatabaseResources.getEntityManager().getTransaction().rollback();
      DatabaseResources.close();
   }
}
