package org.freebus.fts.backend;

import org.freebus.fts.products.BcuType;
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
      final BcuType bcuType = device.getCatalogEntry().getProduct().getBcuType();

      return null;
   }

   /*
    * Disabled
    */
   private KNXDeviceAdapterFactory()
   {
   }
}
