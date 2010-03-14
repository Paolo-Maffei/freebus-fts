package org.freebus.fts.persistence.db;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import liquibase.ClassLoaderFileOpener;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;

import org.apache.log4j.Logger;
import org.freebus.fts.common.Rot13;
import org.freebus.fts.common.SimpleConfig;

public class DatabaseResources
{
   static private EntityManagerFactory entityManagerFactory;
   static private EntityManager entityManager;

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
      if (driver == null)
         return null;

      return createEntityManagerFactory(driver);
   }

   /**
    * Create an entity manager factory, using the values from the
    * {@link SimpleConfig} configuration for database, user and password.
    *
    * This method fetches the required parameters from the global
    * {@link SimpleConfig} configuration, using
    * {@link DriverType#getConfigPrefix()} as prefix for the configuration
    * options.
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
      final String host = cfg.getStringValue(pfx + ".host");
      final String user = cfg.getStringValue(pfx + ".user");
      final String passwd = Rot13.rotate(cfg.getStringValue(pfx + ".passwd"));

      final String location = host.isEmpty() ? dbname : host + '/' + dbname;

      return createEntityManagerFactory("default", driver, location, user, passwd);
   }

   /**
    * Create an entity manager factory.
    *
    * @param persistenceUnitName - the name of the persistence unit, e.g.
    *           "default".
    * @param driver - the database driver to use.
    * @param location - the name of the database or host/database.
    * @param user - the database connect user.
    * @param password - the database connect password.
    *
    * @return The created entity-manager factory.
    *
    * @see {@link #setEntityManagerFactory}
    */
   static public EntityManagerFactory createEntityManagerFactory(final String persistenceUnitName, DriverType driver,
         String location, String user, String password)
   {
      final String url = driver.getConnectURL(location);
      Logger.getLogger(DatabaseResources.class).info("Connecting: " + url);

      final Properties props = new Properties();
      props.setProperty("javax.persistence.jdbc.driver", driver.className);
      props.setProperty("javax.persistence.jdbc.url", url);
      props.setProperty("javax.persistence.jdbc.user", user);
      props.setProperty("javax.persistence.jdbc.password", password);
      props.setProperty("eclipselink.logging.logger", "org.freebus.fts.persistence.db.CommonsLoggingSessionLog");

      props.setProperty("hsqldb.default_table_type", "cached");

      try
      {
         return Persistence.createEntityManagerFactory(persistenceUnitName, props);
      }
      catch (PersistenceException e)
      {
         Logger.getLogger(DatabaseResources.class).error(
               "Cannot create entity manager factory for persistence-unit \"" + persistenceUnitName + "\": "
                     + e.getMessage());
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
//      DatabaseResources.entityManager = null;
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

   /**
    * Create a {@link Liquibase} database migrator for migrating a database.
    *
    * @param changeLogFile - the name of the Liquibase change-log file.
    * @param driverType - the database driver to use.
    * @param location - the name of the database or host/database.
    * @param user - the database connect user.
    * @param password - the database connect password.
    *
    * @return The created {@link Liquibase} object.
    */
   public static Liquibase createMigrator(String changeLogFile, DriverType driver, String location, String user,
         String password) throws Exception
   {
      final Driver dbDriver = (Driver) ClassLoader.getSystemClassLoader().loadClass(driver.className).newInstance();

      final String url = driver.getConnectURL(location);
      Logger.getLogger(DatabaseResources.class).info("Connecting: " + url);

      final Properties props = new Properties();
      props.setProperty("javax.persistence.jdbc.url", url);
      props.setProperty("javax.persistence.jdbc.user", user);
      props.setProperty("javax.persistence.jdbc.password", password);
      props.setProperty("eclipselink.logging.logger", "org.freebus.fts.persistence.db.CommonsLoggingSessionLog");

      props.setProperty("hsqldb.default_table_type", "cached");

      final Connection con = dbDriver.connect(url, props);
      final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(con);

      final Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderFileOpener(), database);
      return liquibase;
   }
}
