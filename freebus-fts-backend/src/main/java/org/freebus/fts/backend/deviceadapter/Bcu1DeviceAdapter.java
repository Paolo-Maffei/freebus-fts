package org.freebus.fts.backend.deviceadapter;

import org.freebus.fts.project.Device;

/**
 * A device adapter for devices that are handled with the BCU-1 mechanisms.
 */
public final class Bcu1DeviceAdapter extends BasicDeviceAdapter
{
   /**
    * Create a device adapter for a BCU-1 device.
    * 
    * @param device - the device to handle.
    */
   public Bcu1DeviceAdapter(Device device)
   {
      super(device);
   }
}
