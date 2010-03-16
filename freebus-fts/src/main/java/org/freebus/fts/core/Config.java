package org.freebus.fts.core;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.freebus.fts.common.Environment;
import org.freebus.fts.common.SimpleConfig;
import org.freebus.fts.dialogs.Dialogs;

/**
 * Configuration settings. The configuration is a global singleton object that
 * is automatically loaded on demand.
 */
public final class Config extends SimpleConfig
{
   private final String configFileName;

   /**
    * @return The global configuration object.
    */
   static public Config getInstance()
   {
      return (Config) SimpleConfig.getInstance();
   }

   /**
    * Create a configuration object. Automatically loads the application's
    * default configuration.
    *
    * @see {@link #getConfig} - To access the global configuration object.
    */
   public Config()
   {
      configFileName = Environment.getAppDir() + "/config.ini";
      load();
   }

   /**
    * Set the name of the Swing look-and-feel. This is a convenient method which
    * does only set the configuration option.
    */
   public void setLookAndFeelName(String value)
   {
      put("lookAndFeel", value);
   }

   /**
    * @return The name of the Swing look-and-feel.
    */
   public String getLookAndFeelName()
   {
      return getStringValue("lookAndFeel");
   }

   /**
    * Set the main-window size.
    */
   public void setMainWindowSize(Dimension size)
   {
      put("mainWindowSize", size.width + "x" + size.height);
   }

   /**
    * @return The main-window size, or null if the size was not saved.
    */
   public Dimension getMainWindowSize()
   {
      final String value = getStringValue("mainWindowSize");
      if (value.isEmpty())
         return null;

      final int idx = value.indexOf('x');
      if (idx < 0)
         return null;

      final int width = Integer.parseInt(value.substring(0, idx));
      final int height = Integer.parseInt(value.substring(idx + 1));
      return new Dimension(width, height);
   }

   /**
    * Load the configuration object from the default application configuration
    * file. Automatically called by the constructor. No error occurs if the
    * configuration file does not exist (but the configuration object is empty
    * afterwards).
    *
    * Opens an error dialog if an error occurs.
    */
   public void load()
   {
      try
      {
         try
         {
            load(configFileName);
         }
         catch (FileNotFoundException e)
         {
            init();
         }
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.formatMessage("Config.ErrLoad", new Object[] { configFileName }));
      }
   }

   /**
    * Save the configuration object to the default application configuration
    * file.
    *
    * Opens an error dialog if an error occurs.
    */
   public void save()
   {
      try
      {
         super.save(configFileName);
      }
      catch (IOException e)
      {
         Dialogs.showExceptionDialog(e, I18n.formatMessage("Config.ErrSave", new Object[] { configFileName }));
         e.printStackTrace();
      }
   }

   /**
    * Initialize the configuration object with default values.
    * Called when no config file exists.
    */
   @Override
   public void init()
   {
      final String osname = Environment.getOS();
      String commPort, lookAndFeel;

      if (osname.startsWith("windows"))
      {
         commPort = "COM1";
         lookAndFeel = "";
      }
      else
      {
         commPort = "/dev/ttyS0";
         lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
      }

      put("commPort", commPort);
      setLookAndFeelName(lookAndFeel);
      put("knxConnectionType", "NONE");
   }
}
