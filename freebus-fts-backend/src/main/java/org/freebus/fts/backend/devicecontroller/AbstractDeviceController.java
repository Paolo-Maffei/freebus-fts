package org.freebus.fts.backend.devicecontroller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.freebus.fts.backend.DeviceController;
import org.freebus.fts.backend.memory.AssociationTableEntry;
import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceObject;
import org.freebus.fts.project.DeviceProgramming;
import org.freebus.fts.project.SubGroupToObject;

/**
 * Base class for device adapters that handles the creation of the various
 * device tables.
 */
public abstract class AbstractDeviceController implements DeviceController
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
   public AbstractDeviceController(Device device)
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
      associationTable = null;
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

         od.setPriority(devObject.getPriority());
         od.setType(devObject.getType());

         od.setCommEnabled(devObject.isCommEnabled());
         od.setReadEnabled(devObject.isReadEnabled());
         od.setWriteEnabled(devObject.isWriteEnabled());
         od.setTransEnabled(devObject.isTransEnabled());

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
      final Vector<AssociationTableEntry> assocTab = new Vector<AssociationTableEntry>(100);

      for (final DeviceObject devObject: device.getDeviceObjects())
      {
         final int comObjNumber = devObject.getComObject().getNumber(); 

         for (final SubGroupToObject sgo : devObject.getSubGroupToObjects())
         {
            final int groupAddrIndex = getGroupAddrIndex(sgo.getSubGroup().getGroupAddress());
            assocTab.add(new AssociationTableEntry(groupAddrIndex, comObjNumber));
         }
      }

      associationTable = new AssociationTableEntry[assocTab.size()];
      assocTab.toArray(associationTable);

      Arrays.sort(associationTable, new Comparator<AssociationTableEntry>()
      {
         @Override
         public int compare(AssociationTableEntry a, AssociationTableEntry b)
         {
            final int diff = a.getConnectionIndex() - b.getConnectionIndex();
            if (diff != 0)
               return diff;

            return a.getDeviceObjectIndex() - b.getDeviceObjectIndex();
         }
      });
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

   /**
    * {@inheritDoc}
    */
   @Override
   public List<DeviceProgrammer> getRequiredProgrammers()
   {
      final List<DeviceProgrammer> programmers = new Vector<DeviceProgrammer>();
      final DeviceProgramming progr = device.getProgramming();

      if (!progr.isPhysicalAddressValid())
         programmers.add(getProgrammer(DeviceProgrammerType.PHYSICAL_ADDRESS));
      if (!progr.isProgramValid())
         programmers.add(getProgrammer(DeviceProgrammerType.PROGRAM));
      if (!progr.isParametersValid())
         programmers.add(getProgrammer(DeviceProgrammerType.PARAMETERS));
      if (!progr.isCommunicationValid())
         programmers.add(getProgrammer(DeviceProgrammerType.COMMUNICATIONS));

      return programmers;
   }
}
