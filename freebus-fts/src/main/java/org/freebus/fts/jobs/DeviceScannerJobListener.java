package org.freebus.fts.jobs;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.jobs.JobListener;

/**
 * Interface for listeners that get informed about the progress of a
 * {@link DeviceScannerJob device scanner job}.
 */
public interface DeviceScannerJobListener extends JobListener
{
   /**
    * A device was found.
    *
    * @param addr - the address of the found device.
    */
   public void deviceFound(PhysicalAddress addr);
}
