package example;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.freebus.fts.common.Environment;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.application.MemoryRead;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * An example program that shows the connected data transfer methods.
 */
public final class ConnectedDataTransfer
{
   private static final Logger logger = Logger.getLogger(ConnectedDataTransfer.class);

   private final BusInterface bus;

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
      logger.info("*** Opening bus connection");
      bus = createBusInterface();
      bus.open();
   }

   /**
    * Close the resources.
    */
   public void dispose()
   {
      if (bus != null && bus.isConnected())
      {
         logger.info("*** Closing bus connection");
         bus.close();
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
    * @throws InterruptedException
    */
   public void run() throws IOException, InterruptedException
   {
      logger.info("*** Opening data-connection to " + deviceAddress);
      final DataConnection con = bus.connect(deviceAddress);
      logger.debug("Data-connection to " + deviceAddress + " established");
      con.receiveMultiple(1000);

      logger.info("*** Sending memory-read telegram");
      final Telegram telegram = new Telegram();
      telegram.setApplication(new MemoryRead(1, 10));

      con.receiveMultiple(0);
      con.send(telegram);

      logger.info("*** Waiting for reply");
      List<Telegram> replies = con.receiveMultiple(2000);
      for (Telegram reply: replies)
         logger.debug("*** Reply: " + reply);

      logger.info("*** Closing data-connection");
      con.close();

      logger.info("*** Done, waiting some seconds before terminating");
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
