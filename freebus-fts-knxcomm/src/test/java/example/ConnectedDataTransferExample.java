package example;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.freebus.fts.common.Environment;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.application.ADCRead;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.serial.SerialPortUtil;
import org.freebus.knxcomm.types.LinkMode;

/**
 * An example program that shows the connected data transfer methods.
 */
public final class ConnectedDataTransferExample
{
   // The physical address of the device to which this test program connects.
   private final PhysicalAddress deviceAddress = new PhysicalAddress(1, 1, 6);

   private static final Logger logger = Logger.getLogger(ConnectedDataTransferExample.class);
   private final BusInterface bus;

   /**
    * Create the example main object.
    *
    * @throws Exception
    */
   public ConnectedDataTransferExample() throws Exception
   {
      logger.info("*** Opening bus connection");
      // bus = BusInterfaceFactory.newKNXnetInterface("localhost",
      // KNXnetConnection.defaultPortUDP);
      bus = BusInterfaceFactory.newSerialInterface(SerialPortUtil.getPortNames()[0]);
      bus.open(LinkMode.LinkLayer);
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
    * Do the real (example) work.
    *
    * @throws InterruptedException
    * @throws TimeoutException
    */
   public void run() throws IOException, InterruptedException, TimeoutException
   {
      logger.info("*** Opening data-connection to " + deviceAddress);

      DataConnection con = null;
      try
      {
         con = bus.connect(deviceAddress);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      logger.debug("Data-connection to " + deviceAddress + " established");
      con.receiveMultiple(500);

      Application replyApp;

      logger.info("*** Querying ADC channel #0");
      con.receiveMultiple(0);
      replyApp = con.query(new ADCRead(0, 5));
      logger.debug("*** Reply: " + replyApp);
      if (replyApp == null)
         throw new RuntimeException("No reply received");

      logger.info("*** Sending memory-read telegram");

      con.receiveMultiple(0);
      con.send(new ADCRead(1, 10));

      logger.info("*** Waiting for reply");
      List<Application> replies = con.receiveMultiple(2000);
      for (Application reply : replies)
         logger.debug("*** Reply: " + reply);

      logger.info("*** Closing data-connection");
      con.close();
   }

   /**
    * The main.
    */
   public static void main(String[] args) throws Exception
   {
      Environment.init();

      ConnectedDataTransferExample cdt = null;
      try
      {
         cdt = new ConnectedDataTransferExample();
         cdt.run();

         Thread.sleep(500);
         logger.info("*** Done, waiting some seconds before terminating");
         Thread.sleep(7000);
      }
      finally
      {
         if (cdt != null)
            cdt.dispose();
      }
   }
}
