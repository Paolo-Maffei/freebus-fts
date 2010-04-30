package org.freebus.fts.project;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * A line in an {@link Area}. A line is the mid-level group for the topological
 * structure of an EIB bus. Every line belongs to an {@link Area area}, and can
 * contain {@link Device devices}.
 */
@Entity
@Table(name = "line")
public class Line
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequence", name = "GenLineId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "line_id", nullable = false)
   private int id;

   @Column(name = "line_name")
   private String name = "";

   @Column(name = "line_address")
   private int address;

   @ManyToOne(optional = false)
   @JoinColumn(name = "area_id")
   private Area area;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "line")
   @OrderBy("address")
   private List<Device> devices = new Vector<Device>();

   public final static int MAX_ADDR = 0x0F;        // The highest number valid for address 
   public final static int MIN_NAME_LENGTH = 2;    //TODO Define minimum accepted length for an Area Name

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
      this.name = name == null ? "" : name;
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
    *
    * @throws IllegalArgumentException - If the device was already added to the
    *            room.
    *
    * @see #getFreeAddress()
    */
   public void add(Device device)
   {
      if (devices.contains(device))
         throw new IllegalArgumentException("Device was previously added: " + device);

      device.setLine(this);
      devices.add(device);
   }

   /**
    * Delete the Device from the line
    * @param device to remove
    */
   public void remove(Device device)
   {
      if (!devices.contains(device))
         throw new IllegalArgumentException("Device is not part of this line: " + this + ", Device: " + device);
      
      device.setLine(null);
      devices.remove(device);
   }
   
   /**
    * @return an address that is not used in the line.
    *
    * @throws RuntimeException if no free address can be found.
    */
   public int getFreeAddress()
   {
      final Set<Integer> usedAddr = new HashSet<Integer>(256);

      for (Device device : devices)
         usedAddr.add(device.getAddress());

      int addr;
      for (addr = 0; addr <= 255; ++addr)
         if (!usedAddr.contains(addr))
            break;

      if (addr > 255)
         throw new RuntimeException("Line is full, no free address found");

      return addr;
   }

   /**
    * @return the devices
    */
   public List<Device> getDevices()
   {
      return devices;
   }

   /**
    * @param devices the devices to set
    */
   public void setDevices(List<Device> devices)
   {
      this.devices = devices;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof Line))
         return false;

      final Line oo = (Line) o;
      return (id == oo.id && address == oo.address && name.equals(oo.name) && devices.equals(oo.devices));
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
      String areaAddr;
      if (area == null)
         areaAddr = "?";
      else areaAddr = Integer.toString(area.getAddress());

      return String.format("%s.%d. %s", areaAddr, getAddress(), getName());
   }

   /**
    * request number of all used addresses of the physical addresses by Lines
    * this could be used to find out free addresses
    * @return array with all used addresses
    */
   public int[] getUsedDeviceAddresses()
   {
      int used[] = new int[devices.size()];
      int cnt = 0;
      for (Device device : getDevices())
      {
         used[cnt] = device.getAddress();
         cnt++;
      }
      
      Arrays.sort(used);
      
      return used;
   }
}
