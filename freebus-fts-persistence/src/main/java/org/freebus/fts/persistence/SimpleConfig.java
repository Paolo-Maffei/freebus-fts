package org.freebus.fts.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A simple configuration class.
 */
public class SimpleConfig
{
   private static SimpleConfig instance;
   private final Properties props = new Properties();

   /**
    * Returns the global configuration object. A {@link SimpleConfig} object is
    * created if no global configuration object exists.
    * 
    * @return The global configuration object instance.
    */
   public static SimpleConfig getInstance()
   {
      if (instance == null)
         return new SimpleConfig();
      return instance;
   }

   /**
    * Dispose the global configuration object.
    */
   public static void disposeInstance()
   {
      instance = null;
   }

   /**
    * Create an empty configuration object.
    * 
    * The global instance is set to the created configuration object, if the
    * global instance is null.
    * 
    * @see {@link #getInstance} - to access the global configuration object
    *      instance.
    */
   public SimpleConfig()
   {
      if (instance == null)
         instance = this;
   }

   /**
    * Test if the configuration with the given key exists.
    */
   public boolean containsKey(String key)
   {
      return props.containsKey(key);
   }

   /**
    * @return the configuration value for the given key as String. Returns an
    *         empty string if the key does not exist in the config object.
    */
   public String getStringValue(String key)
   {
      String val = props.getProperty(key);
      if (val == null)
         return "";
      return val;
   }

   /**
    * @return the configuration value for the given key as Integer. Returns zero
    *         if the key does not exist in the configuration object.
    */
   public int getIntValue(String key)
   {
      String val = props.getProperty(key);
      if (val == null)
         return 0;
      return Integer.parseInt(val);
   }

   /**
    * @return the configuration value for the given key. Returns null if the key
    *         does not exist in the configuration object.
    */
   public String get(String key)
   {
      return props.getProperty(key);
   }

   /**
    * Set the configuration value for the given key.
    */
   public void put(String key, String value)
   {
      props.setProperty(key, value);
   }

   /**
    * Clear the configuration.
    * 
    * @see {@link #init}.
    */
   public void clear()
   {
      props.clear();
   }

   /**
    * Initialize the configuration with default values. This default
    * implementation does nothing.
    */
   public void init()
   {
   }

   /**
    * Load the configuration from the file fileName. Automatically called when
    * the configuration object is created.
    * 
    * @throws IOException
    */
   public void load(String fileName) throws IOException
   {
      clear();
      init();

      InputStream inStream = null;
      try
      {
         inStream = new FileInputStream(fileName);
         props.load(inStream);
      }
      catch (FileNotFoundException e)
      {
      }
      finally
      {
         if (inStream != null)
            inStream.close();
      }
   }

   /**
    * Save the configuration.
    * 
    * @throws IOException
    */
   public void save() throws IOException
   {
      final String configFileName = Environment.getAppDir() + "/config.ini";

      FileOutputStream out = null;

      try
      {
         out = new FileOutputStream(new File(configFileName));
         props.store(out, Environment.getAppName() + " configuration");
      }
      finally
      {
         if (out != null)
            out.close();
      }
   }

}
