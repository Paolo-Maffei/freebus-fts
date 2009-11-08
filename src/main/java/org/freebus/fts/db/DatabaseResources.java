package org.freebus.fts.db;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
      if (entityManager == null) entityManager = getEntityManagerFactory().createEntityManager();
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

         final String productsDbFile = cfg.getAppDir() + '/' + cfg.getProductsDb();
         props.setProperty("openjpa.ConnectionURL", "jdbc:hsqldb:file:" + productsDbFile + ";shutdown=true;hsqldb.default_table_type=cached");
         props.setProperty("toplink.jdbc.url", "jdbc:hsqldb:file:" + productsDbFile + ";shutdown=true;hsqldb.default_table_type=cached");
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
