package org.freebus.fts.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class that manages the database schema.
 */
public final class SchemaConfig
{
   // Version of this software release. Do only change if the database
   // tables change, and program migration methods to come from one
   // version to the next!
   private final int version = 1;

   // Installed version.
   private int installedVersion = -1;

   // The database connection
   private final Connection con;

   /**
    * Create a schema-configurator object.
    * 
    * @throws SQLException
    */
   public SchemaConfig() throws SQLException
   {
      con = Database.getDefault().getDefaultConnection();
   }

   /**
    * Get the schema version of the installed database.
    */
   public int getInstalledVersion()
   {
      installedVersion = 0;

      try
      {
         final Statement stmt = con.createStatement();

         final ResultSet result = stmt.executeQuery("select version from version");
         if (result.next()) installedVersion = result.getInt("version");
         stmt.close();
      }
      catch (SQLException e)
      {
         if (e.getErrorCode() != 30000) // Table or view does not exist
            e.printStackTrace();
      }
      return installedVersion;
   }

   /**
    * @return the schema version that this version of FTS requires.
    */
   public int getVersion()
   {
      return version;
   }

   /**
    * Update the database to the latest version
    * 
    * @throws SQLException
    */
   public void update() throws SQLException
   {
      if (installedVersion < 0) getInstalledVersion();

      if (installedVersion == 0)
      {
         log("Initial database installation: " + Database.getDefault().getName());
         installTables();
      }
      else
      {
         log("Upgrading database from version " + Integer.toString(installedVersion) + " to version "
               + Integer.toString(version));
      }

      if (installedVersion != version) throw new RuntimeException("Internal error: Database upgrade failed");

      final Statement stmt = con.createStatement();
      stmt.executeUpdate("update version set version=" + Integer.toString(version));

      con.commit();
   }

   /**
    * Delete all tables.
    * 
    * @throws SQLException
    */
   public void dropAllTables() throws SQLException
   {
      log("Deleting all database tables");

      final DatabaseMetaData metaData = con.getMetaData();
      final Statement stmt = con.createStatement();
      final ResultSet resultSet = metaData.getTables(null, null, "%", null);
      String tableName, tableType;

      while (resultSet.next())
      {
         tableType = resultSet.getString(2);
         tableName = resultSet.getString(3);
         if (tableType.equals("APP")) stmt.addBatch("drop table " + tableName);
      }

      stmt.executeBatch();
      con.commit();
   }

   /**
    * Output a log message
    */
   protected void log(String message)
   {
      System.out.println("Database update: " + message);
   }

   /**
    * Install the tables
    * 
    * @throws SQLException
    */
   protected void installTables() throws SQLException
   {
      final Statement stmt = con.createStatement();

      stmt.addBatch("create table version(version int not null)");
      stmt.addBatch("insert into version(version) values(0)");

      stmt.addBatch("create table manufacturer(manufacturer_id int not null, name varchar(20) not null)");
      stmt.addBatch("create unique index manufacturer_ix0 on manufacturer(manufacturer_id)");

      stmt.addBatch("create table catalog_group(manufacturer_id int not null, group_id int not null, name varchar(40) not null, description varchar(255))");
      stmt.addBatch("create unique index catalog_group_ix0 on catalog_group(manufacturer_id, group_id)");

      stmt.addBatch("create table catalog_entry(manufacturer_id int not null, group_id int not null, name varchar(40) not null, description varchar(255))");
      stmt.addBatch("create unique index catalog_entry_ix0 on catalog_entry(manufacturer_id, group_id)");

      stmt.executeBatch();
      installedVersion = version;
   }
}
