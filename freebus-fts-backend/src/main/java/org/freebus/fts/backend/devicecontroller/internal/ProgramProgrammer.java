package org.freebus.fts.backend.devicecontroller.internal;

import org.freebus.fts.backend.DeviceController;
import org.freebus.fts.backend.devicecontroller.DeviceProgrammer;
import org.freebus.knxcomm.BusInterface;

/**
 * A programmer that programs the application program of a device.
 */
public class ProgramProgrammer implements DeviceProgrammer
{
   private final DeviceController controller;

   /**
    * Create an application program programmer.
    *
    * @param controller - the device controller to use.
    */
   public ProgramProgrammer(DeviceController controller)
   {
      this.controller = controller;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void program(BusInterface iface)
   {
      // TODO
   }
}
