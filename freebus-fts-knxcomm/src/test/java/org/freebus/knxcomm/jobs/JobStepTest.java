package org.freebus.knxcomm.jobs;

import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.ADCRead;
import org.freebus.knxcomm.application.DeviceDescriptorRead;
import org.freebus.knxcomm.application.IndividualAddressRead;
import org.freebus.knxcomm.application.IndividualAddressWrite;
import org.freebus.knxcomm.application.MemoryRead;
import org.freebus.knxcomm.application.Restart;
import org.freebus.knxcomm.application.memory.MemoryLocation;
import org.freebus.knxcomm.jobs.steps.JobSteps;
import org.freebus.knxcomm.jobs.steps.JobStepsQueue;

public class JobStepTest
{

   public void testStatus()
   {
      JobSteps jobSteps;
      JobStepsQueue jobStepsQueue;
      jobSteps = new JobSteps();
      jobSteps.setFrom(new PhysicalAddress(1, 1, 1));
      jobSteps.setDest(new PhysicalAddress(1, 2, 3));
      jobSteps.add(new DeviceDescriptorRead(0));
      jobSteps.add(new MemoryRead(MemoryLocation.System));
      jobSteps.add(new ADCRead(1, 5));
      jobStepsQueue = new JobStepsQueue(jobSteps);
      JobQueue.getDefaultJobQueue().add(jobStepsQueue);

   }

   public void testSetPhysicalAddress()
   {
      JobSteps jobSteps;
      JobStepsQueue jobStepsQueue;
      jobSteps = new JobSteps();
      jobSteps.setFrom(PhysicalAddress.NULL);
      jobSteps.setDest(GroupAddress.BROADCAST);
      jobSteps.add(new IndividualAddressRead());
      jobSteps.add(new IndividualAddressWrite(new PhysicalAddress(1, 1, 3)));
      jobSteps.add(new Restart());
      jobStepsQueue = new JobStepsQueue(jobSteps);
      JobQueue.getDefaultJobQueue().add(jobStepsQueue);
   }

   public void verifytheprogrammedaddress()
   {
      JobSteps jobSteps;
      JobStepsQueue jobStepsQueue;
      jobSteps = new JobSteps();
      jobSteps.setFrom(new PhysicalAddress(1, 1, 1));
      jobSteps.setDest(new PhysicalAddress(1, 1, 3));
      jobSteps.add(new DeviceDescriptorRead(0));
      jobStepsQueue = new JobStepsQueue(jobSteps);
      JobQueue.getDefaultJobQueue().add(jobStepsQueue);
   }
}
