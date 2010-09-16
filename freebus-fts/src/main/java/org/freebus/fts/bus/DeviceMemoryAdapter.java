package org.freebus.fts.bus;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.types.ObjectType;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.Mask;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceObject;
import org.freebus.fts.project.SubGroupToObject;

/**
 * An adapter for a {@link Device} that creates the EEPROM memory tables and
 * assigns RAM addresses for a specific device.
 */
public final class DeviceMemoryAdapter
{
   private final Vector<ObjectDescriptor> objectDescriptors = new Vector<ObjectDescriptor>(64);
   private final Vector<GroupAddress> groupAddresses = new Vector<GroupAddress>();
   private final Vector<AssociationTableEntry> associationTable = new Vector<AssociationTableEntry>();

   private Device device;

   private int ramFlagTablePtr;

   // User RAM: start, end, and next free address
   private int userRamStart, userRamEnd, userRamAddr;

   // User EEPROM: start, end, and next free address
   private int userEepromStart, userEepromEnd, userEepromAddr;

   /**
    * Create an empty device-memory adapter.
    */
   public DeviceMemoryAdapter()
   {
   }

   /**
    * Create a device-memory adapter.
    * 
    * @param device - the device to process.
    */
   public DeviceMemoryAdapter(Device device)
   {
      setDevice(device);
   }

   /**
    * @return The device that is processed.
    */
   public Device getDevice()
   {
      return device;
   }

   /**
    * Set the device to process. Calls {@link #update()}.
    * 
    * @param device - the device to set.
    */
   public void setDevice(Device device)
   {
      this.device = device;
      update();
   }

   /**
    * Get all group addresses that the device uses. The
    * returned list is sorted by group address.
    * 
    * @return A sorted collection of group addresses.
    */
   public List<GroupAddress> getGroupAddresses()
   {
      return groupAddresses;
   }

   /**
    * @return The list of object descriptors for the device.
    */
   public List<ObjectDescriptor> getObjectDescriptors()
   {
      return objectDescriptors;
   }

   /**
    * Returns the pointer to the ram-flag table. The ram-flag table allocates
    * one byte per 2 device objects.
    * 
    * @return The pointer to the ram-flag table.
    */
   public int getRamFlagTablePtr()
   {
      return ramFlagTablePtr;
   }

   /**
    * Update the object's data. Call when the {@link #getDevice() device} was
    * changed.
    */
   public void update()
   {
      objectDescriptors.clear();
      groupAddresses.clear();
      associationTable.clear();

      ramFlagTablePtr = 0;

      if (device == null)
         return;

      resetDeviceDetails();
      updateGroupAddresses();
      updateObjectDescriptors();
      updateAssociationTable();
   }

   /**
    * Get the next free user RAM address for count bytes.
    * 
    * @param count - the number of bytes that shall be allocated.
    * @return The start address of the allocated memory. Zero is returned if
    *         count is zero.
    */
   private int allocateRam(int count)
   {
      if (count < 0)
         throw new IllegalArgumentException("count must not be negative");

      if (count == 0)
         return 0;

      final int addr = userRamAddr;
      userRamAddr += count;

      if (userRamAddr > userRamEnd)
         throw new OutOfMemoryError("device's user RAM is exhausted");

      return addr;
   }

   /**
    * Get the next free user EEPROM address for count bytes.
    * 
    * @param count - the number of bytes that shall be allocated.
    * @return The start address of the allocated memory.
    */
   private int allocateEeprom(int count)
   {
      if (count < 1)
         throw new IllegalArgumentException("count must be >= 1");

      final int addr = userEepromAddr;
      userEepromAddr += count;

      if (userEepromAddr > userEepromEnd)
         throw new OutOfMemoryError("device's user EEPROM is exhausted");

      return addr;
   }

   /**
    * Reset the details of the device that are required to work properly.
    */
   private void resetDeviceDetails()
   {
      final Program program = device.getProgram();
      final Mask mask = program.getMask();

      userEepromStart = mask.getUserEepromStart();
      userEepromEnd = mask.getUserEepromEnd();
      userEepromAddr = userEepromStart;

      userRamStart = mask.getUserRamStart();
      userRamEnd = mask.getUserRamEnd();
      userRamAddr = userRamStart;
   }

   /**
    * Update all group addresses that the device uses.
    * 
    * @return A sorted collection of group addresses.
    */
   private void updateGroupAddresses()
   {
      final Set<GroupAddress> groupAddressSet = new TreeSet<GroupAddress>();

      for (final DeviceObject devObject: device.getVisibleDeviceObjects())
      {
         for (final SubGroupToObject sgo : devObject.getSubGroupToObjects())
            groupAddressSet.add(sgo.getSubGroup().getGroupAddress());
      }

      groupAddresses.clear();
      groupAddresses.addAll(groupAddressSet);
   }

   /**
    * Update the list of object descriptors of the device.
    */
   private void updateObjectDescriptors()
   {
      objectDescriptors.clear();

      final List<DeviceObject> devObjects = device.getDeviceObjects();
      ramFlagTablePtr = allocateRam((devObjects.size() + 1) >> 1);

      for (DeviceObject devObject : devObjects)
      {
         final CommunicationObject comObject = devObject.getComObject();
         final ObjectDescriptor od = new ObjectDescriptor();
         od.setPriority(comObject.getObjectPriority());

         ObjectType type = devObject.getObjectType();
         if (type == null)
            type = comObject.getObjectType();
         od.setType(type);

         od.setCommEnabled(devObject.isComm());
         od.setReadEnabled(devObject.isRead());
         od.setWriteEnabled(devObject.isWrite());
         od.setTransEnabled(devObject.isTrans());

         // TODO add handling for read-only device objects
         od.setDataPointer(allocateRam(1), false);

         objectDescriptors.add(od);
      }
   }

   /**
    * Update the connection-to-device-objects association table.
    */
   private void updateAssociationTable()
   {
      final List<DeviceObject> devObjects = device.getDeviceObjects();

      associationTable.clear();
      associationTable.setSize(devObjects.size());

      for (DeviceObject devObject : devObjects)
      {
         // TODO
      }
   }
}
