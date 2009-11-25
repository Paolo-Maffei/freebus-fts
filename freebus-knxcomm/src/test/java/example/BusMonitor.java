package example;

import java.io.IOException;

import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * A simple bus monitor that opens a bus interface, and prints the received telegrams to
 * the console.
 */
public class BusMonitor implements TelegramListener
{
   private final BusInterface iface;

   /**
    * Create the bus monitor.
    * @throws IOException 
    */
   public BusMonitor() throws IOException
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

      iface = BusInterfaceFactory.newSerialInterface(commPort);
      iface.addListener(this);
      iface.open();
   }

   @Override
   public void telegramReceived(Telegram telegram)
   {
      System.out.println(telegram.toString());
   }

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
      @SuppressWarnings("unused")
      BusMonitor mon = new BusMonitor();

      while (true)
         Thread.sleep(1000);
   }
}
