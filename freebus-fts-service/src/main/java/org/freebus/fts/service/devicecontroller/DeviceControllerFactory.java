package org.freebus.fts.service.devicecontroller;

import org.freebus.fts.project.Device;
import org.freebus.fts.service.devicecontroller.internal.Bcu1DeviceController;

/**
 * A factory class for {@link DeviceController device controllers}.
 */
public final class DeviceControllerFactory
{
   /**
    * Obtain a device controller for a specific device.
    * 
    * @param device - The device to get a controller for.
    * @return The controller for the device.
    */
   public static DeviceController getDeviceController(Device device)
   {
//      final BcuType bcuType = device.getCatalogEntry().getProduct().getBcuType();

      return new Bcu1DeviceController(device);
   }

   /*
    * Disabled
    */
   private DeviceControllerFactory()
   {
   }
}
