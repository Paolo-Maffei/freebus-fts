package org.freebus.fts.db;

import org.freebus.fts.utils.I18n;

/**
 * All supported JDBC connection drivers.
 */
public enum Driver
{
   /**
    * Hypersonic SQL (HSQL) file based driver.
    */
   HSQL("org.hsqldb.jdbcDriver", "jdbc:hsqldb:file:", ";create=true;shutdown=true;hsqldb.default_table_type=cached", true),

   /**
    * MYSQL driver.
    */
   MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://", null, false);

   /**
    * The class-name of the JDBC driver.
    */
   public final String className;
   
   /**
    * The prefix of the connection URL.
    */
   public final String urlPrefix;

   /**
    * Additional arguments for the connection URL. Optional - may be null.
    */
   public final String urlArgs;

   /**
    * True if the database is file-based.
    */
   public final boolean fileBased;

   /**
    * @return the default database driver.
    */
   static public Driver getDefault()
   {
      return HSQL;
   }

   /**
    * @return the translated name of the driver.
    */
   public String getLabel()
   {
      return I18n.getMessage("Driver_" + toString());
   }

   /**
    * @return an example location, translated. 
    */
   public String getExampleLocation()
   {
      return I18n.getMessage("Driver_Location_" + toString());
   }

   /**
    * @return a connect URL for the given location.
    */
   public String getConnectURL(final String location)
   {
      if (urlArgs == null) return urlPrefix + location;
      return urlPrefix + location + urlArgs;
   }

   /*
    * Internal constructor
    */
   private Driver(String className, String urlPrefix, String urlArgs, boolean fileBased)
   {
      this.className = className;
      this.urlPrefix = urlPrefix;
      this.urlArgs = urlArgs;
      this.fileBased = fileBased;
   }
}
