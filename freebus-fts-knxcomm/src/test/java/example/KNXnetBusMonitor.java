package example;

import org.freebus.fts.common.Environment;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.application.IndividualAddressRead;
import org.freebus.knxcomm.netip.KNXnetConnection;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramListener;
import org.freebus.knxcomm.telegram.Transport;
import org.freebus.knxcomm.types.LinkMode;

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
      iface = BusInterfaceFactory.newKNXnetInterface("localhost", KNXnetConnection.defaultPortUDP);
      iface.addListener(this);
      iface.open(LinkMode.BusMonitor);

      final Telegram telegram = new Telegram();
      telegram.setFrom(PhysicalAddress.NULL);
      telegram.setPriority(Priority.SYSTEM);
      telegram.setTransport(Transport.Individual);
      telegram.setDest(GroupAddress.BROADCAST);
      telegram.setApplication(new IndividualAddressRead());
      iface.send(telegram);
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
      System.out.println("Telegram received: " + telegram.toString());
   }

   /**
    * A telegram was sent by our application.
    */
   @Override
   public void telegramSent(Telegram telegram)
   {
      System.out.println("Telegram sent: " + telegram.toString());
   }

   /**
    * Start the application.
    *
    * @throws Exception
    */
   public static void main(String[] args) throws Exception
   {
      Environment.init();

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
