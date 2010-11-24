package example;

import org.apache.log4j.Logger;
import org.freebus.fts.common.Environment;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.service.job.Job;
import org.freebus.fts.service.job.JobListener;
import org.freebus.fts.service.job.device.SetPhysicalAddressJob;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.link.netip.KNXnetLink;
import org.freebus.knxcomm.types.LinkMode;

/**
 * An example program that sets the physical address of a device on the bus that
 * is in programming mode to 1.2.200
 */
public final class SetPhysicalAddressExample
{
   // The physical address to program
   private static final PhysicalAddress newAddr = new PhysicalAddress(1, 1, 10);

   public static void main(String[] args) throws Exception
   {
      final Logger logger = Logger.getLogger(SetPhysicalAddressExample.class);
      BusInterface bus = null;
      Job job;

      try
      {
         Environment.init();

         logger.info("*** Opening bus connection");
         bus = BusInterfaceFactory.newKNXnetInterface("localhost", KNXnetLink.defaultPortUDP);
//         bus = BusInterfaceFactory.newSerialInterface(SerialPortUtil.getPortNames()[0]);
         bus.open(LinkMode.LinkLayer);

         logger.info("*** Starting job");
         job = new SetPhysicalAddressJob(newAddr);

         job.addListener(new JobListener()
         {
            @Override
            public void progress(int done, String message)
            {
               if (message == null)
                  message = "";

               Logger.getLogger(SetPhysicalAddressExample.class).info("%%% Programming " + done + "%: " + message);
            }
         });

         job.run(bus);
      }
      finally
      {
         Thread.sleep(2000);

         logger.info("*** Closing connection");
         if (bus != null)
            bus.close();

         System.exit(0);
      }
   }
}
