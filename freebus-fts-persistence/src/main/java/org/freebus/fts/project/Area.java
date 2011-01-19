package org.freebus.fts.project;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class Area implements Comparable<Area>
{
   @Id
   @TableGenerator(name = "Area", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "Area")
   @Column(name = "area_id", nullable = false)
   private int id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "project_id")
   private Project project;

   @Column(name = "area_name")
   private String name = "";

   @Column(name = "area_address", nullable = false)
   private int address;

   @Column(name = "area_description")
   private String description;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "area")
   private TreeSet<Line> lines = new TreeSet<Line>();

   /**
    * The highest valid number of an area address
    */
   public final static int MAX_ADDR = 15;

   @Deprecated
   public final static int MIN_NAME_LENGTH = 2;    //TODO Define minimum accepted length for an Area Name
   
   /**
    * Create a new area.
    */
   public Area()
   {
   }

   /**
    * Create an area with a id.
    */
   public Area(int id)
   {
      this.id = id;
   }

   /**
    * Remove the area from its project.
    */
   public void detach()
   {
      if (project != null)
         project.remove(this);
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
      this.name = name == null ? "" : name;
   }

   /**
    * Set the description.
    *
    * @param description - the description to set
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * @return The description.
    */
   public String getDescription()
   {
      return description;
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
    *
    * @param line - the line to add
    *
    * @throws IllegalArgumentException - if the area already contains the line.
    */
   public void add(Line line)
   {
      if (lines.contains(line))
         throw new IllegalArgumentException("Line was previously added: " + line);

      final Area oldArea = line.getArea();
      if (oldArea != null)
         oldArea.remove(line);

      line.setArea(this);
      lines.add(line);
   }

   /**
    * Remove a line from the area.
    *
    * @param line - the line to remove
    *
    * @throws IllegalArgumentException - if the line is not part of the area
    */
   public void remove(Line line)
   {
      if (lines == null)
         return;
      
      if (!lines.contains(line))
         throw new IllegalArgumentException("Line is not part of area: " + line.getArea() + ", line: " + line);

      line.setArea(null);
      lines.remove(line);
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
   void setLines(TreeSet<Line> lines)
   {
      this.lines = lines;
   }

   /**
    * request number of all used addresses of the physical addresses by Lines
    * this could be used to find out free addresses
    * @return array with all used addresses
    */
   public int[] getUsedLineAddresses()
   {
      int used[] = new int[lines.size()];
      int cnt = 0;
      for (Line line : getLines())
      {
         used[cnt] = line.getAddress();
         cnt++;
      }
      
      Arrays.sort(used);
      
      return used;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof Area))
         return false;

      final Area oo = (Area) o;

      return (id == oo.id && address == oo.address && name.equals(oo.name) && lines.equals(oo.lines));
   }

   /**
    * Compare two areas by address and id.
    */
   @Override
   public int compareTo(Area o)
   {
      if (o == null)
         return 1;

      final int d = address - o.address;
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
      return (id << 8) | address;
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
