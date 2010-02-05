package org.freebus.fts.project;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Main class for a FTS project.
 * 
 * @See {@link ProjectManager#getProject} - to access the global project
 *      instance.
 * @See {@link ProjectManager#openProject} - to open an existing project.
 * @See {@link ProjectManager#newProject} - to create a new project.
 * @See {@link SampleProjectFactory#newProject} - to create a sample project.
 */
@Entity
@Table(name = "project")
public class Project
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 1, table = "sequences", name = "GenProjectId")
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

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
   private Set<Area> areas = new HashSet<Area>();

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
   private Set<Building> buildings = new HashSet<Building>();

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
   private Set<MainGroup> mainGroups = new HashSet<MainGroup>();

   /**
    * Create a new project.
    * 
    * @see {@link ProjectManager#newProject}.
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
    */
   public void add(Area area)
   {
      area.setProject(this);
      areas.add(area);
   }

   /**
    * Add a building to the project.
    */
   public void add(Building building)
   {
      building.setProject(this);
      buildings.add(building);
   }

   /**
    * Add a main-group to the project.
    */
   public void add(MainGroup mainGroup)
   {
      mainGroup.setProject(this);
      mainGroups.add(mainGroup);
   }

   /**
    * Set the areas container.
    */
   public void setAreas(Set<Area> areas)
   {
      this.areas = areas;
   }

   /**
    * @return the areas container.
    */
   public Set<Area> getAreas()
   {
      return areas;
   }

   /**
    * Set the buildings container.
    */
   public void setBuildings(Set<Building> buildings)
   {
      this.buildings = buildings;
   }

   /**
    * @return the buildings container.
    */
   public Set<Building> getBuildings()
   {
      return buildings;
   }

   /**
    * Set the main-groups container.
    */
   public void setMainGroups(Set<MainGroup> mainGroups)
   {
      this.mainGroups = mainGroups;
   }

   /**
    * @return the main-groups container.
    */
   public Set<MainGroup> getMainGroups()
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
      return id == oo.id && name == oo.name && description == oo.description && areas.equals(oo.areas)
            && buildings.equals(oo.buildings) && mainGroups.equals(oo.mainGroups);
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
