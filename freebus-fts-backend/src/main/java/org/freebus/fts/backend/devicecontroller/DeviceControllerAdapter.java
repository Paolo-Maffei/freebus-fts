package org.freebus.fts.backend.devicecontroller;

import java.util.List;

import org.freebus.fts.backend.DeviceController;
import org.freebus.fts.backend.memory.AssociationTableEntry;
import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.project.Device;

/**
 * Adapter for {@link DeviceController device controllers}.
 */
public class DeviceControllerAdapter implements DeviceController
{
   @Override
   public Device getDevice()
   {
      return null;
   }

   @Override
   public void deviceChanged()
   {
   }

   @Override
   public GroupAddress[] getGroupAddresses()
   {
      return null;
   }

   @Override
   public ObjectDescriptor[] getObjectDescriptors()
   {
      return null;
   }

   @Override
   public AssociationTableEntry[] getAssociationTable()
   {
      return null;
   }

   @Override
   public DeviceProgrammer getProgrammer(DeviceProgrammerType type)
   {
      return null;
   }

   @Override
   public List<DeviceProgrammer> getRequiredProgrammers()
   {
      return null;
   }
}
