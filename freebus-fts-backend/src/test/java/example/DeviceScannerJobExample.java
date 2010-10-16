package example;

import org.freebus.fts.backend.job.DeviceScannerJob;
import org.freebus.fts.common.Environment;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramListener;
import org.freebus.knxcomm.types.LinkMode;

/**
 * Starts the device scanner job.
 * 
 * You can change the bus connection in {@link #DeviceScannerJobExample()}, the
 * constructor.
 */
public class DeviceScannerJobExample implements TelegramListener
{
   private final BusInterface bus;

   // Bus zone and line to scan
   private final static int zone = 1;
   private final static int line = 1;

   /**
    * Create the bus monitor.
    * 
    * @throws Exception
    */
   public DeviceScannerJobExample() throws Exception
   {
      //
      // Bus connection
      //
//      bus = BusInterfaceFactory.newSerialInterface(SerialPortUtil.getPortNames()[0]);
      bus = BusInterfaceFactory.newSerialInterface("/dev/ttyUSB0");
//      bus = BusInterfaceFactory.newKNXnetInterface("localhost", KNXnetLink.defaultPortUDP);

      bus.addListener(this);
      bus.open(LinkMode.LinkLayer);

      final DeviceScannerJob job = new DeviceScannerJob(zone, line);
      job.setMinAddress(1);
      job.setMaxAddress(40);
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
      System.out.println(telegram.toString());
   }

   /**
    * A telegram was sent by our application.
    */
   @Override
   public void telegramSent(Telegram telegram)
   {
   }

   /**
    * Start the application.
    * 
    * @throws Exception
    */
   public static void main(String[] args) throws Exception
   {
      Environment.init();

      DeviceScannerJobExample tst = null;
      try
      {
         tst = new DeviceScannerJobExample();
         Thread.sleep(20000);

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         if (tst != null)
            tst.dispose();

         System.exit(0);
      }
   }
}
