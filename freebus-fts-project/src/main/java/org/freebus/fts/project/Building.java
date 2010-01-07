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
 * A building in a {@link Project}. A building is the top-level group for the
 * physical structure of a project.
 */
@Entity
@Table(name = "building")
public class Building
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "GenBuildingId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "building_id", nullable = false)
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "project_id")
   private Project project;

   @Column(name = "building_name", nullable = false)
   private String name = "";

   @Column(name = "description")
   private String description;

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "building")
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
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (!(o instanceof Building))
         return false;

      final Building oo = (Building) o;
      return (id == oo.id && name == oo.name && description == oo.description && rooms == oo.rooms);
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
