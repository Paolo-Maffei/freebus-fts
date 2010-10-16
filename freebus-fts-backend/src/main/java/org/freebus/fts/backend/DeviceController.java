package org.freebus.fts.backend;

import java.util.List;

import org.freebus.fts.backend.devicecontroller.DeviceProgrammer;
import org.freebus.fts.backend.devicecontroller.DeviceProgrammerType;
import org.freebus.fts.backend.memory.AssociationTableEntry;
import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.project.Device;

/**
 * Interface for controllers that work with specific (hardware) devices on the
 * KNX bus.
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
    * Get a device programmer for programming an aspect of the device.
    * 
    * @param type - the type of the requested device programmer.
    */
   DeviceProgrammer getProgrammer(DeviceProgrammerType type);

   /**
    * Get all programmers that are required to fully update the device.
    */
   List<DeviceProgrammer> getRequiredProgrammers();
}
