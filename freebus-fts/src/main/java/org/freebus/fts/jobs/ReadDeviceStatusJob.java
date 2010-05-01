package org.freebus.fts.jobs;

import org.freebus.fts.common.address.Address;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.ADCRead;
import org.freebus.knxcomm.application.DeviceDescriptorRead;
import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.application.MemoryRead;
import org.freebus.knxcomm.applicationData.DeviceDescriptorProperties;
import org.freebus.knxcomm.applicationData.DeviceDescriptorPropertiesFactory;
import org.freebus.knxcomm.applicationData.MemoryAddressTypes;
import org.freebus.knxcomm.jobs.JobQueue;
import org.freebus.knxcomm.jobs.JobSteps;
import org.freebus.knxcomm.jobs.JobStepsQueue;
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

      DeviceDescriptorResponse deviceDescriptorResponse = new DeviceDescriptorResponse();
      int[] data = { 0x40, 0x00, 0x12 };

      deviceDescriptorResponse.fromRawData(data, 0, 3);
      DeviceDescriptorPropertiesFactory deviceDescriptorPropertiesFactory = new DeviceDescriptorPropertiesFactory();

      DeviceDescriptorProperties deviceDescriptorProperties = deviceDescriptorPropertiesFactory
            .getDeviceDescriptor(deviceDescriptorResponse);

      setFrom(new PhysicalAddress(0, 0, 0));
      setDest(address);
      setRepeated(true);
      setPriority(Priority.SYSTEM);
      add(new DeviceDescriptorRead(0));
      add(new MemoryRead(MemoryAddressTypes.ApplicationID, 1, 1));
      add(new ADCRead(1, 8));
      add(new MemoryRead(MemoryAddressTypes.SystemState));
      add(new MemoryRead(MemoryAddressTypes.RunError));
      add(new ADCRead(4, 8));
      add(new MemoryRead(MemoryAddressTypes.ApplicationID, 1, 4));
      add(new MemoryRead(MemoryAddressTypes.SystemState));
      add(new MemoryRead(MemoryAddressTypes.PEI_Type));
      JobStepsQueue jobStepsQueue;
      jobStepsQueue = new JobStepsQueue(this);
      jobStepsQueue.setDeviceDescriptorProperties(deviceDescriptorProperties);

      JobQueue jobQueue = JobQueue.getDefaultJobQueue();
      jobQueue.add(jobStepsQueue);
   }


}
