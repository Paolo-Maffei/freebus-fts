package org.freebus.fts.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.freebus.fts.dialogs.Dialogs;

public class SimpleConfig
{

   private final Map<String, String> values = new HashMap<String, String>();

   public SimpleConfig()
   {
      super();
   }

   /**
    * Test if the configuration with the given key exists.
    */
   public boolean containsKey(String key)
   {
      return values.containsKey(key);
   }

   /**
    * @return the configuration value for the given key as String. Returns an empty string
    *         if the key does not exist in the config object.
    */
   public String getStringValue(String key)
   {
      String val = values.get(key);
      if (val == null) return "";
      return val;
   }

   /**
    * @return the configuration value for the given key as Integer. Returns zero if the
    *         key does not exist in the config object.
    */
   public int getIntValue(String key)
   {
      String val = values.get(key);
      if (val == null) return 0;
      return Integer.parseInt(val);
   }

   /**
    * @return the configuration value for the given key. Returns null if the key does not
    *         exist in the config object.
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
    * Initialize the configuration with default values.
    * This default implementation does nothing.
    */
   public void init()
   {
   }

   /**
    * Load the configuration from the file fileName. Automatically called when the
    * configuration object is created.
    */
   public void load(String fileName)
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
            if (line.length() < 2 || line.startsWith(";") || line.startsWith("#")) continue;
            idx = line.indexOf('=');
            if (idx < 0) continue;
            key = line.substring(0, idx);
            val = line.substring(idx + 1);
   
            values.put(key, val);
         }
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.formatMessage("Config.ErrLoad", new Object[] { fileName }));
      }
      finally
      {
         if (fileReader != null) try
         {
            fileReader.close();
         }
         catch (IOException e)
         {
         }
      }
   }

   /**
    * Save the configuration.
    */
   public void save()
   {
      final String configFileName = Environment.getAppDir() + "/config.ini";
   
      try
      {
         final FileOutputStream outStream = new FileOutputStream(new File(configFileName));
         final PrintWriter out = new PrintWriter(outStream);
   
         out.println("; FTS Configuration");
   
         for (String key : values.keySet())
            out.printf("%s=%s\n", key, values.get(key));
   
         out.flush();
         outStream.close();
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.formatMessage("Config.ErrSave", new Object[] { configFileName }));
      }
   }

}
