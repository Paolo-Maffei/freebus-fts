package org.freebus.fts.core;

import java.io.File;

import org.freebus.fts.dialogs.Dialogs;

/**
 * Configuration settings. The configuration is a global singleton object that is
 * automatically loaded.
 */
public final class Config extends SimpleConfig
{
   static private final Config instance = new Config();
   /**
    * @return The global configuration object.
    */
   static public Config getInstance()
   {
      return instance;
   }

   /**
    * Create a configuration object. Use {@link #getConfig} to access the configuration
    * object.
    */
   private Config()
   {
      init();

      final String configFileName = Environment.getAppDir() + "/config.ini";
      try
      {
         if ((new File(configFileName)).exists()) load(configFileName);
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.formatMessage("Config.ErrLoad", new Object[] { configFileName }));
      }
   }

   /**
    * Initialize the configuration object with default values.
    */
   public void init()
   {
      final String osname = Environment.getOS();
      String commPort, lookAndFeel;

      if (osname.startsWith("windows"))
      {
         commPort = "COM1";
         lookAndFeel = "";
      }
      else if (osname.startsWith("linux"))
      {
         commPort = "/dev/ttyS0";
         lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
      }
      else
      {
         commPort = "";
         lookAndFeel = "";
      }

      put("commPort", commPort);
      put("lookAndFeel", lookAndFeel);
      put("knxConnectionType", "NONE");
   }
}
