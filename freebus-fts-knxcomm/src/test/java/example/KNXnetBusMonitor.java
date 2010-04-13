package example;

import org.freebus.fts.common.Environment;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.application.IndividualAddressRead;
import org.freebus.knxcomm.netip.KNXnetConnection;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

//Layer 0(011736F0,4BC38D4A) Recv(014): 06 10 02 01 00 0E 08 01 00 00 00 00 C6 D6
//Layer 1(011736F0,4BC38D4A) Recv(008): 08 01 00 00 00 00 C6 D6
//Layer 8(01173240,4BC38D4A) SEARCH
//Layer 1(011736F0,4BC38D4A) Send(068): 08 01 7F 00 00 01 0E 57 36 01 02 00 00 00 00 00 00 00 00 00 00 00 E0 00 17 0C 00 00 00 00 00 00 65 69 62 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 06 02 02 01 04 01
//Layer 0(011736F0,4BC38D4A) Send(074): 06 10 02 02 00 4A 08 01 7F 00 00 01 0E 57 36 01 02 00 00 00 00 00 00 00 00 00 00 00 E0 00 17 0C 00 00 00 00 00 00 65 69 62 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 06 02 02 01 04 01
//Layer 0(011736F0,4BC38D4B) Recv(014): 06 10 02 03 00 0E 08 01 00 00 00 00 C6 D6
//Layer 1(011736F0,4BC38D4B) Recv(008): 08 01 00 00 00 00 C6 D6
//Layer 8(01173240,4BC38D4B) DESCRIBE
//Layer 1(011736F0,4BC38D4B) Send(060): 36 01 02 00 00 00 00 00 36 01 02 00 00 00 E0 00 17 0C 00 00 00 00 00 00 65 69 62 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 06 02 02 01 04 01
//Layer 0(011736F0,4BC38D4B) Send(066): 06 10 02 04 00 42 36 01 02 00 00 00 00 00 36 01 02 00 00 00 E0 00 17 0C 00 00 00 00 00 00 65 69 62 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 06 02 02 01 04 01
//Layer 0(011736F0,4BC38D4B) Recv(027): 06 10 02 05 00 1B 18 08 01 00 00 00 00 C6 D6 08 01 00 00 00 00 C6 D6 04 04 02 00
//Layer 1(011736F0,4BC38D4B) Recv(021): 18 08 01 00 00 00 00 C6 D6 08 01 00 00 00 00 C6 D6 04 04 02 00

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
      iface = BusInterfaceFactory.newKNXnetInterface("taferner.dyndns.org", KNXnetConnection.defaultPortUDP);
      iface.addListener(this);
      iface.open();

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
    * The sending of a telegram was confirmed by the bus coupling unit.
    */
   @Override
   public void telegramSendConfirmed(Telegram telegram)
   {
      System.out.println("Telegram confirmed: " + telegram.toString());
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
