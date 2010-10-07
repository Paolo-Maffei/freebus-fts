package org.freebus.fts.backend;

import org.freebus.fts.backend.deviceadapter.Bcu1DeviceAdapter;
import org.freebus.fts.project.Device;

/**
 * A factory class for {@link KNXDeviceAdapter device adapters}.
 */
public final class KNXDeviceAdapterFactory
{
   /**
    * Obtain a device adapter for a specific device.
    * 
    * @param device - The device to get an adapter for.
    * @return The device adapter for the device.
    */
   public static KNXDeviceAdapter getDeviceAdapter(Device device)
   {
//      final BcuType bcuType = device.getCatalogEntry().getProduct().getBcuType();

      return new Bcu1DeviceAdapter(device);
   }

   /*
    * Disabled
    */
   private KNXDeviceAdapterFactory()
   {
   }
}
