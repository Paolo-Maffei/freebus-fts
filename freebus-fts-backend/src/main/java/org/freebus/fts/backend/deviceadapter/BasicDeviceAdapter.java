package org.freebus.fts.backend.deviceadapter;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.freebus.fts.backend.KNXDeviceAdapter;
import org.freebus.fts.backend.memory.AssociationTableEntry;
import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.types.ObjectType;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceObject;
import org.freebus.fts.project.SubGroupToObject;

/**
 * Base class for device adapters that handles the creation of the various
 * device tables.
 */
public abstract class BasicDeviceAdapter implements KNXDeviceAdapter
{
   private final Device device;

   private ObjectDescriptor[] objectDescriptors;
   private GroupAddress[] groupAddresses;
   private AssociationTableEntry[] associationTable;

   /**
    * Create a basic device adapter.
    * 
    * @param device - the device to handle.
    */
   public BasicDeviceAdapter(Device device)
   {
      this.device = device;
   }

   /**
    * @return The device.
    */
   @Override
   public final Device getDevice()
   {
      return device;
   }

   @Override
   public void deviceChanged()
   {
      groupAddresses = null;
      objectDescriptors = null;

   }

   @Override
   public GroupAddress[] getGroupAddresses()
   {
      if (groupAddresses == null)
         updateGroupAddresses();

      return groupAddresses;
   }

   @Override
   public ObjectDescriptor[] getObjectDescriptors()
   {
      if (objectDescriptors == null)
         updateObjectDescriptors();

      return objectDescriptors;
   }

   @Override
   public AssociationTableEntry[] getAssociationTable()
   {
      if (associationTable == null)
         updateAssociationTable();

      return associationTable;
   }

   /**
    * Update the list of the device's object descriptors.
    */
   private synchronized void updateObjectDescriptors()
   {
      final List<DeviceObject> devObjects = device.getDeviceObjects();
      final int numComObjects = countComObjects();

      objectDescriptors = new ObjectDescriptor[numComObjects];

      // Create the object descriptors for the used device objects
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

         objectDescriptors[comObject.getNumber()] = od;
      }

      // Create default object descriptors for the unused device object numbers
      for (int i = 0; i < numComObjects; ++i)
      {
         if (objectDescriptors[i] == null)
            objectDescriptors[i] = new ObjectDescriptor();
      }
   }

   /**
    * Update the list of the device's group addresses.
    */
   private synchronized void updateGroupAddresses()
   {
      final Set<GroupAddress> groupAdds = new TreeSet<GroupAddress>();

      for (final DeviceObject devObject : device.getDeviceObjects())
      {
         for (final SubGroupToObject sgo : devObject.getSubGroupToObjects())
            groupAdds.add(sgo.getSubGroup().getGroupAddress());
      }

      groupAddresses = new GroupAddress[groupAdds.size()];
      groupAdds.toArray(groupAddresses);
   }

   /**
    * Update the association table.
    */
   private synchronized void updateAssociationTable()
   {
      final Vector<AssociationTableEntry> assocTab = new Vector<AssociationTableEntry>(64);

      // Transmitting device objects get an association table entry
      // at the index of their device object number. This is required
      // for certain BCU firmware implementations.
      for (final DeviceObject devObject: device.getDeviceObjects())
      {
         if (!devObject.isTrans())
            continue;

         final List<SubGroupToObject> sgos = devObject.getSubGroupToObjects();
         if (sgos.isEmpty())
            continue;

         final int groupAddrIndex = getGroupAddrIndex(sgos.get(0).getSubGroup().getGroupAddress());

         final int number = devObject.getComObject().getNumber(); 
         if (assocTab.size() <= number)
            assocTab.setSize(number + 1);

         if (assocTab.get(number) != null)
            throw new RuntimeException("internal error: association table position is used twice");

         assocTab.set(number, new AssociationTableEntry(groupAddrIndex, number));
      }

      // Add all non-transmitting device objects, and the group addresses
      // of transmitting device objects after the first group address,
      // into the free positions and at the end of the association table.
      for (final DeviceObject devObject: device.getDeviceObjects())
      {
         final List<SubGroupToObject> sgos = devObject.getSubGroupToObjects();
         if (sgos.isEmpty())
            continue;

         boolean skipFirst = devObject.isTrans();
         for (final SubGroupToObject sgo : devObject.getSubGroupToObjects())
         {
            if (skipFirst)
            {
               skipFirst = false;
               continue;
            }

            final int groupAddrIndex = getGroupAddrIndex(sgo.getSubGroup().getGroupAddress());
            final int number = devObject.getComObject().getNumber();

            int i = 0;
            while (i < assocTab.size() && assocTab.get(i) != null)
               ++i;

            if (assocTab.size() <= i)
               assocTab.setSize(i + 1);

            assocTab.set(number, new AssociationTableEntry(groupAddrIndex, number));
         }
      }

      associationTable = new AssociationTableEntry[assocTab.size()];
      assocTab.toArray(associationTable);
   }

   /**
    * Lookup the group address in the group address table.
    * 
    * @param groupAddr - the group address to find.
    * @return The index of the group address in the group address table.
    *
    * @throw IllegalArgumentException if the group address was not found in the
    *        group address table.
    */
   private int getGroupAddrIndex(GroupAddress groupAddr)
   {
      final GroupAddress[] groupAdds = getGroupAddresses();
      for (int i = groupAdds.length - 1; i >= 0; --i)
      {
         if (groupAdds[i].equals(groupAddr))
            return i;
      }

      throw new IllegalArgumentException("group address not found in group address table: " + groupAddr);
   }

   /**
    * @return The number of communication objects that the device has. Returns 0
    *         if the device has no {@link Program application program}.
    */
   private int countComObjects()
   {
      final Program prog = device.getProgram();
      int maxNum = 0;

      if (prog == null)
         return 0;

      for (final CommunicationObject comObject : prog.getCommunicationObjects())
      {
         final int num = comObject.getNumber();
         if (num > maxNum)
            maxNum = num;
      }

      return maxNum + 1;
   }

}
