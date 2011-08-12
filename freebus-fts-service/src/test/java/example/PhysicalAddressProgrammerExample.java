package example;

import org.freebus.fts.common.Environment;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.service.devicecontroller.DeviceController;
import org.freebus.fts.service.devicecontroller.DeviceProgrammerType;
import org.freebus.fts.service.devicecontroller.internal.Bcu1DeviceController;
import org.freebus.fts.service.job.Job;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.types.LinkMode;

/**
 * Program a device.
 *
 * You can change the bus connection in {@link #DeviceScannerJobExample()}, the
 * constructor.
 */
public class PhysicalAddressProgrammerExample
{
   private final BusInterface bus;

   // The physical address to program
   private static final PhysicalAddress newAddr = new PhysicalAddress(1, 1, 14);

   /**
    * Create the bus monitor.
    *
    * @throws Exception
    */
   public PhysicalAddressProgrammerExample() throws Exception
   {
      //
      // Bus connection
      //
      // bus = BusInterfaceFactory.newSerialInterface(SerialPortUtil.getPortNames()[0]);
      bus = BusInterfaceFactory.newSerialInterface("/dev/ttyUSB0");
      // bus = BusInterfaceFactory.newKNXnetInterface("localhost", KNXnetLink.defaultPortUDP);

      bus.open(LinkMode.LinkLayer);

      final Area area = new Area();
      area.setAddress(newAddr.getZone());

      final Line line = new Line();
      line.setAddress(newAddr.getLine());
      area.add(line);

      final Device device = new Device();
      device.setAddress(newAddr.getNode());
      line.add(device);

      final DeviceController controller = new Bcu1DeviceController(device);
      final Job job = controller.getProgrammerJob(DeviceProgrammerType.PHYSICAL_ADDRESS);

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

      PhysicalAddressProgrammerExample tst = null;
      try
      {
         tst = new PhysicalAddressProgrammerExample();

         System.out.println("Job done, waiting for extra telegrams");
         Thread.sleep(7000);
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
