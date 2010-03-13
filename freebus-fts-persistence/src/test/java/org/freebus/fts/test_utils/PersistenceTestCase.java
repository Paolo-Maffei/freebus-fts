package org.freebus.fts.test_utils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for persistence test cases that require an entity manager.
 */
public abstract class PersistenceTestCase
{
   /**
    * The name of the persistence unit, as given in the constructor.
    */
   protected final String persistenceUnitName;

   /**
    * The name of the HSQL database.
    */
   protected final String databaseName = getClass().getName();

   /**
    * Create a persistence test case.
    *
    * @param persistenceUnitName - the name of the persistence unit to use.
    */
   public PersistenceTestCase(final String persistenceUnitName)
   {
      this.persistenceUnitName = persistenceUnitName;

      final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(persistenceUnitName,
            DriverType.HSQL_MEM, databaseName, "sa", "");
      DatabaseResources.setEntityManagerFactory(emf);
   }

   /**
    * Start a transaction and prepare for rollback only.
    */
   @Before
   public final void setUpPersistenceTestCase()
   {
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
