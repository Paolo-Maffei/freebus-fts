package org.freebus.fts.project;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * A line in an {@link Area}. A line is the mid-level group for the topological
 * structure of an EIB bus. Every line belongs to an {@link Area area}, and can
 * contain {@link Device devices}.
 */
@Entity
@Table(name = "line")
public class Line implements Comparable<Line>
{
   @Id
   @TableGenerator(name = "Line", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "Line")
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
   private Set<Device> devices = new TreeSet<Device>();

   @Deprecated
   public final static int MAX_ADDR = 0x0F; // The highest number valid for
                                            // address

   @Deprecated
   public final static int MIN_NAME_LENGTH = 2; // minimum accepted length for a
                                                // name

   /**
    * Create a new line.
    */
   public Line()
   {
   }

   /**
    * Remove the line from its area.
    */
   public void detach()
   {
      if (area != null)
         area.remove(this);
   }

   /**
    * @return the line id
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the line id.
    * 
    * @param id - the line id to set
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
    * Set the name of the line.
    * 
    * @param name - the name to set
    */
   public void setName(String name)
   {
      this.name = name == null ? "" : name;
   }

   /**
    * @return the KNX bus address of the line (0..255).
    */
   public int getAddress()
   {
      return address;
   }

   /**
    * Set the KNX bus address of the line.
    * 
    * @param address - the address to set (0..255)
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
    * Set the area to which the line belongs. Use {@link Area#add(Line)} instead
    * of this method if you want to add a line to an {@link Area area}.
    * 
    * @param area - the area to set
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

      final Line prev = device.getLine();
      if (prev != null)
         prev.remove(device);

      device.setLine(this);
      devices.add(device);
   }

   /**
    * Delete the device from the line.
    *
    * @param device - the device to remove.
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
   public Set<Device> getDevices()
   {
      return devices;
   }

   /**
    * @param devices the devices to set
    */
   void setDevices(TreeSet<Device> devices)
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
    * Compare by address and id.
    */
   @Override
   public int compareTo(Line o)
   {
      if (o == null)
         return 1;

      final int d = address - o.address;
      if (d != 0)
         return d;

      return id - o.id;
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
    * 
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
