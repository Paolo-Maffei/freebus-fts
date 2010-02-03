package org.freebus.fts.persistence;

import java.io.File;

import org.apache.log4j.BasicConfigurator;

/**
 * System specific settings.
 */
public final class Environment
{
   static private final String osname, tempDir, homeDir;
   static private String appDir = null;
   static private String appName = "fts";

   static
   {
      // Configure Log4J
      BasicConfigurator.configure();

      osname = System.getProperty("os.name", "").toLowerCase();
      if (osname.startsWith("windows"))
      {
         tempDir = "c:/windows/temp";
         homeDir = System.getenv("USERPROFILE");
      }
      else if (osname.startsWith("linux"))
      {
         tempDir = "/tmp";
         homeDir = System.getenv("HOME");
      }
      else
      {
         tempDir = ".";
         homeDir = System.getenv("HOME");

         System.err.println("The system \"" + osname + "\" is not fully supported.\nPlease contact the developers.");
      }
   }

   /**
    * @return the name of the operating system.
    */
   public static String getOS()
   {
      return osname;
   }

   /**
    * @return the temporary directory (/tmp for Unix, c:\temp for Windows).
    */
   public static String getTempDir()
   {
      return tempDir;
   }

   /**
    * @return the user's home directory.
    */
   public static String getHomeDir()
   {
      return homeDir;
   }

   /**
    * Returns the application's user-data directory. The name of the directory depends
    * on the platform and uses the application name (see {@link setAppName}).
    * 
    * @return the directory for application specific user data.
    */
   public static String getAppDir()
   {
      if (appDir == null)
      {
         if (osname.startsWith("linux"))
         {
            appDir = homeDir + "/." + appName;
         }
         else
         {
            appDir = homeDir + '/' + appName;
         }

         final File appDirFile = new File(appDir);
         if (!appDirFile.isDirectory())
         {
            if (!appDirFile.mkdir())
               throw new RuntimeException("Cannot create application directory: " + appDir);
         }
      }
      return appDir;
   }

   /**
    * Set the name of the application. Default: "fts"
    */
   public static void setAppName(String appName)
   {
      appName.replace('\\', '-');
      appName.replace('/',  '-');
      appName.replace(':',  '-');
      appName.replace(' ',  '_');

      Environment.appName = appName;
      appDir = null;
   }

   /**
    * @return the name of the application.
    */
   public static String getAppName()
   {
      return appName;
   }
}
