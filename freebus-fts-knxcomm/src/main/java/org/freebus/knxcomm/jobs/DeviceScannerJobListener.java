package org.freebus.knxcomm.jobs;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor;

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
   void deviceFound(PhysicalAddress addr);

   /**
    * The device descriptor of a device was received.
    * 
    * @param descriptor - the device descriptor.
    */
   void deviceDescriptorReceived(DeviceDescriptor descriptor);
}
