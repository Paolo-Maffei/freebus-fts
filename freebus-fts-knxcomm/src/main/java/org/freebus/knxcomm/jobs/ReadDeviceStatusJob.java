package org.freebus.knxcomm.jobs;

import org.freebus.fts.common.address.Address;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.ADCRead;
import org.freebus.knxcomm.application.DeviceDescriptorRead;
import org.freebus.knxcomm.application.MemoryRead;
import org.freebus.knxcomm.application.memory.MemoryAddressType;
import org.freebus.knxcomm.jobs.steps.JobSteps;
import org.freebus.knxcomm.jobs.steps.JobStepsQueue;
import org.freebus.knxcomm.telegram.Priority;


public class ReadDeviceStatusJob extends JobSteps
{
   /**
    *
    */
   private static final long serialVersionUID = -1955762658391790946L;

   public ReadDeviceStatusJob(PhysicalAddress physicalAddress)
   {
      super();
   }

   public void ReadStatus(Address address) throws Exception
   {


      setFrom(new PhysicalAddress(0, 0, 0));
      setDest(address);
      setRepeated(true);
      setPriority(Priority.SYSTEM);
      add(new DeviceDescriptorRead(0));
      add(new MemoryRead(MemoryAddressType.ApplicationID, 1, 1));
      add(new ADCRead(1, 8));
      add(new MemoryRead(MemoryAddressType.SystemState));
      add(new MemoryRead(MemoryAddressType.RunError));
      add(new ADCRead(4, 8));
      add(new MemoryRead(MemoryAddressType.ApplicationID, 1, 4));
      add(new MemoryRead(MemoryAddressType.SystemState));
      add(new MemoryRead(MemoryAddressType.PEI_Type));
      JobStepsQueue jobStepsQueue;
      jobStepsQueue = new JobStepsQueue(this);

      JobQueue jobQueue = JobQueue.getDefaultJobQueue();
      jobQueue.add(jobStepsQueue);
   }


}
