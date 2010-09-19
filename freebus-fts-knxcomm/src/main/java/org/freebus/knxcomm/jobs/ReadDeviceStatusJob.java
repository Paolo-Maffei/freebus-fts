package org.freebus.knxcomm.jobs;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.application.ADCRead;
import org.freebus.knxcomm.application.MemoryRead;
import org.freebus.knxcomm.application.memory.MemoryLocation;
import org.freebus.knxcomm.telegram.Priority;

/**
 * Currently unused, old, broken job.
 */
public class ReadDeviceStatusJob extends ListenableJob
{
   private final PhysicalAddress address;

   public ReadDeviceStatusJob(PhysicalAddress address)
   {
      this.address = address;
   }

   @Override
   public String getLabel()
   {
      // TODO Auto-generated method stub
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
      con.installMemoryAddressMapper();

      // old code: add(new MemoryRead(MemoryAddressType.ApplicationID, 1, 1));
      con.query(new MemoryRead(MemoryLocation.ApplicationID, 0, 5));

      // old code: add(new ADCRead(1, 8));
      con.query(new ADCRead(1, 8));

      // old code: add(new MemoryRead(MemoryAddressType.SystemState));
      con.query(new MemoryRead(MemoryLocation.SystemState, 0, 1));

      // old code: add(new MemoryRead(MemoryAddressType.RunError));
      con.query(new MemoryRead(MemoryLocation.RunError, 0, 1));

      // old code: add(new ADCRead(4, 8));
      con.query(new ADCRead(4, 8));

      // old code: add(new MemoryRead(MemoryAddressType.ApplicationID, 1, 4));
      // old code: add(new MemoryRead(MemoryAddressType.SystemState));

      // old code: add(new MemoryRead(MemoryAddressType.PEI_Type));
      con.query(new MemoryRead(MemoryLocation.PEI_Type, 0, 1));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void main(BusInterface bus) throws IOException, TimeoutException
   {
      final DataConnection con = bus.connect(address, Priority.SYSTEM);

      try
      {
         msleep(50);
         innerMain(con);
      }
      catch (TimeoutException e)
      {
         throw new IOException("timeout: no reply from device " + address, e);
      }
      finally
      {
         con.close();
      }
   }
}
