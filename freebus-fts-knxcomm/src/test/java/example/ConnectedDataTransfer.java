package example;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.telegram.PhysicalAddress;

/**
 * An example program that shows the connected data transfer methods.
 */
public final class ConnectedDataTransfer
{
   private final BusInterface iface;

   /**
    * The physical address of the device to which this test program connects.
    */
   private final PhysicalAddress deviceAddress = new PhysicalAddress(1, 1, 6);

   /**
    * Create the example main object.
    * 
    * @throws Exception
    */
   public ConnectedDataTransfer() throws Exception
   {
      // Create the bus interface
      iface = createBusInterface();
      iface.open();
   }

   /**
    * Close the resources.
    */
   public void dispose()
   {
      if (iface != null && iface.isConnected())
         iface.close();
   }

   /**
    * Create the bus interface
    * @throws Exception 
    */
   public BusInterface createBusInterface() throws Exception
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

      return BusInterfaceFactory.newSerialInterface(commPort);
   }

   /**
    * Do the real (example) work.
    */
   public void run() throws IOException
   {
      // Create a data connection to the device and open the connection
      final DataConnection con = iface.connect(deviceAddress);
      con.open();
   }

   /**
    * The main.
    */
   public static void main(String[] args) throws Exception
   {
      // Configure Log4J
      BasicConfigurator.configure();

      ConnectedDataTransfer cdt = null;
      try
      {
         cdt = new ConnectedDataTransfer();
         cdt.run();
      }
      finally
      {
         if (cdt != null)
            cdt.dispose();
      }
   }
}
