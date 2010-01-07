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
 * An area in a {@link Project}. An area is the top-level
 * group for the topological structure of an EIB bus. The
 * area holds the first number of a physical address.
 */
@Entity
@Table(name = "area")
public class Area
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "GenAreaId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "area_id", nullable = false)
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "project_id")
   private Project project;

   @Column(name = "area_name")
   private String name;

   @Column(name = "area_address", nullable = false)
   private int address;

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "area")
   private Set<Line> lines = new HashSet<Line>();

   /**
    * Create a new area.
    */
   public Area()
   {
   }

   /**
    * @return the area id
    */
   public int getId()
   {
      return id;
   }

   /**
    * @param id the area id to set
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the project
    */
   public Project getProject()
   {
      return project;
   }

   /**
    * @param project the project to set
    */
   public void setProject(Project project)
   {
      this.project = project;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      if (name == null) return "";
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
    * @return the address
    */
   public int getAddress()
   {
      return address;
   }

   /**
    * @param address the address to set
    */
   public void setAddress(int address)
   {
      this.address = address;
   }

   /**
    * Add a line to the area.    
    */
   public void add(Line line)
   {
      line.setArea(this);
      lines.add(line);
   }

   /**
    * @return the lines
    */
   public Set<Line> getLines()
   {
      return lines;
   }

   /**
    * @param lines the lines to set
    */
   public void setLines(Set<Line> lines)
   {
      this.lines = lines;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (!(o instanceof Area))
         return false;

      final Area oo = (Area) o;
      return (id == oo.id && address == oo.address && name == oo.name && lines == oo.lines);
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
      return String.format("%d. %s", getAddress(), getName());
   }
}
