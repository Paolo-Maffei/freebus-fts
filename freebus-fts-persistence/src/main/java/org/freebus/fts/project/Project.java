package org.freebus.fts.project;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Main class for a FTS project.
 *
 * @see ProjectManager#getProject()
 * @see ProjectManager#setProject(Project)
 * @see SampleProjectFactory#newProject()
 */
@Entity
@Table(name = "project")
public class Project
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 1, table = "sequence",  name = "GenProjectId",
         pkColumnName = "seq_name", valueColumnName = "seq_count")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "project_id", nullable = false)
   private int id;

   @Column(name = "project_name", nullable = false)
   private String name;

   @Column(name = "project_description")
   private String description;

   @Column(name = "last_modified", nullable = false)
   @Temporal(TemporalType.TIMESTAMP)
   private Date lastModified = new Date();

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "project")
   @OrderBy("id")
   private List<Area> areas = new Vector<Area>();

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
   @OrderBy("id")
   private List<Building> buildings = new Vector<Building>();

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
   @OrderBy("address")
   private List<MainGroup> mainGroups = new Vector<MainGroup>();

    /**
    * Create a new project.
    */
   public Project()
   {
      name = "Unnamed";
   }

   /**
    * Set the project id. Use with care.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the project id.
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the name of the project
    *
    * @param name is the new name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the name of the project
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the project description.
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * @return the project description.
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * Set the time-stamp of the last modification.
    */
   public void setLastModified(Date lastModified)
   {
      this.lastModified = lastModified;
   }

   /**
    * @return the time-stamp of the last modification.
    */
   public Date getLastModified()
   {
      return lastModified;
   }

   /**
    * Add an area to the project.
    *
    * @param area - the area to add.
    *
    * @throws IllegalArgumentException - if the project already contains the area.
    */
   public void addArea(Area area)
   {
      if (areas.contains(area))
         throw new IllegalArgumentException("Area was previously added: " + area);

      area.setProject(this);
      areas.add(area);
   }

   /**
    * Add an area to the project.
    *
    * @param name - the name of the area to add.
    *
    * @throws IllegalArgumentException - if the project already contains the area.
    */
   public Area addArea(String name)
   {
      final Area area = new Area();
      area.setName(name);

      areas.add(area);
      area.setProject(this);

      return area;
   }

   /**
    * Add a building to the project.
    *
    * @param building - the building to add.
    *
    * @throws IllegalArgumentException - if the project already contains the building.
    */
   public void add(Building building)
   {
      if (buildings.contains(building))
         throw new IllegalArgumentException("Building was previously added: " + building);

      building.setProject(this);
      buildings.add(building);
   }

   /**
    * Add a main-group to the project.
    *
    * @param mainGroup - the main-group to add.
    *
    * @throws IllegalArgumentException - if the project already contains the main-group.
    */
   public void add(MainGroup mainGroup)
   {
      if (mainGroups.contains(mainGroup))
         throw new IllegalArgumentException("Main-group was previously added: " + mainGroup);

      mainGroup.setProject(this);
      mainGroups.add(mainGroup);
   }

   /**
    * Remove an area from the project.
    */
   public void remove(Area area)
   {
      area.setProject(null);
      areas.remove(area);
   }

   /**
    * Remove a building from the project.
    */
   public void remove(Building building)
   {
      building.setProject(null);
      buildings.remove(building);
   }

   /**
    * Remove a main-group from the project.
    */
   public void remove(MainGroup mainGroup)
   {
      mainGroup.setProject(null);
      mainGroups.remove(mainGroup);
   }

   /**
    * Set the areas container.
    */
   public void setAreas(List<Area> areas)
   {
      this.areas = areas;
   }

   /**
    * @return the areas container.
    */
   public List<Area> getAreas()
   {
      return areas;
   }

   /**
    * Set the buildings container.
    */
   public void setBuildings(List<Building> buildings)
   {
      this.buildings = buildings;
   }

   /**
    * @return the buildings container.
    */
   public List<Building> getBuildings()
   {
      return buildings;
   }

   /**
    * Set the main-groups container.
    */
   public void setMainGroups(List<MainGroup> mainGroups)
   {
      this.mainGroups = mainGroups;
   }

   /**
    * @return the main-groups container.
    */
   public List<MainGroup> getMainGroups()
   {
      return mainGroups;
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
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof Project))
         return false;

      final Project oo = (Project) o;

      if (id != oo.id)
         return false;

      if (name == null ? name != oo.name : !name.equals(oo.name))
         return false;

      if (description == null ? description != oo.description : !description.equals(oo.description))
         return false;

      if (!areas.equals(oo.areas))
         return false;

      if (!buildings.equals(oo.buildings))
         return false;

      if (!mainGroups.equals(oo.mainGroups))
         return false;

      return true;
   }

   /**
    * request number of all used addresses of the physical addresses by areas
    * this could be used to find out free addresses
    * @return array with all used addresses
    */
   public int[] getUsedAreaAddresses()
   {
      int used[] = new int[areas.size()];
      int cnt = 0;
      for (Area area : getAreas())
      {
         used[cnt] = area.getAddress();
         cnt++;
      }

      Arrays.sort(used);

      return used;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return "Project #" + id + " \"" + name + "\"";
   }
}
