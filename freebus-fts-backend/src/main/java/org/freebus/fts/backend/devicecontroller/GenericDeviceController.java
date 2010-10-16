package org.freebus.fts.backend.devicecontroller;

import org.freebus.fts.backend.devicecontroller.internal.CommunicationsProgrammer;
import org.freebus.fts.backend.devicecontroller.internal.ParametersProgrammer;
import org.freebus.fts.backend.devicecontroller.internal.PhysicalAddressProgrammer;
import org.freebus.fts.backend.devicecontroller.internal.ProgramProgrammer;
import org.freebus.fts.project.Device;

/**
 * A device controller with methods for most BCU types.
 */
public class GenericDeviceController extends AbstractDeviceController
{
   /**
    * Create a generic device controller.
    *
    * @param device - the device to control.
    */
   public GenericDeviceController(Device device)
   {
      super(device);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DeviceProgrammer getProgrammer(DeviceProgrammerType type)
   {
      if (type == DeviceProgrammerType.PROGRAM)
         return new ProgramProgrammer(this);
      else if (type == DeviceProgrammerType.COMMUNICATIONS)
         return new CommunicationsProgrammer(this);
      else if (type == DeviceProgrammerType.PARAMETERS)
         return new ParametersProgrammer(this);
      else if (type == DeviceProgrammerType.PHYSICAL_ADDRESS)
         return new PhysicalAddressProgrammer(this);

      throw new IllegalArgumentException("No device programmer available for: " + type);
   }
}
