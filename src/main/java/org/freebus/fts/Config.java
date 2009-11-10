package org.freebus.fts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.freebus.fts.db.Driver;
import org.freebus.fts.utils.I18n;

/**
 * Configuration settings. The configuration is a global singleton object that
 * is automatically loaded.
 */
public final class Config
{
   static private Config instance;
   private String commPort;
   private String tempDir;
   private String vdxDir;
   private String language = "German";

   private String productsDbLocation = "products/db";
   private Driver productsDbDriver = Driver.getDefault();
   private String productsDbUser = "sa";
   private String productsDbPass = "";

   private final String configFileName;
   private final String homeDir, appDir;

   /**
    * @return the directory where FTS can store files
    */
   public String getAppDir()
   {
      return appDir;
   }

   /**
    * @return The global configuration object.
    */
   static public Config getInstance()
   {
      if (instance == null) instance = new Config();
      return instance;
   }

   /**
    * @return the communication port.
    */
   public String getCommPort()
   {
      return commPort;
   }

   /**
    * Set the communication port.
    */
   public void setCommPort(String commPort)
   {
      this.commPort = commPort;
   }

   /**
    * Set the preferred language.
    */
   public void setLanguage(String language)
   {
      this.language = language;
   }

   /**
    * @return the preferred language.
    */
   public String getLanguage()
   {
      return language;
   }

   /**
    * Set the location of the products database.
    * E.g. "localhost" or "products/db".
    */
   public void setProductsDbLocation(String productsDbLocation)
   {
      this.productsDbLocation = productsDbLocation;
   }

   /**
    * @return the location of the products database.
    * E.g. "localhost" or "products/db".
    */
   public String getProductsDbLocation()
   {
      return productsDbLocation;
   }

   /**
    * @return the productsDbDriver
    */
   public Driver getProductsDbDriver()
   {
      return productsDbDriver;
   }

   /**
    * @param productsDbDriver the productsDbDriver to set
    */
   public void setProductsDbDriver(Driver productsDbDriver)
   {
      this.productsDbDriver = productsDbDriver;
   }

   /**
    * @return the productsDbUser
    */
   public String getProductsDbUser()
   {
      return productsDbUser;
   }

   /**
    * @param productsDbUser the productsDbUser to set
    */
   public void setProductsDbUser(String productsDbUser)
   {
      this.productsDbUser = productsDbUser;
   }

   /**
    * @return the productsDbPass
    */
   public String getProductsDbPass()
   {
      return rot13(productsDbPass);
   }
   
   /**
    * @param productsDbPass the productsDbPass to set
    */
   public void setProductsDbPass(String productsDbPass)
   {
      this.productsDbPass = rot13(productsDbPass);
   }

   /**
    * A variation of the ROT-13 algorithm for string obfuscation.
    * This is no password security, of course.
    */
   private String rot13(String str)
   {
      final int len = str.length();
      StringBuilder result = new StringBuilder(len);

      for (int i = 0; i < len; ++i)
      {
         char ch = str.charAt(i);
         if ((ch >= 'a' && ch <= 'm') || (ch >= 'A' && ch <= 'M')) ch += 13;
         else if ((ch >= 'n' && ch <= 'z') || (ch >= 'N' && ch <= 'Z')) ch -= 13;
         else if (ch >= '0' && ch <= '4') ch += 5;
         else if (ch >= '5' && ch <= '9') ch -= 5;
         result.append(ch);
      }

      return result.toString();
   }

   /**
    * Set the directory for temporary files.
    */
   public void setTempDir(String tempDir)
   {
      this.tempDir = tempDir;
   }

   /**
    * @return the directory for temporary files.
    */
   public String getTempDir()
   {
      return tempDir;
   }
   /**
    * @return the directory from where the latest VDX file was loaded.
    */
   public String getVdxDir()
   {
      return vdxDir;
   }

   /**
    * Set the directory from where the latest VDX file was loaded.
    */
   public void setVdxDir(String vdxDir)
   {
      this.vdxDir = vdxDir;
   }

   /**
    * Create a configuration object. Use {@link #getConfig} to access the
    * configuration object.
    */
   private Config()
   {
      // determine the name of the serial port on several operating systems
      final String osname = System.getProperty("os.name", "").toLowerCase();
      if (osname.startsWith("windows"))
      {
         tempDir = "c:/windows/temp";
         homeDir = System.getenv("USERPROFILE");
         appDir = homeDir + "/fts";

         commPort = "COM1";
      }
      else if (osname.startsWith("linux"))
      {
         tempDir = "/tmp";
         homeDir = System.getenv("HOME");
         appDir = homeDir + "/.fts";

         commPort = "/dev/ttyS0";
      }
      else
      {
         homeDir = System.getenv("HOME");
         appDir = homeDir + "/fts";
         
         final Shell shell = new Shell(Display.getDefault(), SWT.TITLE | SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL);
         MessageBox mbox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
         mbox.setText(I18n.getMessage("Unknown System"));
         mbox.setMessage("The system \""+osname+"\" is not fully supported.\nPlease contact the developers.");
         mbox.open();
      }

      configFileName = appDir + "/config.ini";
      vdxDir = homeDir;

      final File appDirFile = new File(appDir);
      if (!appDirFile.isDirectory())
      {
         if (!appDirFile.mkdir())
            throw new RuntimeException("Cannot create application directory: "+appDir);
      }

      try
      {
         if ((new File(configFileName)).exists()) load(configFileName);
      }
      catch (Exception e)
      {
         MessageBox mbox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
         mbox.setMessage(e.getLocalizedMessage());
         mbox.open();
      }
   }

   /**
    * Load the configuration from the file fileName. Automatically called when
    * the configuration object is created.
    */
   public void load(String fileName)
   {
      FileReader fileReader = null;
      try
      {
         fileReader = new FileReader(new File(configFileName));
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

            try
            {
               if (key.equals("comm_port")) setCommPort(val);
               else if (key.equals("vdx_dir")) vdxDir = val;
               else if (key.equals("products_db_driver")) productsDbDriver = Driver.valueOf(val);
               else if (key.equals("products_db_location")) productsDbLocation = val;
               else if (key.equals("products_db_user")) productsDbUser = val;
               else if (key.equals("products_db_pass")) productsDbPass = val;
            }
            catch (Exception e)
            {
               MessageBox mbox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
               mbox.setMessage(I18n.getMessage("Error_in_config_file_%1_line_%2")
                                 .replace("%2", Integer.toString(lineNo))
                                 .replace("%1", configFileName) + ":\n"
                               + e.getLocalizedMessage());
               mbox.open();
            }
         }
      }
      catch (Exception e1)
      {
         e1.printStackTrace();
         MessageBox mbox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
         mbox.setMessage(I18n.getMessage("Failed_to_read_%1").replace("%1", configFileName)
                         + ":\n" + e1.getLocalizedMessage());
         mbox.open();
      }
      finally
      {
         if (fileReader!=null) try
         {
            fileReader.close();
         }
         catch (IOException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

   /**
    * Save the configuration.
    */
   public void save()
   {
      try
      {
         final FileOutputStream outStream = new FileOutputStream(new File(configFileName));
         final PrintWriter out = new PrintWriter(outStream);

         out.println("; FTS Configuration");
         out.println("comm_port=" + commPort);
         out.println("vdx_dir=" + vdxDir);
         out.println("products_db_driver=" + productsDbDriver.toString());
         out.println("products_db_location=" + productsDbLocation);
         out.println("products_db_user=" + productsDbUser);
         out.println("products_db_pass=" + productsDbPass);

         out.flush();
         outStream.close();
      }
      catch (Exception e)
      {
         MessageBox mbox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
         mbox.setMessage(e.getLocalizedMessage());
         mbox.open();
      }
   }
}
