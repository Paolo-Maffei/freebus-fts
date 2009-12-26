package org.freebus.fts.project;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Main class for a FTS project.
 * 
 * @See {@link ProjectManager#getProject} - to access the global project
 *      instance.
 * @See {@link ProjectManager#newProject} - to create a new project.
 * @See {@link ProjectManager#openProject} - to open an existing project.
 */
@Entity
@Table(name = "project", uniqueConstraints = @UniqueConstraint(columnNames = "project_id"))
public class Project
{
   @Id
   @Column(name = "project_id", columnDefinition = "INT", nullable = false)
   private int id;

   @Column(name = "project_name", nullable = false)
   private String name;

   @Column(name = "project_description")
   private String description;

   @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
   private Set<Area> areas = new HashSet<Area>();

   /**
    * Create a new project.
    * 
    * @see {@link ProjectManager#newProject}.
    */
   public Project()
   {
      this.name ="Unnamed";
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
    * @param areas the areas to set
    */
   public void setAreas(Set<Area> areas)
   {
      this.areas = areas;
   }

   /**
    * @return the areas
    */
   public Set<Area> getAreas()
   {
      return areas;
   }
}
