package org.freebus.fts.persistence.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
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
    * Create an entity manager factory for the persistence unit "default", using
    * the configured values from the {@link SimpleConfig configuration}.
    *
    * @return The created entity-manager factory.
    *
    * @see {@link #setEntityManagerFactory}
    * @see {@link ConnectionDetails#fromConfig(SimpleConfig)}
    */
   static public EntityManagerFactory createDefaultEntityManagerFactory()
   {
      final SimpleConfig cfg = SimpleConfig.getInstance();

      final ConnectionDetails conDetails = new ConnectionDetails();
      conDetails.fromConfig(cfg);

      return createEntityManagerFactory("default", conDetails);
   }

   /**
    * Create an entity manager factory.
    *
    * @param persistenceUnitName - the name of the persistence unit, e.g.
    *           "default".
    * @param conDetails - the database connection-details to use.
    *
    * @return The created entity-manager factory.
    *
    * @see {@link #setEntityManagerFactory}
    */
   static public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName,
         ConnectionDetails conDetails)
   {
      Logger.getLogger(DatabaseResources.class).info("JPA Connecting: " + conDetails.getConnectURL());

      try
      {
         final Properties props = getPropertiesFor(conDetails);
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
      // DatabaseResources.entityManager = null;
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
    * Close all database resources. Does not close the created {@link Liquibase}
    * migrators.
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
    * Create a {@link Liquibase} database migrator for migrating a database. The
    * change log files are loaded using a class-loader file-opener.
    *
    * @param changeLogFile - the name of the Liquibase change-log file.
    * @param con - the database connection that is used for migration.
    *
    * @return The created {@link Liquibase} object.
    *
    * @see {@link #createConnection(ConnectionDetails)}
    */
   public static Liquibase createMigrator(String changeLogFile, Connection con) throws Exception
   {
      final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(con);

      return new Liquibase(changeLogFile, new ClassLoaderFileOpener(), database);
   }

   /**
    * Create a {@link Connection connection} using the given connection-details.
    *
    * @param conDetails - the database connection-details to use.
    *
    * @return The created database connection.
    *
    * @throws SQLException - if there was an error connecting to the database
    * @throws ClassNotFoundException - if the database driver was not found
    */
   public static Connection createConnection(ConnectionDetails conDetails) throws SQLException, ClassNotFoundException
   {
      try
      {
         final DriverType type = conDetails.getType();
         final Driver dbDriver = (Driver) ClassLoader.getSystemClassLoader().loadClass(type.className).newInstance();

         final String url = conDetails.getConnectURL();
         Logger.getLogger(DatabaseResources.class).info("JDBC Connecting: " + url);

         final Properties props = new Properties();
         props.setProperty("user", conDetails.getUser());
         props.setProperty("password", conDetails.getPassword());
         props.setProperty("hsqldb.default_table_type", "cached");

         return dbDriver.connect(url, props);
      }
      catch (IllegalAccessException e)
      {
         throw new SQLException(e);
      }
      catch (InstantiationException e)
      {
         throw new SQLException(e);
      }
   }

   /**
    * @return a {@link Properties database properties} object filled from the
    *         given connection-details object
    */
   public static Properties getPropertiesFor(final ConnectionDetails conDetails)
   {
      final Properties props = new Properties();
      props.setProperty("javax.persistence.jdbc.driver", conDetails.getType().className);
      props.setProperty("javax.persistence.jdbc.url", conDetails.getConnectURL());
      props.setProperty("javax.persistence.jdbc.user", conDetails.getUser());
      props.setProperty("javax.persistence.jdbc.password", conDetails.getPassword());

      props.setProperty("eclipselink.logging.logger", "org.freebus.fts.persistence.db.CommonsLoggingSessionLog");

      final DriverType type = conDetails.getType();
      if (type == DriverType.HSQL || type == DriverType.HSQL_MEM)
         props.setProperty("hsqldb.default_table_type", "cached");

      return props;
   }
}
