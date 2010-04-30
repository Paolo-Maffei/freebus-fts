package org.freebus.fts.project;

import java.util.Iterator;
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

/**
 * List for handling areas in the project like add, delete, etc
 * @author jsachs
 *
 */
@Entity
@Table(name = "AreaList")
public class AreaList
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 1, table = "AreaList",  name = "GenAreaListId",
         pkColumnName = "seq_name", valueColumnName = "seq_count")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "area_list_id", nullable = false)
   private int id;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "project")
   @OrderBy("id")
   private List<Area> areas = new Vector<Area>();
   
   @Column(name = "parent_project")
   private Project parentProject;
   
   public AreaList(Project parent)
   {
      parentProject = parent;
   }
   
   /**
    * Add an area to the project.
    *
    * @param area - the area to add.
    *
    * @throws IllegalArgumentException - if the project already contains the area.
    */
   public boolean add(Area area)
   {
      if (areas.contains(area))
      {
         //FIXME this is not really a place for an exception : throw new IllegalArgumentException("Area was previously added: " + area);
         return false;
      }

      area.setProject(parentProject);
      areas.add(area);
      return true;
   }

   /**
    * Remove an area from the project.
    */
   public boolean remove(Area area)
   {
      if (areas.contains(area))
      {
         area.setProject(null);
         areas.remove(area);
         return true;
      }
      return false;
      
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
   public List<Area> getAreaList() //FIXME this function should be obsolete from now !
   {
      return areas;
   }
   
   /**
    * returns true if AreaList is Empty
    * @return
    */
   public boolean isEmpty()
   {
      return areas.isEmpty();
   }
   
   /**
    * return number of Areas in list
    */
   public int size()
   {
      return areas.size();
   }
   
   public Iterator<Area> iterator()
   {
      return areas.iterator();
   }
   
   /**
    * Adds a new area with the give name to the list
    * @param name name for the area
    * @return newly created area
    */
   public Area newArea(String name)
   {
      if (areas.contains(name))
      {
         throw new IllegalArgumentException("Area name already exists: " + name);
      }
      
      final Area area = new Area();
      area.setName(name);
      
      areas.add(area);
      
      return area;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public int getId()
   {
      return id;
   }
}
