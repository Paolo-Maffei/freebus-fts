package org.freebus.fts.project;

import org.freebus.fts.eib.PhysicalAddress;
import org.freebus.fts.project.generic.Node;

/**
 * A middle-group in a {@link Project}. A middle-group is the mid-level
 * group for the logical structure of an EIB bus.
 */
public class MiddleGroup extends Node<Device>
{
   /**
    * Create a new middle-group with the given name.
    */
   public MiddleGroup(String name)
   {
      super(name, 0);
   }

   /**
    * Create a new middle-group with the given name and identifier.
    */
   public MiddleGroup(String name, int id)
   {
      super(name, id);
   }

   /**
    * Assign id's to all devices.
    * 
    * The id of the main-group, to which this middle-group belongs,
    * is given in parentId.
    */
   void renumber(int parentId)
   {
      for (int i=children.size()-1; i>=0; --i)
      {
         final Device dev = children.get(i);
         dev.setPhysicalAddr(new PhysicalAddress(parentId, getId(), i+1));
      }
   }

   /**
    * Create a new {@link Device} with the given name.
    * The subGroup is appended to the end of the list of devices.
    */
   public Device createDevice(String name)
   {
      final Device device = new Device(name);
      add(device);
      return device;
   }
}
