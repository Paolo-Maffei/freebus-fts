package org.freebus.fts.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The class represents a database.
 * 
 * It holds a default database connection and adds some other features.
 */
public class Database
{
   private static String driverName = "org.hsqldb.jdbcDriver";
   private static String protocol = "jdbc:hsqldb:file:";
   private static Database instance = null;
   private Connection defaultConnection = null;
   private final String name;

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

      try
      {
         Class.forName(driverName);
      }
      catch (Exception e)
      {
         throw new Exception("Cannot load database driver " + driverName, e);
      }

      if (create)
      {
         File dbDir = new File(name).getParentFile();
         if (!dbDir.exists()) dbDir.mkdir();
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
      final String url = protocol + name;

      final Properties props = new Properties();
      props.put("hsqldb.shutdown", "true");
      props.put("hsqldb.default_table_type", "cached");
      
      final Connection con = DriverManager.getConnection(url, props);
      if (con == null) throw new SQLException("Cannot connect to database " + url);

      return con;
   }

   /**
    * Set the default database driver.
    */
   public static void setDefaultDriver(String driverName)
   {
      Database.driverName = driverName;
   }
}
