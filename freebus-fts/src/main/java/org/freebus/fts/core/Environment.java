package org.freebus.fts.core;

import java.io.File;

/**
 * System specific settings.
 */
public final class Environment
{
  static final String osname, tempDir, homeDir, appDir;

  static
  {
    osname = System.getProperty("os.name", "").toLowerCase();
    if (osname.startsWith("windows"))
    {
      tempDir = "c:/windows/temp";
      homeDir = System.getenv("USERPROFILE");
      appDir = homeDir + "/fts";
    }
    else if (osname.startsWith("linux"))
    {
      tempDir = "/tmp";
      homeDir = System.getenv("HOME");
      appDir = homeDir + "/.fts";
    }
    else
    {
      homeDir = System.getenv("HOME");
      appDir = homeDir + "/fts";
      tempDir = ".";

      System.err.println("The system \"" + osname + "\" is not fully supported.\nPlease contact the developers.");
    }

    final File appDirFile = new File(appDir);
    if (!appDirFile.isDirectory())
    {
      if (!appDirFile.mkdir()) throw new RuntimeException("Cannot create application directory: " + appDir);
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
   * @return the directory for application specific user data.
   */
  public static String getAppDir()
  {
    return appDir;
  }  
}
