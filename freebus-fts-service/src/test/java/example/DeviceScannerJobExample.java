package example;

import org.freebus.fts.common.Environment;
import org.freebus.fts.service.job.DeviceScannerJob;
import org.freebus.fts.service.job.entity.DeviceInfo;
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
   private final DeviceScannerJob job;

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
      bus = BusInterfaceFactory.newSerialInterface("/dev/ttyS0");
      //      bus = BusInterfaceFactory.newKNXnetInterface("localhost", KNXnetLink.defaultPortUDP);

      bus.addListener(this);
      bus.open(LinkMode.LinkLayer);

      job = new DeviceScannerJob(zone, line);
      job.setMinAddress(1);
      job.setMaxAddress(110);
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
         Thread.sleep(2000);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         if (tst != null)
            tst.dispose();

         if (tst != null && tst.job != null)
         {
            System.out.println("\nScanned devices:");
            for (final DeviceInfo info : tst.job.getDeviceInfos())
            {
               System.out.print("  ");
               System.out.print(info.getAddress().toString());
               System.out.print(": manufacturer #");
               System.out.print(info.getManufacturerId());
               if (info.getDeviceType() != -1)
               {
                  System.out.print(", device-type 0x");
                  System.out.print(Integer.toHexString(info.getDeviceType()));
               }
               System.out.print(", ");
               System.out.print(info.getDescriptor().toString());
               System.out.println();
            }
         }

         System.exit(0);
      }
   }
}
