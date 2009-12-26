package org.freebus.knxcomm.serial;

import gnu.io.CommPortIdentifier;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import org.freebus.knxcomm.internal.JarLoader;
import org.freebus.knxcomm.internal.SystemPathLoader;

/**
 * Utility methods around serial ports.
 */
public final class SerialPortUtil
{
   private static boolean serialPortLibLoaded = false;

   /**
    * Ensure that the native serial port library is loaded.
    */
   static
   {
      SerialPortUtil.loadSerialPortLib();
   }

   /**
    * @return the names of the available serial ports, alphabetically sorted.
    */
   public static String[] getPortNames()
   {
      final Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();
      final Vector<String> portNames = new Vector<String>(20);

      while (portList.hasMoreElements())
      {
         CommPortIdentifier portIdent = (CommPortIdentifier) portList.nextElement();

         if (portIdent.getPortType() == CommPortIdentifier.PORT_SERIAL)
            portNames.add(portIdent.getName());
      }

      final String[] result = new String[portNames.size()];
      portNames.toArray(result);

      Arrays.sort(result);
      return result;
   }

   /**
    * Ensure that the native serial port library is loaded.
    * @return 
    */
   public static void loadSerialPortLib()
   {
      if (serialPortLibLoaded) return;

      final String osName = System.getProperty("os.name").toLowerCase();
      final String osArch = System.getProperty("os.arch").toLowerCase();
      String dirName;
      if (osName.startsWith("linux"))
      {
         if (osArch.equals("amd64") || osArch.equals("x86_64"))
            dirName = "linux-x86_64";
         else dirName = "linux-i686";
      }
      else if (osName.startsWith("windows"))
      {
         dirName = "windows-" + osArch;
      }
      else
      {
         dirName = osName + '-' + osArch;
      }

      String libPath = "";
      for (String dir : new String[] { "contrib/rxtx/", "../freebus-knxcomm/contrib/rxtx/" })
      {
         libPath = (new File(dir + dirName)).getAbsolutePath();
         try
         {
            SystemPathLoader.loadLibrary(libPath, "rxtxSerial");
            serialPortLibLoaded = true;
            break;
         }
         catch (UnsatisfiedLinkError e)
         {
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }

      if (!serialPortLibLoaded)
         throw new RuntimeException("Failed to load native rxtxSerial library");

      boolean jarLoaded = false;
      try
      {
         JarLoader.loadJar(new String[] { "contrib/rxtx/RXTXcomm.jar", "../freebus-knxcomm/contrib/rxtx/RXTXcomm.jar" });
         CommPortIdentifier.getPortIdentifiers();
         jarLoaded = true;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      if (!jarLoaded)
         throw new RuntimeException("Failed to load RXTXcomm jar");
   }
}
