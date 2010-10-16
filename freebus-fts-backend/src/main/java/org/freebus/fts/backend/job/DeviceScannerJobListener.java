package org.freebus.fts.backend.job;

import org.freebus.fts.backend.job.entity.DeviceInfo;

/**
 * Interface for listeners that get informed about the progress of a
 * {@link DeviceScannerJob device scanner job}.
 */
public interface DeviceScannerJobListener extends JobListener
{
   /**
    * Information about a device. May be a new device, or
    * the update to an already found device.
    *
    * @param info - the device information.
    */
   void deviceInfo(DeviceInfo info);
}
