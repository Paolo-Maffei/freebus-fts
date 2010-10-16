package example;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.freebus.fts.backend.job.JobListener;
import org.freebus.fts.backend.job.ReadDeviceDetailsJob;
import org.freebus.fts.common.Environment;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.link.serial.SerialPortUtil;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramAdapter;
import org.freebus.knxcomm.types.LinkMode;

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
   // Physical address of the device to read details from
   private final PhysicalAddress target = new PhysicalAddress(1, 1, 13);

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
      bus = BusInterfaceFactory.newSerialInterface(SerialPortUtil.getPortNames()[0]);
      //bus = BusInterfaceFactory.newKNXnetInterface("localhost", KNXnetLink.defaultPortUDP);

      bus.addListener(this);
      bus.open(LinkMode.LinkLayer);
   }

   /**
    * Run the example
    * 
    * @throws IOException
    */
   public void run() throws IOException
   {
      final ReadDeviceDetailsJob job = new ReadDeviceDetailsJob(target);

      job.addListener(new JobListener()
      {
         @Override
         public void progress(int done, String message)
         {
            if (message == null)
               message = "";

            Logger.getLogger(SetPhysicalAddressExample.class).info("%%% Job " + done + "%: " + message);
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
         Logger.getLogger(ReadDeviceDetailsJobExample.class).error(e);
      }
      finally
      {
         if (tst != null)
         {
            Logger.getLogger(ReadDeviceDetailsJobExample.class).debug("closing connection");
            tst.dispose();
         }

         Thread.sleep(500);
         Logger.getLogger(ReadDeviceDetailsJobExample.class).debug("done, exit");
         System.exit(0);
      }
   }
}
