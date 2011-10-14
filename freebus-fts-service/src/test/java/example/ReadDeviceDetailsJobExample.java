package example;

import java.io.IOException;

import org.freebus.fts.common.Environment;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.service.exception.JobFailedException;
import org.freebus.fts.service.job.JobListener;
import org.freebus.fts.service.job.ReadDeviceDetailsJob;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramAdapter;
import org.freebus.knxcomm.types.LinkMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts a {@link ReadDeviceDetailsJob} job for a specific device.
 *
 * You can change the bus connection in {@link #ReadDeviceDetailsJobExample()},
 * the constructor.
 *
 * You can change the physical address of the target device by changing
 * {@link #target} just below here.
 */
public class ReadDeviceDetailsJobExample extends TelegramAdapter
{
   private final static Logger LOGGER = LoggerFactory.getLogger(ReadDeviceDetailsJobExample.class);

   // Physical address of the device to read details from
   private final PhysicalAddress target = new PhysicalAddress(1, 1, 12);

   private final BusInterface bus;

   /**
    * Create the example.
    *
    * @throws Exception
    */
   public ReadDeviceDetailsJobExample() throws Exception
   {
      //
      // Bus connection
      //
      //bus = BusInterfaceFactory.newSerialInterface(SerialPortUtil.getPortNames()[0]);
      bus = BusInterfaceFactory.newSerialInterface("/dev/ttyUSB0");
      //bus = BusInterfaceFactory.newKNXnetInterface("localhost", KNXnetLink.defaultPortUDP);

      bus.addListener(this);
      bus.open(LinkMode.LinkLayer);
   }

   /**
    * Run the example
    *
    * @throws IOException
    */
   public void run() throws JobFailedException
   {
      final ReadDeviceDetailsJob job = new ReadDeviceDetailsJob(target);

      job.addListener(new JobListener()
      {
         @Override
         public void progress(int done, String message)
         {
            if (message == null)
               message = "";

            LOGGER.info("%%% Job " + done + "%: " + message);
         }
      });

      job.run(bus);
   }

   /**
    * Close the resources
    */
   public void dispose()
   {
      if (bus != null && bus.isConnected())
         bus.close();
   }

   /**
    * A telegram was received.
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
      // System.out.println(telegram.toString());
   }

   /**
    * Start the application.
    *
    * @throws Exception
    */
   public static void main(String[] args) throws Exception
   {
      Environment.init();

      ReadDeviceDetailsJobExample tst = null;
      try
      {
         tst = new ReadDeviceDetailsJobExample();
         tst.run();
      }
      catch (Exception e)
      {
         LOGGER.error("top level exception", e);
      }
      finally
      {
         if (tst != null)
         {
            LOGGER.debug("closing connection");
            tst.dispose();
         }

         Thread.sleep(500);
         LOGGER.debug("done, exit");
         System.exit(0);
      }
   }
}
