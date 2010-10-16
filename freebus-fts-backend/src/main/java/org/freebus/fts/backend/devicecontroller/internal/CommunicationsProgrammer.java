package org.freebus.fts.backend.devicecontroller.internal;

import org.freebus.fts.backend.DeviceController;
import org.freebus.fts.backend.devicecontroller.DeviceProgrammer;
import org.freebus.knxcomm.BusInterface;

/**
 * A programmer that programs the communications tables of a device.
 */
public class CommunicationsProgrammer implements DeviceProgrammer
{
   private final DeviceController controller;

   /**
    * Create a communications programmer.
    *
    * @param controller - the device controller to use.
    */
   public CommunicationsProgrammer(DeviceController controller)
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
