package org.freebus.fts.project;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;


/**
 * An area in a {@link Project}. An area is the top-level
 * group for the topological structure of an EIB bus. The
 * area holds the first number of a physical address.
 */
@Entity
@Table(name = "area", uniqueConstraints = @UniqueConstraint(columnNames = { "area_id" } ))
public class Area
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "area_id", pkColumnValue = "area")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "area_id", columnDefinition = "INT", nullable = false)
   private int id;

   @ManyToOne(cascade=CascadeType.ALL)
   private Project project;

   @Column(name = "area_name")
   private String name = "";

   @Column(name = "area_address", columnDefinition = "INT")
   private int address;

//   @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
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
    * @return a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      return String.format("%d. %s", getAddress(), getName());
   }
}
