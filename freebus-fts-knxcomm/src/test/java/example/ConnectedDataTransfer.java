package example;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.DataConnection;

/**
 * An example program that shows the connected data transfer methods.
 */
public final class ConnectedDataTransfer
{
   private static final Logger logger = Logger.getLogger(ConnectedDataTransfer.class);

   private final BusInterface iface;

   /**
    * The physical address of the device to which this test program connects.
    */
   private final PhysicalAddress deviceAddress = new PhysicalAddress(1, 1, 6);

   /**
    * Create the example main object.
    * 
    * @throws Exception
    */
   public ConnectedDataTransfer() throws Exception
   {
      // Create the bus interface
      logger.info("*** Opening bus connection");
      iface = createBusInterface();
      iface.open();
   }

   /**
    * Close the resources.
    */
   public void dispose()
   {
      if (iface != null && iface.isConnected())
      {
         logger.info("*** Closing bus connection");
         iface.close();
      }
   }

   /**
    * Create the bus interface
    * @throws Exception 
    */
   public BusInterface createBusInterface() throws Exception
   {
      String commPort;

      final String osname = System.getProperty("os.name", "").toLowerCase();
      if (osname.startsWith("windows"))
      {
         commPort = "COM1";
      }
      else if (osname.startsWith("linux"))
      {
         commPort = "/dev/ttyS0";
      }
      else
      {
         throw new RuntimeException("Sorry, but your platform is not supported by this example");
      }

      return BusInterfaceFactory.newSerialInterface(commPort);
   }

   /**
    * Do the real (example) work.
    */
   public void run() throws IOException
   {
      // Create a data connection to the device and open the connection
      logger.info("*** Connecting to " + deviceAddress);
      final DataConnection con = iface.connect(deviceAddress);
      con.open();
   }

   /**
    * The main.
    */
   public static void main(String[] args) throws Exception
   {
      // Configure Log4J
      BasicConfigurator.configure();

      ConnectedDataTransfer cdt = null;
      try
      {
         cdt = new ConnectedDataTransfer();
         cdt.run();

         Thread.sleep(500);
         logger.info("*** Sleeping some seconds to allow any timeout to occur");
         Thread.sleep(8000);
      }
      finally
      {
         if (cdt != null)
            cdt.dispose();
      }
   }
}
