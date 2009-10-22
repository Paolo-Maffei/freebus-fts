package org.freebus.fts.db;

import java.sql.*;

/**
 * The class represents a database.
 * 
 * It holds a default database connection and adds some other features.
 */
public class Database
{
   private static String driverName = "org.apache.derby.jdbc.EmbeddedDriver";
   private static String protocol = "jdbc:derby:";
   private static Database instance = null;
   private Connection defaultConnection = null;
   private final String name;
   private final boolean create;
   private final Driver driver;

   /**
    * @return the default database object.
    */
   public static Database getDefault()
   {
      return instance;
   }

   /**
    * Set the default database object.
    */
   public static void setDefault(Database database)
   {
      instance = database;
   }

   /**
    * @return the name of the database.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Create a new database object.
    * 
    * @param dbName is the name of the database to which we will connect.
    * @param create if set true, the database is created if it does not exist.
    * @throws Exception
    */
   public Database(String dbName, boolean create) throws Exception
   {
      this.name = dbName;
      this.create = create;

      try
      {
         driver = (Driver) Class.forName(driverName).newInstance();
      }
      catch (Exception e)
      {
         throw new Exception("Cannot initialise database " + dbName + "\n\n" + e.getMessage(), e);
      }
   }

   /**
    * Returns the default connection to the database. If no default
    * connection exists, or if it is closed, a new default connection
    * is established.
    *
    * @return the default connection to the database.
    * @throws SQLException
    */
   public Connection getDefaultConnection() throws SQLException
   {
      if (defaultConnection == null || defaultConnection.isClosed())
         defaultConnection = connect();

      return defaultConnection;
   }

   /**
    * Create a private connection to the database.
    * 
    * @return the connection.
    * @throws SQLException
    */
   public Connection connect() throws SQLException
   {
      String url = protocol + name;
      if (create) url += ";create=true";

      return driver.connect(url, null);
   }

   /**
    * Set the default database driver.
    */
   public static void setDefaultDriver(String driverName)
   {
      Database.driverName = driverName;
   }
}
