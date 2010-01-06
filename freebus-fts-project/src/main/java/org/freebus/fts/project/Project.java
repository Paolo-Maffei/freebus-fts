package org.freebus.fts.project;

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

/**
 * Main class for a FTS project.
 * 
 * @See {@link ProjectManager#getProject} - to access the global project
 *      instance.
 * @See {@link ProjectManager#newProject} - to create a new project.
 * @See {@link ProjectManager#openProject} - to open an existing project.
 */
@Entity
@Table(name = "project")
public class Project
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "idgen", pkColumnValue = "project")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "project_id", columnDefinition = "INT", nullable = false)
   private int id;

   @Column(name = "project_name", nullable = false)
   private String name;

   @Column(name = "project_description")
   private String description;

//   @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
   private Set<Area> areas = new HashSet<Area>();

   // @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
   private Set<Building> buildings = new HashSet<Building>();

   // @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
   private Set<MainGroup> mainGroups = new HashSet<MainGroup>();

   /**
    * Create a new project.
    * 
    * @see {@link ProjectManager#newProject}.
    */
   public Project()
   {
      this.name = "Unnamed";
      this.id = 0;
      description = "";
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
      if (!(o instanceof Project))
         return false;

      final Project oo = (Project) o;
      if (id != oo.id || name != oo.name || description != oo.description || areas != oo.areas
            || buildings != oo.buildings || mainGroups != oo.mainGroups)
      {
         return false;
      }
      return true;
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
