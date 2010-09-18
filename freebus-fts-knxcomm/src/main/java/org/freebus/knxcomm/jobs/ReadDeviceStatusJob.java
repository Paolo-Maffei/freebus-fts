package org.freebus.knxcomm.jobs;

import java.io.IOException;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;

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

   @Override
   public void main(BusInterface bus) throws IOException
   {
//    setFrom(new PhysicalAddress(0, 0, 0));
//    setDest(address);
//    setRepeated(true);
//    setPriority(Priority.SYSTEM);
//    add(new DeviceDescriptorRead(0));
//    add(new MemoryRead(MemoryAddressType.ApplicationID, 1, 1));
//    add(new ADCRead(1, 8));
//    add(new MemoryRead(MemoryAddressType.SystemState));
//    add(new MemoryRead(MemoryAddressType.RunError));
//    add(new ADCRead(4, 8));
//    add(new MemoryRead(MemoryAddressType.ApplicationID, 1, 4));
//    add(new MemoryRead(MemoryAddressType.SystemState));
//    add(new MemoryRead(MemoryAddressType.PEI_Type));
//    JobStepsQueue jobStepsQueue;
//    jobStepsQueue = new JobStepsQueue(this);
//
//    JobQueue jobQueue = JobQueue.getDefaultJobQueue();
//    jobQueue.add(jobStepsQueue);
   }
}
