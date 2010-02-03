package org.freebus.fts.persistence.db;

/**
 * All supported JDBC connection drivers.
 */
public enum DriverType
{
   /**
    * No driver.
    */
   None(null, null, null, DriverClass.NONE),

   /**
    * Hypersonic SQL (HSQL) file based driver.
    */
   HSQL("org.hsqldb.jdbcDriver", "jdbc:hsqldb:file:", ";create=true;shutdown=true;hsqldb.default_table_type=cached", DriverClass.FILE_BASED),

   /**
    * Hypersonic SQL (HSQL) memory based driver. For testing only.
    */
   HSQL_MEM("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:", ";create=true;shutdown=true", DriverClass.NONE),

   /**
    * MYSQL driver.
    */
   MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://", null, DriverClass.SERVER_BASED);

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
    * The class of the driver, @see {@link DriverClass}.
    */
   public final DriverClass driverClass;

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

   /**
    * @return The prefix for configuration options.
    */
   public String getConfigPrefix()
   {
      return "database" + toString();
   }

   /*
    * Internal constructor
    */
   private DriverType(String className, String urlPrefix, String urlArgs, DriverClass driverClass)
   {
      this.className = className;
      this.urlPrefix = urlPrefix;
      this.urlArgs = urlArgs;
      this.driverClass = driverClass;
   }
}
