package org.freebus.fts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.freebus.fts.comm.ConnectType;
import org.freebus.fts.utils.I18n;
import org.xml.sax.SAXException;

/**
 * Configuration settings. The configuration is a global singleton object that
 * is automatically loaded.
 */
public final class Config
{
   static private Config instance = null;
   private String commPort = null;
   private ConnectType commType = ConnectType.SERIAL;
   private String tempDir = null;
   private String vdxDir = null;

   private final String configFileName;
   private final String homeDir;

   /**
    * @return The global configuration object.
    */
   static public Config getInstance()
   {
      if (instance == null) instance = new Config();
      return instance;
   }

   /**
    * Set the communication port type.
    */
   public void setCommType(ConnectType commType)
   {
      this.commType = commType;
   }

   /**
    * @return the communication port type.
    */
   public ConnectType getCommType()
   {
      return commType;
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
    * Set the temp directory.
    */
   public void setTempDir(String tempDir)
   {
      this.tempDir = tempDir;
   }

   /**
    * @return the temp directory.
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
      String osname = System.getProperty("os.name", "").toLowerCase();
      if (osname.startsWith("windows"))
      {
         commPort = "COM1";
         tempDir = "c:/windows/temp";
         homeDir = System.getenv("USERPROFILE");
         configFileName = homeDir + "/fts-config.ini";
      }
      else if (osname.startsWith("linux"))
      {
         commPort = "/dev/ttyS0";
         tempDir = "/tmp";
         homeDir = System.getenv("HOME");
         configFileName = homeDir + "/.fts-config";
      }
      // else if (osname.startsWith("mac")) commPort = null; // what here?
      else
      {
         configFileName = "fts-config.xml";
         homeDir = System.getenv("HOME");
      }

      vdxDir = homeDir;

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
               else if (key.equals("comm_type")) setCommType(ConnectType.valueOf(val));
               else if (key.equals("vdx_dir")) vdxDir = val;
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
         out.println("comm_type=" + commType.toString());
         out.println("vdx_dir=" + vdxDir);

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
