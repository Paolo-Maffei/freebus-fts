package org.freebus.fts.core;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import org.freebus.fts.common.Environment;
import org.freebus.fts.common.SimpleConfig;
import org.freebus.fts.dialogs.Dialogs;

/**
 * Configuration settings. The configuration is a global singleton object that
 * is automatically loaded.
 */
public final class Config extends SimpleConfig
{
   /**
    * @return The global configuration object.
    */
   static public Config getInstance()
   {
      return (Config) SimpleConfig.getInstance();
   }

   /**
    * Create a configuration object. The saved configuration is automatically
    * loaded.
    * 
    * @see {@link #getConfig} - To access the global configuration object.
    */
   public Config()
   {
      init();

      final String configFileName = Environment.getAppDir() + "/config.ini";
      try
      {
         if ((new File(configFileName)).exists())
            load(configFileName);
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.formatMessage("Config.ErrLoad", new Object[] { configFileName }));
      }
   }
   
   /**
    * Set the name of the Swing look-and-feel. This is a convenient method
    * which does only set the configuration option.
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
      if (value.isEmpty()) return null;

      final int idx = value.indexOf('x');
      if (idx < 0) return null;

      final int width = Integer.parseInt(value.substring(0, idx));
      final int height = Integer.parseInt(value.substring(idx + 1));
      return new Dimension(width, height);
   }

   /**
    * Save the configuration object.
    */
   @Override
   public void save()
   {
      try
      {
         super.save();
      }
      catch (IOException e)
      {
         Dialogs.showExceptionDialog(e, I18n.getMessage("Config.ErrSave"));
         e.printStackTrace();
      }
   }

   /**
    * Initialize the configuration object with default values.
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
      setLookAndFeelName(lookAndFeel);
      put("knxConnectionType", "NONE");
   }
}
