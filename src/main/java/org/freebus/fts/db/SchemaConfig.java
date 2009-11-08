package org.freebus.fts.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class that manages the database schema. This class creates the database
 * tables, and migrates the database tables from one FTS database version
 * to the next.
 */
public final class SchemaConfig
{
   // Version of this software release. Do only change if the database
   // tables change, and program migration methods to come from one
   // version to the next!
   private final int version = 1;

   // Version of the installed database.
   private int installedVersion = -1;

   // The database connection.
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
         final int err = e.getErrorCode();
         if (err != -5501 && err != 30000) // Table or view does not exist (HSQL and Derby)
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
    * Update the database to the latest version, if required.
    * 
    * @throws SQLException
    */
   public void update() throws SQLException
   {
      if (installedVersion < 0) getInstalledVersion();
      if (installedVersion == version) return;

      if (installedVersion == 0)
      {
         log("Creating database " + Database.getDefault().getName());
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
         if (tableType.equals("PUBLIC")) stmt.addBatch("drop table " + tableName);
      }

      stmt.executeBatch();
      con.commit();

      installedVersion = -1;
   }

   /**
    * Output a log message
    */
   protected void log(String message)
   {
      System.out.println("Database upgrade: " + message);
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

      stmt.addBatch("create table manufacturer(manufacturer_id int not null, name varchar(50) not null)");
      stmt.addBatch("create unique index manufacturer_ix0 on manufacturer(manufacturer_id)");

      stmt.addBatch("create table functional_entity(functional_entity_id int not null, manufacturer_id int not null, fun_functional_entity_id int, functional_entity_name varchar(50) not null, functional_entity_description varchar(80))");
      stmt.addBatch("create unique index functional_entity_ix0 on functional_entity(functional_entity_id)");
      stmt.addBatch("create index functional_entity_ix1 on functional_entity(manufacturer_id)");

      stmt.addBatch("create table catalog_entry(catalog_entry_id int not null, product_id int not null, manufacturer_id int not null, entry_name varchar(50), entry_colour varchar(20), entry_width_in_modules double, entry_width_in_millimeters double, din_flag boolean, registration_ts datetime(0))");
      stmt.addBatch("create unique index catalog_entry_ix0 on catalog_entry(catalog_entry_id)");
//      stmt.addBatch("create index catalog_entry_ix1 on catalog_entry(product_id)");
//      stmt.addBatch("create index catalog_entry_ix2 on catalog_entry(manufacturer_id)");

      stmt.addBatch("create table virtual_device(virtual_device_id int not null, catalog_entry_id int not null, program_id int not null, virtual_device_name varchar(50) not null, virtual_device_description varchar(80) not null, functional_entity_id int not null, product_type_id int not null, medium_types varchar(255))");
      stmt.addBatch("create unique index virtual_device_ix0 on virtual_device(virtual_device_id)");
      stmt.addBatch("create index virtual_device_ix1 on virtual_device(catalog_entry_id)");
      stmt.addBatch("create index virtual_device_ix2 on virtual_device(functional_entity_id)");

      stmt.executeBatch();
      con.commit();

      installedVersion = version;
   }
}
