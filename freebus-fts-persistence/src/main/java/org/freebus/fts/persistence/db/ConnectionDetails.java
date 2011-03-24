package org.freebus.fts.persistence.db;

import org.freebus.fts.common.Environment;
import org.freebus.fts.common.Rot13;
import org.freebus.fts.common.SimpleConfig;

/**
 * Details about a database connection. Objects of this class are used to get a
 * database connection or entity manager from the {@link DatabaseResources}.
 */
public final class ConnectionDetails
{
   /**
    * The {@link SimpleConfig configuration} key where the database driver type
    * is stored by the {@link ConnectionDetails} methods.
    */
   public static final String DRIVER_TYPE_CONFIG_KEY = "databaseDriverType";

   private DriverType type;
   private String dbName = "";
   private String host = "";
   private String user = "";
   private String passwd = "";

   /**
    * Create a database connection-details object with only the driver type set.
    *
    * @param type - the type of the database driver to set
    */
   public ConnectionDetails(DriverType type)
   {
      this(type, "");
   }

   /**
    * Create a database connection-details object with a default connection.
    */
   public ConnectionDetails()
   {
      setDefaults(DriverType.getDefault());
   }

   /**
    * Create a database connection-details object for a file-based database. The
    * user is set to "sa", with no password and no host.
    *
    * @param type - the type of the database engine.
    * @param dbName - the name of the database, or the name of the file for file
    *           based databases.
    */
   public ConnectionDetails(DriverType type, String dbName)
   {
      this.type = type;
      this.dbName = dbName == null ? "" : dbName;
      this.host = "";
      this.user = "sa";
      this.passwd = "";
   }

   /**
    * Create a database connection-details object.
    *
    * @param type - the type of the database driver.
    * @param dbName - the name of the database, or the name of the file for file
    *           based databases.
    * @param host - the host name of the database server, or null for file based
    *           databases.
    * @param user - the user that is used to connect to the database.
    * @param passwd - the password that is used to connect to the database.
    */
   public ConnectionDetails(DriverType type, String dbName, String host, String user, String passwd)
   {
      this.type = type;
      this.dbName = dbName == null ? "" : dbName;
      this.host = host == null ? "" : host;
      this.user = user == null ? "" : user;
      this.passwd = passwd == null ? "" : passwd;
   }

   /**
    * Set the database connection-details object to default values. This is a
    * HSQL file-based database that is stored in the
    * {@link Environment#getAppDir() user's application directory} and is named
    * "db". This not like creating an empty connection-details object.
    *
    * @param type - the type of the database driver.
    */
   public void setDefaults(DriverType type)
   {
      this.type = type;

      if (type.driverClass == DriverClass.FILE_BASED)
      {
         dbName = Environment.getAppDir() + "/db";
         host = "";
      }
      else
      {
         dbName = Environment.getAppName();
         host = "localhost";
      }

      user = "sa";
      passwd = "";
   }

   /**
    * Initialize the object from the configuration <code>config</code>. The
    * {@link DriverType driver-type} is taken from the configuration option
    * "databaseDriverType", the other values are taken from the configuration
    * options "database<type>.<key>", e.g. "databaseHSQL.user" for the user name
    * for the HSQL file-based database.
    * <p>
    * The object is {@link #setDefaults set to default values} if no database
    * connection was yet configured.
    *
    * @param config - the configuration to get the values from
    *
    * @return true if the configuration was loaded, false if no configuration
    *         was found and the default values were applied.
    */
   public boolean fromConfig(final SimpleConfig config)
   {
      final String driverTypeStr = config.get(DRIVER_TYPE_CONFIG_KEY);
      if (driverTypeStr == null)
      {
         setDefaults(DriverType.getDefault());
         return false;
      }

      fromConfig(config, DriverType.valueOf(driverTypeStr));
      return true;
   }

   /**
    * Initialize the object from the configuration <code>config</code>. The
    * configuration values are taken from the configuration options
    * "database<type>.<key>", e.g. "databaseHSQL.user" for the user name for the
    * HSQL file-based driver type.
    *
    * @param config - the configuration to get the values from
    * @param type - the database driver type to get the values for
    */
   public void fromConfig(final SimpleConfig config, DriverType type)
   {
      final String pfx = type.getConfigPrefix();

      if (config.containsKey(pfx + ".database"))
      {
         setType(type);
         setDbName(config.getStringValue(pfx + ".database"));
         setHost(config.getStringValue(pfx + ".host"));
         setUser(config.getStringValue(pfx + ".user"));
         setPassword(Rot13.rotate(config.getStringValue(pfx + ".passwd")));
      }
      else setDefaults(type);
   }

   /**
    * Store the object's contents in the configuration <code>config</code>.
    *
    * @param config - the config to read from.
    *
    * @see #fromConfig(SimpleConfig)
    */
   public void toConfig(final SimpleConfig config)
   {
      config.put(DRIVER_TYPE_CONFIG_KEY, type.toString());

      final String pfx = getType().getConfigPrefix();
      config.put(pfx + ".database", getDbName());
      config.put(pfx + ".host", getHost());
      config.put(pfx + ".user", getUser());
      config.put(pfx + ".passwd", Rot13.rotate(getPassword()));
   }

   /**
    * @return the type of the database engine.
    */
   public DriverType getType()
   {
      return type;
   }

   /**
    * Set the type of the database driver.
    * 
    * @param type - the database driver type.
    */
   public void setType(DriverType type)
   {
      this.type = type;
   }

   /**
    * @return the name of the database, or the name of the file for file based
    *         databases.
    */
   public String getDbName()
   {
      return dbName;
   }

   /**
    * Set the name of the database, or the name of the file for file based
    * databases.
    * 
    * @param dbName - the database name.
    */
   public void setDbName(String dbName)
   {
      this.dbName = (dbName == null ? "" : dbName);
   }

   /**
    * @return the host name of the database server, or null for file based
    *         databases.
    */
   public String getHost()
   {
      return host;
   }

   /**
    * Set the host name of the database server, or null for file based
    * databases.
    * 
    * @param host - the host name.
    */
   public void setHost(String host)
   {
      this.host = (host == null ? "" : host);
   }

   /**
    * @return the user that is used to connect to the database.
    */
   public String getUser()
   {
      return user;
   }

   /**
    * Set the user that is used to connect to the database.
    * 
    * @param user - the user to set.
    */
   public void setUser(String user)
   {
      this.user = (user == null ? "" : user);
   }

   /**
    * @return the password that is used to connect to the database.
    */
   public String getPassword()
   {
      return passwd;
   }

   /**
    * Set the password that is used to connect to the database.
    * 
    * @param passwd - the password to set.
    */
   public void setPassword(String passwd)
   {
      this.passwd = (passwd == null ? "" : passwd);
   }

   /**
    * @return the connect URL for a database connection.
    *
    * @see DriverType#getConnectURL(String)
    */
   public String getConnectURL()
   {
      return type.getConnectURL(host.isEmpty() ? dbName : host + '/' + dbName);
   }
}
