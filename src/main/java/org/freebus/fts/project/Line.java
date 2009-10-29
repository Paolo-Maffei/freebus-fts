package org.freebus.fts.project;

import org.freebus.fts.eib.PhysicalAddress;
import org.freebus.fts.project.generic.Node;


/**
 * An area in an {@link Area}. A line is the mid-level
 * group for the topological structure of an EIB bus.
 * Every line belongs to an {@link Area}, and can contain
 * {@link Devices}.
 */
public class Line extends Node<Device>
{
   /**
    * Create a new line with the given name.
    */
   public Line(String name)
   {
      super(name, 0);
   }

   /**
    * Create a new line with the given name and identifier.
    */
   public Line(String name, int id)
   {
      super(name, id);
   }

   /**
    * Reassign the physical addresses of the devices on the line.
    */
   public void updateIds(int areaId)
   {
      final int lineId = getId();
      for (int i=children.size()-1; i>=0; --i)
      {
         final Device dev = children.get(i);
         dev.setPhysicalAddr(new PhysicalAddress(areaId, lineId, i+1));
      }
   }

   /**
    * Create a new {@link Device} with the given name.
    * The device is appended to the end of the line's list of devices.
    */
   public Device createDevice(String name)
   {
      final Device device = new Device(name);
      add(device);
      return device;
   }
}
