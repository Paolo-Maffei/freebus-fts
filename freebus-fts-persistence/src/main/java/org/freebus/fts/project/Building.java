package org.freebus.fts.project;

import java.util.Set;
import java.util.TreeSet;

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
 * A building in a {@link Project}. A building is the top-level group for the
 * physical structure of a project.
 */
@Entity
@Table(name = "building")
public class Building implements Comparable<Building>
{
   @Id
   @TableGenerator(name = "Building", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "Building")
   @Column(name = "building_id", nullable = false)
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "project_id")
   private Project project;

   @Column(name = "building_name", nullable = false)
   private String name = "";

   @Column(name = "description", nullable = false)
   private String description = "";

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "building")
   private Set<Room> rooms = new TreeSet<Room>();

   /**
    * Create a new building.
    */
   public Building()
   {
      this(0);
   }

   /**
    * Create a new building with an id.
    */
   public Building(int id)
   {
      this.id = id;
   }

   /**
    * Remove the building from its project.
    */
   public void detach()
   {
      if (project != null)
         project.remove(this);
   }

   /**
    * @return the building id
    */
   public int getId()
   {
      return id;
   }

   /**
    * @param id the building id to set
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the project.
    */
   public Project getProject()
   {
      return project;
   }

   /**
    * Set the project to which the building belongs.
    */
   public void setProject(Project project)
   {
      this.project = project;
   }

   /**
    * @return the name of the building.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the building.
    */
   public void setName(String name)
   {
      this.name = name == null ? "" : name;
   }

   /**
    * @return The (optional) description of the building.
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * Set the (optional) description of the building.
    */
   public void setDescription(String description)
   {
      this.description = description == null ? "" : description;
   }

   /**
    * Add a room to the building.
    *
    * @param room - the room to add.
    *
    * @throws IllegalArgumentException - If the room was already added to the building.
    */
   public void add(Room room)
   {
      if (rooms.contains(room))
         throw new IllegalArgumentException("Room was previously added: " + room);

      final Building prev = room.getBuilding();
      if (prev != null)
         prev.remove(room);

      room.setBuilding(this);
      rooms.add(room);
   }

   /**
    * Remove a room from the building.
    *
    * @param room - the room to remove.
    */
   public void remove(Room room)
   {
      room.setBuilding(null);
      rooms.remove(room);
   }

   /**
    * @return the rooms.
    */
   public Set<Room> getRooms()
   {
      if (rooms == null)
         rooms = new TreeSet<Room>();

      return rooms;
   }

   /**
    * @param rooms the rooms to set.
    */
   public void setRooms(Set<Room> rooms)
   {
      this.rooms = rooms;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof Building))
         return false;

      final Building oo = (Building) o;
      if (id != oo.id || !name.equals(oo.name) || !description.equals(oo.description))
         return false;
      return rooms.equals(oo.rooms);
   }

   /**
    * Compare by name & id.
    */
   @Override
   public int compareTo(Building o)
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
