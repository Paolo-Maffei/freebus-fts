package org.freebus.fts.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.products.VirtualDevice;
import org.freebus.knxcomm.telegram.PhysicalAddress;

/**
 * A KNX/EIB bus device. This is a device that is part of a project, that gets
 * installed somewhere in a house, and gets programmed.
 */
@Entity
@Table(name = "device")
public final class Device
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "GenDeviceId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "device_id", nullable = false)
   private int id;

   @Column(name = "device_address", nullable = false)
   private int address;

   @ManyToOne(optional = true)
   @JoinColumn(name = "virtual_device_id")
   private VirtualDevice virtualDevice;

   @ManyToOne(optional = false)
   @JoinColumn(name = "line_id")
   private Line line;

   @ManyToOne(optional = true)
   @JoinColumn(name = "room_id")
   private Room room;

   /**
    * Create an empty device object.
    */
   public Device()
   {
   }

   /**
    * Create a device object from a virtual device.
    */
   public Device(VirtualDevice virtualDevice)
   {
      this.virtualDevice = virtualDevice;
   }

   /**
    * Create a device object.
    */
   public Device(int id, VirtualDevice virtualDevice)
   {
      this.id = id;
      this.virtualDevice = virtualDevice;
   }

   /**
    * @param id the id to set
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the id of the device.
    */
   public int getId()
   {
      return id;
   }

   /**
    * @param address the address to set
    */
   public void setAddress(int address)
   {
      this.address = address;
   }

   /**
    * @return the address
    */
   public int getAddress()
   {
      return address;
   }

   /**
    * @return the physical address of the device.
    */
   public PhysicalAddress getPhysicalAddress()
   {
      if (line == null)
         return PhysicalAddress.NULL;
      final Area area = line.getArea();
      if (area == null)
         return PhysicalAddress.NULL;
      return new PhysicalAddress(area.getAddress(), line.getAddress(), address);
   }

   /**
    * Set the id of the {@link VirtualDevice} virtual device.
    * 
    * @param virtualDevice - the virtual device.
    */
   public void setVirtualDevice(VirtualDevice virtualDevice)
   {
      this.virtualDevice = virtualDevice;
   }

   /**
    * @return the virtual device.
    */
   public VirtualDevice getVirtualDevice()
   {
      return virtualDevice;
   }

   /**
    * @return the line to which the device belongs.
    */
   public Line getLine()
   {
      return line;
   }

   /**
    * Set the line to which the device belongs.
    */
   public void setLine(Line line)
   {
      this.line = line;
   }

   /**
    * Set the room in which the device is physically installed.
    */
   public void setRoom(Room room)
   {
      this.room = room;
   }

   /**
    * @return the room in which the device is physically installed.
    */
   public Room getRoom()
   {
      return room;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this) return true;
      if (!(o instanceof Device)) return false;
      final Device oo = (Device) o;
      return (id == oo.id && address == oo.address && virtualDevice.equals(oo.virtualDevice));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return id;
   }

   /**
    * @return a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(getPhysicalAddress().toString());
      sb.append(" ").append("Device ").append(virtualDevice);

      return sb.toString();
   }
}
