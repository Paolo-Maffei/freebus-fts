package org.freebus.fts.backend.devicecontroller;

import org.freebus.fts.backend.DeviceController;
import org.freebus.fts.project.DeviceProgramming;

/**
 * Abstract base class for device programmers.
 */
public abstract class AbstractDeviceProgrammer implements DeviceProgrammer
{
   protected final DeviceController controller;
   protected final DeviceProgrammerType type;

   /**
    * Create an abstract device programmer.
    * 
    * @param controller - the device controller to use.
    * @param type - the type of the device programmer. May be null for custom programmers.
    */
   protected AbstractDeviceProgrammer(DeviceController controller, DeviceProgrammerType type)
   {
      this.controller = controller;
      this.type = type;
   }

   /**
    * Mark the programming step as done in the {@link DeviceProgramming device
    * programming} of the device. Does nothing if the {@link #type} is null.
    */
   protected void programmingDone()
   {
      if (type == null)
         return;

      final DeviceProgramming progr = controller.getDevice().getProgramming();

      if (type == DeviceProgrammerType.COMMUNICATIONS)
         progr.setCommunicationValid(true);
      else if (type == DeviceProgrammerType.PARAMETERS)
         progr.setParametersValid(true);
      else if (type == DeviceProgrammerType.PROGRAM)
         progr.setProgramValid(true);
      else if (type == DeviceProgrammerType.PHYSICAL_ADDRESS)
         progr.setPhysicalAddressValid(true);
      else throw new RuntimeException("invalid programmer type " + type);

      progr.setLastUploadNow();
   }
}
