package org.freebus.fts.project;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.interfaces.Named;

/**
 * A room in a {@link Building building}. A room is the mid-level group for the
 * physical structure of a {@link Project project}. Every room belongs to a
 * {@link Building building}, and can contain {@link Device devices}.
 */
@Entity
@Table(name = "room")
public class Room implements Comparable<Room>, Named
{
   @Id
   @TableGenerator(name = "Room", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "Room")
   @Column(name = "room_id", nullable = false)
   private int id;

   @Column(name = "room_name", nullable = false)
   private String name = "";

   @Column(name = "room_number", nullable = false)
   private String number = "";

   @Column(name = "description", nullable = false)
   private String description = "";

   @Column(name = "type")
   @Enumerated(EnumType.ORDINAL)
   private RoomType type = RoomType.ROOM;

   @ManyToOne(optional = false)
   @JoinColumn(name = "building_id")
   private Building building;

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "room")
   private Set<Device> devices = new TreeSet<Device>();

   /**
    * Create a new room.
    */
   public Room()
   {
   }

   /**
    * Remove the room from its building.
    */
   public void detach()
   {
      if (building != null)
         building.remove(this);
   }

   /**
    * @return the room id
    */
   public int getId()
   {
      return id;
   }

   /**
    * @param id the room id to set
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * Get the name of the room.
    *
    * @return the name
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * Get the full name of the room, including the name of the building
    * and building parts (if any).
    */
   public String getPathName()
   {
      return building == null ? name : building.getPathName() + " - " + name;
   }

   /**
    * Set the name of the room.
    *
    * @param name - the name to set
    */
   public void setName(String name)
   {
      this.name = name == null ? "" : name;
   }

   /**
    * @return The room number.
    */
   public String getNumber()
   {
      return number;
   }

   /**
    * Set the room number.
    */
   public void setNumber(String number)
   {
      this.number = number == null ? "" : number;
   }

   /**
    * @return The (optional) description of the room.
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * Set the (optional) description of the room.
    */
   public void setDescription(String description)
   {
      this.description = description == null ? "" : description;
   }

   public void setType(RoomType type)
   {
      this.type = type;
   }

   public RoomType getType()
   {
      return type;
   }

   /**
    * @return the building
    */
   public Building getBuilding()
   {
      return building;
   }

   /**
    * @param building the building to set
    */
   public void setBuilding(Building building)
   {
      this.building = building;
   }

   /**
    * Add a device to the room.
    *
    * @param device - the device to add.
    *
    * @throws IllegalArgumentException - If the device was already added to the
    *            room.
    */
   public void add(Device device)
   {
      if (devices.contains(device))
         throw new IllegalArgumentException("Device was previously added: " + device);

      final Room oldRoom = device.getRoom();
      if (oldRoom != null)
         oldRoom.remove(device);

      device.setRoom(this);
      devices.add(device);
   }

   /**
    * Remove a device from the room.
    *
    * @param device - the device to remove.
    */
   public void remove(Device device)
   {
      device.setRoom(null);
      devices.remove(device);
   }

   /**
    * @return the devices
    */
   public Set<Device> getDevices()
   {
      return devices;
   }

   /**
    * Set the devices.
    *
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

      if (!(o instanceof Room))
         return false;

      final Room oo = (Room) o;
      if (id != oo.id || !name.equals(oo.name) || !description.equals(oo.description))
         return false;

      if (!devices.equals(oo.devices))
         return false;

      return true;
   }

   /**
    * Compare by name and id.
    */
   @Override
   public int compareTo(Room o)
   {
      int d = name.compareTo(o.name);
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
      return name;
   }
}
