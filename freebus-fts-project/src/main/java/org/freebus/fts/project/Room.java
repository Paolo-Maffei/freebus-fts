package org.freebus.fts.project;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * A room in a {@link Building}. A room is the mid-level group for the physical
 * structure of a project. Every room belongs to a {@link Building}, and can
 * contain {@link Devices}.
 */
@Entity
@Table(name = "room")
public class Room
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "GenRoomId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "room_id", nullable = false)
   private int id;

   @Column(name = "room_name", nullable = false)
   private String name = "";

   @Column(name = "room_number")
   private String number = "";

   @Column(name = "description")
   private String description = "";

   @ManyToOne(optional = false)
   @JoinColumn(name = "building_id")
   private Building building;

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "room")
   private Set<Device> devices = new HashSet<Device>();

   /**
    * Create a new room.
    */
   public Room()
   {
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
      this.number = number;
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
      this.description = description;
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
    */
   public void add(Device device)
   {
      device.setRoom(this);
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
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (!(o instanceof Room))
         return false;

      final Room oo = (Room) o;
      return (id == oo.id && name == oo.name && number == oo.number && description == oo.description && devices == oo.devices);
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
      return getName();
   }
}
