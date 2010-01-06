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
import javax.persistence.UniqueConstraint;


/**
 * A building in a {@link Project}. A building is the top-level
 * group for the physical structure of a project.
 */
@Entity
@Table(name = "building", uniqueConstraints = @UniqueConstraint(columnNames = { "building_id" } ))
public class Building
{
   @Id
   @Column(name = "building_id", columnDefinition = "INT", nullable = false)
   private int id;

   @ManyToOne(cascade=CascadeType.ALL)
   private Project project;

   @Column(name = "building_name", nullable = false)
   private String name = "";

   @Column(name = "description")
   private String description;

   @OneToMany(mappedBy = "building", cascade = CascadeType.ALL)
   private Set<Room> rooms = new HashSet<Room>();

   /**
    * Create a new building.
    */
   public Building()
   {
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
      this.name = name;
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
      this.description = description;
   }

   /**
    * Add a room to the building.    
    */
   public void add(Room room)
   {
      room.setBuilding(this);
      rooms.add(room);
   }

   /**
    * @return the rooms.
    */
   public Set<Room> getRooms()
   {
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
    * @return a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      return getName();
   }
}
