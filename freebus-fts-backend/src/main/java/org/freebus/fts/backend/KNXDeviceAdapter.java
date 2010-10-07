package org.freebus.fts.backend;

import org.freebus.fts.backend.memory.AssociationTableEntry;
import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.project.Device;

/**
 * Interface for adapters that work with specific (hardware) devices on the KNX
 * bus.
 */
public interface KNXDeviceAdapter
{
   /**
    * Get the device that the adapter handles.
    * 
    * @return The device.
    */
   Device getDevice();

   /**
    * Update the calculated data. Call when the device
    * was changed.
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
}
