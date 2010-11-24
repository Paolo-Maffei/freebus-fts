package org.freebus.fts.service.job;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.application.DeviceDescriptorRead;
import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor;
import org.freebus.knxcomm.telegram.Priority;

/**
 * Gather details about a specific device that is identified by a physical
 * address.
 */
public final class ReadDeviceDetailsJob extends ListenableJob
{
   private final Logger logger = Logger.getLogger(getClass());
   private final PhysicalAddress addr;

   private DeviceDescriptor deviceDescriptor;

   /**
    * Create a device-details reader job.
    * 
    * @param addr - the physical address of the device to read.
    */
   public ReadDeviceDetailsJob(PhysicalAddress addr)
   {
      this.addr = addr;
   }

   /**
    * @return The physical address of the device being read.
    */
   public PhysicalAddress getAddress()
   {
      return addr;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getLabel()
   {
      return null;
   }

   /**
    * Do the work.
    * 
    * @param con - the data connection to use.
    *
    * @throws TimeoutException
    * @throws IOException
    */
   private void innerMain(final DataConnection con) throws IOException, TimeoutException
   {
      Application reply;

      con.installMemoryAddressMapper();
      msleep(10);

      reply = con.query(new DeviceDescriptorRead(2));
      deviceDescriptor = ((DeviceDescriptorResponse) reply).getDescriptor();
      logger.info("%%% Device descriptor: " + deviceDescriptor);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void main(BusInterface bus) throws IOException
   {
      final DataConnection con = bus.connect(addr, Priority.SYSTEM);

      try
      {
         msleep(50);
         innerMain(con);
         con.close();
      }
      catch (TimeoutException e)
      {
         throw new IOException("timeout: no reply from device " + addr, e);
      }
      finally
      {
      }
   }
}
