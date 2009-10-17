package org.freebus.fts;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Configuration settings.
 *
 * The configuration is a global singleton object that is automatically
 * loaded.
 */
public final class Config
{
   static private Config instance = null;
   private String commPort = null;

   /**
    * @return The global configuration object.
    */
   static public Config getConfig()
   {
      if (instance==null) instance = new Config();
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
    * Create a configuration object.
    * Use {@link #getConfig} to access the configuration object.
    */
   private Config()
   {
      // determine the name of the serial port on several operating systems
      String osname = System.getProperty("os.name","").toLowerCase();
      if (osname.startsWith("windows")) commPort = "COM1";
      else if (osname.startsWith("linux")) commPort = "/dev/ttyS0";
      //else if (osname.startsWith("mac")) commPort = null; // what here?

      try
      {
         load();
      }
      catch (Exception e)
      {
         MessageBox mbox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
         mbox.setMessage(e.getLocalizedMessage());
         mbox.open();
      }
   }

   /**
    * Load the configuration.
    * Automatically called when the configuration object is created.
    * @throws ParserConfigurationException 
    * @throws IOException 
    * @throws SAXException 
    */
   public void load() throws ParserConfigurationException, SAXException, IOException
   {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse("fts-config.xml");
      doc.getDocumentElement().normalize();
   }

   /**
    * Save the configuration.
    */
   public void save()
   {
      // TODO
   }
}
