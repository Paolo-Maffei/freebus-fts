package org.freebus.fts.service.devicecontroller.internal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceObject;
import org.freebus.fts.project.DeviceParameter;
import org.freebus.fts.project.SubGroupToObject;
import org.freebus.fts.service.devicecontroller.AssociationTableEntry;
import org.freebus.fts.service.devicecontroller.DeviceController;
import org.freebus.fts.service.memory.MemoryRange;

/**
 * A device controller with methods for most BCU types.
 */
public abstract class GenericDeviceController implements DeviceController
{
   private final Device device;
   private MemoryRange memory = null;

   private ObjectDescriptor[] objectDescriptors;
   private GroupAddress[] groupAddresses;
   private AssociationTableEntry[] associationTable;
   private boolean deviceCompatible;

   /**
    * Create a basic device controller.
    * 
    * @param device - the device to handle.
    */
   public GenericDeviceController(Device device)
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

   @Override
   public MemoryRange getDeviceMemory()
   {
      if (memory == null)
         updateMemory();

      return memory;
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
    * Update the device's memory.
    */
   private void updateMemory()
   {
      final Program prog = device.getProgram();
//      final Mask mask = prog.getMask();

      final MemoryRange mem = new MemoryRange(0, prog.getEepromData());

      for (final Parameter param : device.getProgram().getParameters())
      {
         final DeviceParameter devParam = device.getDeviceParameter(param);
         if (!devParam.isUsed())
            continue;

         final Integer addr = param.getAddress();
         if (addr == null || addr == 0)
            continue;

         final int bits = param.getSize();
         final int bitOffset = param.getBitOffset();

         int bitMask = (1 << bits) - 1;
         bitMask <<= bitOffset;
         
         int oldValue = mem.getValue(addr) & 255;

         final int paramValue = devParam.getIntValue() & 255;
         final int newValue = (oldValue & ~bitMask) | ((paramValue << bitOffset) & bitMask);

         mem.setValue(addr, (byte) newValue);
      }

      memory = mem;
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
    * @return The highest communication object number that the device has plus 1.
    *         Returns 0 if the device has no {@link Program application program}.
    */
   private int countComObjects()
   {
      final Program prog = device.getProgram();
      int maxNum = -1;

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
   public boolean isCompatible()
   {
      return deviceCompatible;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setCompatible(boolean deviceCompatible)
   {
      this.deviceCompatible = deviceCompatible;
   }
}
