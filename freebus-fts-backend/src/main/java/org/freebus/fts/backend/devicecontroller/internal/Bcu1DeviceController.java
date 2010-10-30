package org.freebus.fts.backend.devicecontroller.internal;

import java.util.List;
import java.util.Vector;

import org.freebus.fts.backend.devicecontroller.DeviceProgrammerType;
import org.freebus.fts.backend.exception.DeviceControllerException;
import org.freebus.fts.backend.job.device.Bcu1ProgrammerJob;
import org.freebus.fts.backend.job.device.DeviceProgrammerJob;
import org.freebus.fts.backend.job.device.SetPhysicalAddressJob;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceProgramming;

/**
 * A device controller for devices that are handled with the BCU-1 mechanisms.
 */
public final class Bcu1DeviceController extends GenericDeviceController
{
   /**
    * Create a device controller for a BCU-1 device.
    * 
    * @param device - the device to control.
    */
   public Bcu1DeviceController(Device device)
   {
      super(device);
   }

   /**
    * {@inheritDoc}
    * 
    * @throws DeviceControllerException
    */
   @Override
   public List<DeviceProgrammerJob> getRequiredProgrammerJobs() throws DeviceControllerException
   {
      final List<DeviceProgrammerJob> programmers = new Vector<DeviceProgrammerJob>();
      final DeviceProgramming progr = getDevice().getProgramming();
      final List<DeviceProgrammerType> types = new Vector<DeviceProgrammerType>(10);

      if (!progr.isPhysicalAddressValid())
         programmers.add(new SetPhysicalAddressJob(getDevice()));

      if (!progr.isProgramValid())
         types.add(DeviceProgrammerType.PROGRAM);
      if (!progr.isParametersValid())
         types.add(DeviceProgrammerType.PARAMETERS);
      if (!progr.isCommunicationValid())
         types.add(DeviceProgrammerType.COMMUNICATIONS);

      if (!types.isEmpty())
      {
         final Bcu1ProgrammerJob job = new Bcu1ProgrammerJob(this, types);
         if (!progr.isPhysicalAddressValid())
            job.setPhysicalAddressJobQueued();

         programmers.add(job);
      }

      return programmers;
   }

   /**
    * {@inheritDoc}
    * 
    * @throws DeviceControllerException
    */
   @Override
   public DeviceProgrammerJob getProgrammerJob(DeviceProgrammerType type) throws DeviceControllerException
   {
      if (type == DeviceProgrammerType.PHYSICAL_ADDRESS)
         return new SetPhysicalAddressJob(getDevice());
      else return new Bcu1ProgrammerJob(this, type);
   }
}
