package org.freebus.fts.backend.devicecontroller.internal;

import org.freebus.fts.backend.DeviceController;
import org.freebus.fts.backend.devicecontroller.DeviceProgrammer;
import org.freebus.knxcomm.BusInterface;

/**
 * A programmer that programs the parameters of a device.
 */
public class ParametersProgrammer implements DeviceProgrammer
{
   private final DeviceController controller;

   /**
    * Create a parameter programmer.
    *
    * @param controller - the device controller to use.
    */
   public ParametersProgrammer(DeviceController controller)
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
