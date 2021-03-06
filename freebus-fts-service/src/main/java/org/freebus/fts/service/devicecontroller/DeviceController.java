package org.freebus.fts.service.devicecontroller;

import java.util.List;

import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.project.Device;
import org.freebus.fts.service.exception.DeviceControllerException;
import org.freebus.fts.service.job.device.DeviceProgrammerJob;
import org.freebus.fts.service.memory.MemoryRange;

/**
 * Interface for controllers that work with specific (hardware) devices on the
 * KNX bus.
 * <p>
 * The controller is used for programming the device (application program,
 * parameters, communication objects, etc), for querying details of the device,
 * and for other similar tasks.
 */
public interface DeviceController
{
   /**
    * Get the device that the controller handles.
    * 
    * @return The device.
    */
   Device getDevice();

   /**
    * Test if the hardware device is compatible to the {@link Device device}.
    * Programming must not be done if the hardware is not compatible.
    * 
    * @return True if the hardware is compatible
    */
   boolean isCompatible();

   /**
    * Set the compatibility flag.
    * 
    * @see #isCompatible()
    */
   void setCompatible(boolean f);

   /**
    * Update the calculated data. Call when the device was changed.
    */
   void deviceChanged();

   /**
    * Get the group addresses that the device knows.
    * 
    * @return The list of group addresses, in the order as they appear in the
    *         device.
    */
   GroupAddress[] getGroupAddresses();

   /**
    * Get the list of object descriptors of the device.
    * 
    * @return The list of object descriptors.
    */
   ObjectDescriptor[] getObjectDescriptors();

   /**
    * Get the association table.
    * 
    * @return The association table.
    */
   AssociationTableEntry[] getAssociationTable();

   /**
    * Get the device's memory.
    * 
    * @return The memory of the device.
    */
   MemoryRange getDeviceMemory();
   
   /**
    * Get all programmer jobs that are required to fully update the device.
    * @throws DeviceControllerException
    */
   List<DeviceProgrammerJob> getRequiredProgrammerJobs() throws DeviceControllerException;

   /**
    * Get a device programmer job for programming an aspect of the device.
    * 
    * @param type - the type of the requested device programmer.
    * @throws DeviceControllerException
    */
   DeviceProgrammerJob getProgrammerJob(DeviceProgrammerType type) throws DeviceControllerException;
}
