package org.freebus.fts.backend.devicecontroller;

import org.freebus.fts.project.Device;

/**
 * A device adapter for devices that are handled with the BCU-1 mechanisms.
 */
public final class Bcu1DeviceController extends GenericDeviceController
{
   /**
    * Create a device adapter for a BCU-1 device.
    * 
    * @param device - the device to control.
    */
   public Bcu1DeviceController(Device device)
   {
      super(device);
   }
}
