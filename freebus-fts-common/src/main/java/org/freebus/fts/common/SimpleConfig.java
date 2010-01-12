package org.freebus.fts.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

/**
 * A simple configuration class.
 */
public class SimpleConfig
{
   private static SimpleConfig instance;
   private final Map<String, String> values = new TreeMap<String, String>();

   /**
    * Returns the global configuration object. A {@link SimpleConfig} object is
    * created if no global configuration object exists.
    * 
    * @return The global configuration object instance.
    */
   public static SimpleConfig getInstance()
   {
      if (instance == null)
         new SimpleConfig();

      return instance;
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
      return values.containsKey(key);
   }

   /**
    * @return the configuration value for the given key as String. Returns an
    *         empty string if the key does not exist in the config object.
    */
   public String getStringValue(String key)
   {
      String val = values.get(key);
      if (val == null)
         return "";
      return val;
   }

   /**
    * @return the configuration value for the given key as Integer. Returns zero
    *         if the key does not exist in the config object.
    */
   public int getIntValue(String key)
   {
      String val = values.get(key);
      if (val == null)
         return 0;
      return Integer.parseInt(val);
   }

   /**
    * @return the configuration value for the given key. Returns null if the key
    *         does not exist in the config object.
    */
   public String get(String key)
   {
      return values.get(key);
   }

   /**
    * Set the configuration value for the given key.
    */
   public void put(String key, String value)
   {
      values.put(key, value);
   }

   /**
    * Clear the configuration.
    * 
    * @see {@link #init}.
    */
   public void clear()
   {
      values.clear();
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

      FileReader fileReader = null;
      try
      {
         fileReader = new FileReader(new File(fileName));
         final BufferedReader in = new BufferedReader(fileReader);

         String key, val, line;
         int idx, lineNo = 0;

         while (in.ready())
         {
            ++lineNo;
            line = in.readLine().trim();
            if (line.length() < 2 || line.startsWith(";") || line.startsWith("#"))
               continue;
            idx = line.indexOf('=');
            if (idx < 0)
               continue;
            key = line.substring(0, idx);
            val = line.substring(idx + 1);

            values.put(key, val);
         }
      }
      catch (FileNotFoundException e)
      {
         // Not a problem
      }
      finally
      {
         if (fileReader != null)
         {
            try
            {
               fileReader.close();
            }
            catch (IOException e)
            {
            }
         }
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

      final FileOutputStream outStream = new FileOutputStream(new File(configFileName));
      final PrintWriter out = new PrintWriter(outStream);

      out.println("; " + Environment.getAppName() + " configuration");

      for (String key : values.keySet())
         out.printf("%s=%s\n", key, values.get(key));

      out.flush();
      outStream.close();
   }

}
