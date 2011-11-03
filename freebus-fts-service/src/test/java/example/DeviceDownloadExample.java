package example;

import javax.persistence.EntityManagerFactory;

import org.freebus.fts.common.Environment;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.service.job.Job;
import org.freebus.fts.service.job.device.DeviceDownloadJob;
import org.freebus.fts.service.project.BasicProjectController;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.types.LinkMode;

/**
 * Download the details of a device.
 * 
 * You can change the bus connection in {@link #DeviceScannerJobExample()}, the
 * constructor.
 * 
 * In order to make this example work, you need:
 * 
 * <pre>
 *  - a working bus connection (configured with the {@link #bus} variable below),
 *  - a device on the bus with the physical address configured in {@link #ADDR} below,
 *  - a working FTS database (will be found via the FTS configuration file),
 *  - the product data for this device in the FTS database (imported from the product's VD).
 * </pre>
 */
public class DeviceDownloadExample
{
   // The physical address of the device to download
   private static final PhysicalAddress ADDR = new PhysicalAddress(1, 1, 50);

   // The bus connection
   private final BusInterface bus =
         // BusInterfaceFactory.newSerialInterface(SerialPortUtil.getPortNames()[0]);
         BusInterfaceFactory.newSerialInterface("/dev/ttyUSB0");
         // BusInterfaceFactory.newKNXnetInterface("localhost",KNXnetLink.defaultPortUDP);

   /**
    * Create the bus monitor.
    * 
    * @throws Exception
    */
   public DeviceDownloadExample() throws Exception
   {
      bus.open(LinkMode.LinkLayer);

      final Job job = new DeviceDownloadJob(ADDR);

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
    * Start the application.
    * 
    * @throws Exception
    */
   public static void main(String[] args) throws Exception
   {
      Environment.init();

      final EntityManagerFactory emf = DatabaseResources.createDefaultEntityManagerFactory();
      DatabaseResources.setEntityManagerFactory(emf);

      ProjectManager.setController(new BasicProjectController());


      DeviceDownloadExample tst = null;
      try
      {
         tst = new DeviceDownloadExample();

         System.out.println("*** Job done, waiting for extra telegrams");
         Thread.sleep(3000);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         Thread.sleep(250);
      }
      finally
      {
         if (tst != null)
            tst.dispose();

         System.out.println("*** Done, exit");
         System.exit(0);
      }
   }
}
