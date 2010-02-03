package org.freebus.fts.persistence.db;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.freebus.fts.persistence.Environment;
import org.freebus.fts.persistence.SimpleConfig;

public class DatabaseResources
{
   static protected EntityManagerFactory entityManagerFactory;
   static protected EntityManager entityManager;

   static
   {
      // Ensure that Environment is loaded, which initializes the logging
      Environment.getOS();
   }

   /**
    * Lazily acquire the global entity manager and cache it.
    * 
    * @return the global {@link EntityManager} entity manager.
    */
   static public EntityManager getEntityManager()
   {
      if (entityManager == null)
      {
         if (entityManagerFactory == null)
            throw new RuntimeException("entity manager factory is not initialized");

         entityManager = entityManagerFactory.createEntityManager();
         entityManager.setFlushMode(FlushModeType.COMMIT);
      }

      return entityManager;
   }

   /**
    * Create an entity manager factory, using the values from the
    * {@link SimpleConfig} configuration.
    * 
    * @return The created entity-manager factory.
    * 
    * @see {@link #setEntityManagerFactory}
    */
   static public EntityManagerFactory createDefaultEntityManagerFactory()
   {
      final SimpleConfig cfg = SimpleConfig.getInstance();
      final String driverStr = cfg.getStringValue("databaseDriverType");

      final DriverType driver = driverStr.isEmpty() ? DriverType.getDefault() : DriverType.valueOf(driverStr);
      if (driver == null) return null;

      return createEntityManagerFactory(driver);
   }

   /**
    * Create an entity manager factory, using the values from the
    * {@link SimpleConfig} configuration for database, user and password.
    * 
    * This method fetches the required parameters from the global
    * {@link SimpleConfig} configuration, using {@link DriverType#getConfigPrefix()}
    * as prefix for the configuration options.
    * 
    * @param driver - the database driver to use.
    * 
    * @return The created entity-manager factory.
    * 
    * @see {@link #setEntityManagerFactory}
    */
   static public EntityManagerFactory createEntityManagerFactory(DriverType driver)
   {
      final SimpleConfig cfg = SimpleConfig.getInstance();
      final String pfx = driver.getConfigPrefix();

      final String dbname = cfg.getStringValue(pfx + ".database");
      final String user = cfg.getStringValue(pfx + ".user");
      final String passwd = cfg.getStringValue(pfx + ".passwd");

      return createEntityManagerFactory(driver, dbname, user, passwd);
   }

   /**
    * Create an entity manager factory.
    * 
    * @param driver - the database driver to use.
    * @param dbname - the name of the database (or database file).
    * @param user - the database connect user.
    * @param password - the database connect password.
    * 
    * @return The created entity-manager factory.
    * 
    * @see {@link #setEntityManagerFactory}
    */
   static public EntityManagerFactory createEntityManagerFactory(DriverType driver, String dbname, String user,
         String password)
   {
      // final Config cfg = Config.getInstance();
      // final Driver driver = cfg.getProductsDbDriver();
      // String url;
      // if (driver.fileBased) url = driver.getConnectURL(cfg.getAppDir() + '/'
      // +
      // cfg.getProductsDbLocation());
      // else url = driver.getConnectURL(cfg.getProductsDbLocation());
      final String url = driver.getConnectURL(dbname);
      Logger.getLogger(DatabaseResources.class).info("Connecting: " + url);

      final Properties props = new Properties();
      props.setProperty("javax.persistence.jdbc.driver", driver.className);
      props.setProperty("javax.persistence.jdbc.url", url);
      props.setProperty("javax.persistence.jdbc.user", user);
      props.setProperty("javax.persistence.jdbc.password", password);
      // props.setProperty("eclipselink.logging.level", "FINEST");
      //props.setProperty("eclipselink.logging.logger", "org.freebus.fts.common.db.CommonsLoggingSessionLog");

      props.setProperty("hsqldb.default_table_type", "cached");

      try
      {
         return Persistence.createEntityManagerFactory("default", props);
      }
      catch (PersistenceException e)
      {
         System.err.println("Cannot create entity manager factory: " + e.getMessage());
         throw e;
      }
   }

   /**
    * Set the default entity manager factory. Must be called before
    * {@link #getEntityManager} can be used.
    * 
    * @see {@link #createEntityManagerFactory}.
    */
   public static void setEntityManagerFactory(EntityManagerFactory entityManagerFactory)
   {
      DatabaseResources.entityManagerFactory = entityManagerFactory;
   }

   /**
    * @return the default entity manager factory.
    * @see {@link #setEntityManagerFactory}.
    */
   public static EntityManagerFactory getEntityManagerFactory()
   {
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
