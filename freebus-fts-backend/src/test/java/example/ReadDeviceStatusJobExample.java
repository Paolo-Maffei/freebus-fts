package example;

import org.freebus.fts.backend.job.ReadDeviceStatusJob;
import org.freebus.fts.common.Environment;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.link.serial.SerialPortUtil;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramAdapter;
import org.freebus.knxcomm.types.LinkMode;

/**
 * Starts a {@link ReadDeviceStatusJob} job for a specific device.
 * 
 * You can change the bus connection in the constructor, and the physical
 * address of the target device by changing {@link #target} just below here.
 */
public class ReadDeviceStatusJobExample extends TelegramAdapter
{
   // Physical address of the device to read details from
   private final PhysicalAddress target = new PhysicalAddress(1, 1, 12);

   private final BusInterface bus;

   /**
    * Create the example.
    * 
    * @throws Exception
    */
   public ReadDeviceStatusJobExample() throws Exception
   {
      //
      // Bus connection
      //
      bus = BusInterfaceFactory.newSerialInterface(SerialPortUtil.getPortNames()[0]);
      // bus = BusInterfaceFactory.newKNXnetInterface("localhost",
      // KNXnetConnection.defaultPortUDP);

      bus.addListener(this);
      bus.open(LinkMode.LinkLayer);

      final ReadDeviceStatusJob job = new ReadDeviceStatusJob(target);
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
    * Start the application.
    * 
    * @throws Exception
    */
   public static void main(String[] args) throws Exception
   {
      Environment.init();

      ReadDeviceStatusJobExample tst = null;
      try
      {
         tst = new ReadDeviceStatusJobExample();
         Thread.sleep(5000);
      }
      finally
      {
         if (tst != null)
            tst.dispose();
      }
   }
}
