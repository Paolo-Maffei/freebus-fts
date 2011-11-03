package org.freebus.fts.service.job.device;

import org.freebus.fts.service.job.JobListener;
import org.freebus.fts.service.job.entity.DeviceInfo;

/**
 * Interface for listeners that get informed about the progress of a
 * {@link DeviceScannerJob device scanner job}.
 */
public interface DeviceScannerJobListener extends JobListener
{
   /**
    * Called with new information about a device. May be a new device, or the
    * update of an already found device.
    * 
    * @param info - the device information.
    */
   void deviceInfo(DeviceInfo info);
}
