package org.freebus.fts.test_utils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.freebus.fts.common.Environment;
import org.freebus.fts.persistence.db.ConnectionDetails;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for persistence test cases that require an entity manager.
 */
public abstract class PersistenceTestCase
{
   static
   {
      Environment.init();
   }

   /**
    * The name of the persistence unit, as given in the constructor.
    */
   static protected String persistenceUnitName;

   /**
    * The connection details for the test connection.
    */
   static protected ConnectionDetails conDetails;

   /**
    * Create a persistence test case.
    *
    * @param persistenceUnitName - the name of the persistence unit to use.
    */
   public PersistenceTestCase(final String persistenceUnitName)
   {
      PersistenceTestCase.persistenceUnitName = persistenceUnitName;
      conDetails = new ConnectionDetails(DriverType.H2_MEM, persistenceUnitName);
   }

   /**
    * Start a transaction and prepare for rollback only.
    */
   @Before
   public final void setUpPersistenceTestCase()
   {
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
      DatabaseResources.getEntityManager().getTransaction().rollback();
      DatabaseResources.close();
   }
}
