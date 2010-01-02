package org.freebus.fts.project;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * An area in an {@link Area}. A line is the mid-level
 * group for the topological structure of an EIB bus.
 * Every line belongs to an {@link Area}, and can contain
 * {@link Devices}.
 */
@Entity
@Table(name = "line")
public class Line
{
   @Id
   @Column(name = "line_id", nullable = false)
   private int id;

   @Column(name = "line_name")
   private String name = "";

   @Column(name = "line_address")
   private int address;

   @ManyToOne(cascade=CascadeType.ALL)
   private Area area;

   @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
   private Set<Device> devices = new HashSet<Device>();
   
   /**
    * Create a new line.
    */
   public Line()
   {
   }

   /**
    * @return the line id
    */
   public int getId()
   {
      return id;
   }

   /**
    * @param id the line id to set
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the address
    */
   public int getAddress()
   {
      return address;
   }

   /**
    * @param address the address to set
    */
   public void setAddress(int address)
   {
      this.address = address;
   }

   /**
    * @return the area
    */
   public Area getArea()
   {
      return area;
   }

   /**
    * @param area the area to set
    */
   public void setArea(Area area)
   {
      this.area = area;
   }

   /**
    * Add a device to the line.
    */
   public void add(Device device)
   {
      device.setLine(this);
      devices.add(device);
   }

   /**
    * @return the devices
    */
   public Set<Device> getDevices()
   {
      return devices;
   }

   /**
    * @param devices the devices to set
    */
   public void setDevices(Set<Device> devices)
   {
      this.devices = devices;
   }

   /**
    * @return a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      String areaAddr;
      if (area == null) areaAddr = "?";
      else areaAddr = Integer.toString(area.getAddress());

      return String.format("%s.%d. %s", areaAddr, getAddress(), getName());
   }
}
