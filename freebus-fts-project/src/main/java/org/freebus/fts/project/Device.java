package org.freebus.fts.project;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.freebus.knxcomm.telegram.PhysicalAddress;

/**
 * A KNX/EIB bus device. This is a device that is part of a project, that
 * gets installed somewhere in a house, and gets programmed.
 */
@Entity
@Table(name = "device")
public final class Device
{
   @Id
   @Column(name = "device_id", columnDefinition = "INT", nullable = false)
   private int id;

   @Id
   @Column(name = "line_id", columnDefinition = "INT", nullable = false)
   private int lineId;

   @Column(name = "device_address", columnDefinition = "INT", nullable = false)
   private int address;

   @Column(name = "virtual_device_id", columnDefinition = "INT", nullable = false)
   private int virtualDeviceId;

   @ManyToOne(cascade=CascadeType.ALL)
   private Line line;

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
    * @param lineId the lineId to set
    */
   public void setLineId(int lineId)
   {
      this.lineId = lineId;
   }

   /**
    * @return the lineId
    */
   public int getLineId()
   {
      return lineId;
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
   public PhysicalAddress getPhysicalAddr()
   {
      if (line == null) return PhysicalAddress.NULL;
      final Area area = line.getArea();
      if (area == null) return PhysicalAddress.NULL;
      return new PhysicalAddress(area.getAddress(), line.getAddress(), address);
   }

   /**
    * Set the id of the {@link VirtualDevice} virtual device.
    * 
    * @param virtualDeviceId - the id of the virtual device.
    */
   public void setVirtualDeviceId(int virtualDeviceId)
   {
      this.virtualDeviceId = virtualDeviceId;
   }

   /**
    * @return the id of the virtual device.
    */
   public int getVirtualDeviceId()
   {
      return virtualDeviceId;
   }
}
