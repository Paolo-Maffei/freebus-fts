package example;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.freebus.fts.common.Environment;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.telegram.Telegram;

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
      // Create a data connection to the device
      logger.info("*** Opening data-connection to " + deviceAddress);
      final DataConnection con = iface.connect(deviceAddress);
      logger.debug("Data-connection to " + deviceAddress + " established");

      logger.info("*** Sending memory-read telegram");
      final Telegram telegram = new Telegram();
      telegram.setApplication(ApplicationType.Memory_Read, new int[] { 10, 0, 1 });
      con.send(telegram);

      logger.info("*** done, waiting some seconds before terminating");
   }

   /**
    * The main.
    */
   public static void main(String[] args) throws Exception
   {
      Environment.init();

      ConnectedDataTransfer cdt = null;
      try
      {
         cdt = new ConnectedDataTransfer();
         cdt.run();

         Thread.sleep(500);
         logger.info("*** Sleeping some seconds to allow device timeouts to occur");
         Thread.sleep(7000);
      }
      finally
      {
         if (cdt != null)
            cdt.dispose();
      }
   }
}
