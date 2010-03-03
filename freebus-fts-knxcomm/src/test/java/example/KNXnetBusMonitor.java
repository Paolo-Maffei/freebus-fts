package example;

import org.apache.log4j.BasicConfigurator;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * A simple bus monitor that opens a bus interface using the KNXnet/IP server on
 * the local host. The received telegrams are printed to the console.
 */
public class KNXnetBusMonitor implements TelegramListener
{
   private final BusInterface iface;

   /**
    * Create the bus monitor.
    * 
    * @throws Exception
    */
   public KNXnetBusMonitor() throws Exception
   {
      iface = BusInterfaceFactory.newKNXnetInterface("192.168.1.120", 3671);
      iface.addListener(this);
      iface.open();
   }

   /**
    * Close the resources
    */
   public void dispose()
   {
      if (iface != null && iface.isConnected())
         iface.close();
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
      // Configure Log4J
      BasicConfigurator.configure();

      KNXnetBusMonitor mon = null;
      try
      {
         mon = new KNXnetBusMonitor();

         while (true)
            Thread.sleep(1000);

      }
      finally
      {
         if (mon != null)
            mon.dispose();
      }
   }
}
