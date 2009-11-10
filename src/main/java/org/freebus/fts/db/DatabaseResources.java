package org.freebus.fts.db;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.freebus.fts.Config;

public final class DatabaseResources
{
   static protected EntityManagerFactory entityManagerFactory;
   static protected EntityManager entityManager;

   /**
    * Lazily acquire the global entity manager and cache it.
    * 
    * @return the global {@link EntityManager} entity manager.
    */
   static public EntityManager getEntityManager()
   {
      if (entityManager == null)
      {
         entityManager = getEntityManagerFactory().createEntityManager();
         entityManager.setFlushMode(FlushModeType.COMMIT);
      }

      return entityManager;
   }

   /**
    * Lazily acquire the global EntityManagerFactory and cache it.
    * 
    * @return the global {@link EntityManagerFactory}.
    */
   static public EntityManagerFactory getEntityManagerFactory()
   {
      if (entityManagerFactory == null)
      {
         final Properties props = new Properties();
         final Config cfg = Config.getInstance();
         final Driver driver = cfg.getProductsDbDriver();

         String url;
         if (driver.fileBased) url = driver.getConnectURL(cfg.getAppDir() + '/' + cfg.getProductsDbLocation());
         else url = driver.getConnectURL(cfg.getProductsDbLocation());
         System.out.printf("Connecting to %s\n", url);

         props.setProperty("toplink.jdbc.driver", driver.className);
         props.setProperty("toplink.jdbc.url", url);
         props.setProperty("toplink.jdbc.user", cfg.getProductsDbUser());
         props.setProperty("toplink.jdbc.password", cfg.getProductsDbPass());

         props.setProperty("hsqldb.default_table_type", "cached");  // ... does not work with openJPA

         entityManagerFactory = Persistence.createEntityManagerFactory("default", props);
      }
      return entityManagerFactory;
   }

   /**
    * Close all database resources.
    */
   public static void close()
   {
      if (entityManager != null)
      {
         if (entityManager.getTransaction().isActive())
            entityManager.getTransaction().rollback();
         entityManager.close();
         entityManager = null;
      }
      if (entityManagerFactory != null)
      {
         entityManagerFactory.close();
         entityManagerFactory = null;
      }
   }
}
