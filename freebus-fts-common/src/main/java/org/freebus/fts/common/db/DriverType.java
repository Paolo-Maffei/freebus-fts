package org.freebus.fts.common.db;

/**
 * All supported JDBC connection drivers.
 */
public enum DriverType
{
   /**
    * No driver.
    */
   None(null, null, null, false),

   /**
    * Hypersonic SQL (HSQL) file based driver.
    */
   HSQL("org.hsqldb.jdbcDriver", "jdbc:hsqldb:file:", ";create=true;shutdown=true;hsqldb.default_table_type=cached", true),

   /**
    * Hypersonic SQL (HSQL) memory based driver. For test cases.
    */
   HSQL_MEM("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:", ";create=true;shutdown=true", false),

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
   static public DriverType getDefault()
   {
      return HSQL;
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
   private DriverType(String className, String urlPrefix, String urlArgs, boolean fileBased)
   {
      this.className = className;
      this.urlPrefix = urlPrefix;
      this.urlArgs = urlArgs;
      this.fileBased = fileBased;
   }
}
