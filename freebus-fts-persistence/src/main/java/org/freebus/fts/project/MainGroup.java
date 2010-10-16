package org.freebus.fts.project;

import java.util.Set;
import java.util.TreeSet;

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

import org.freebus.fts.project.internal.I18n;

/**
 * A main-group in a {@link Project}. A main-group is the top-level group for
 * the logical structure of a project. The main-group holds the first number of
 * a group address.
 */
@Entity
@Table(name = "main_group")
public class MainGroup implements Comparable<MainGroup>
{
   @Id
   @TableGenerator(name = "MainGroup", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "MainGroup")
   @Column(name = "main_group_id", nullable = false)
   private int id;

   @Column(name = "main_group_name", nullable = true)
   private String name = "";

   @Column(name = "main_group_address", nullable = false)
   private int address;

   @ManyToOne(optional = false)
   @JoinColumn(name = "project_id")
   private Project project;

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "mainGroup")
   private Set<MidGroup> midGroups = new TreeSet<MidGroup>();

   /**
    * Create a new main-group.
    */
   public MainGroup()
   {
      this(0);
   }

   /**
    * Create a new main-group with the given id.
    */
   public MainGroup(int id)
   {
      this.id = id;
   }

   /**
    * @return the main-group id.
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the main-group id.
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
    * Set the project.
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
    * Set the name
    */
   public void setName(String name)
   {
      this.name = name == null ? "" : name;
   }

   /**
    * @return the address
    */
   public int getAddress()
   {
      return address;
   }

   /**
    * Set the address.
    */
   public void setAddress(int address)
   {
      this.address = address;
   }

   /**
    * Add a mid-group to the main-group.
    *
    * @param midGroup - the mid-group to add.
    *
    * @throws IllegalArgumentException - if the main-group already contains the mid-group.
    */
   public void add(MidGroup midGroup)
   {
      if (midGroups.contains(midGroup))
         throw new IllegalArgumentException("Mid-group was previously added: " + midGroup);

      final MainGroup prev = midGroup.getMainGroup();
      if (prev != null)
         prev.remove(midGroup);

      midGroup.setMainGroup(this);
      midGroups.add(midGroup);
   }

   /**
    * Delete the mid-group from the main-group.
    *
    * @param midGroup - the mid-group to remove.
    */
   public void remove(MidGroup midGroup)
   {
      if (!midGroups.contains(midGroup))
         throw new IllegalArgumentException("Mid-group is not part of this main-group: " + this + ", mid-group: " + midGroup);

      midGroup.setMainGroup(null);
      midGroups.remove(midGroup);
   }

   /**
    * @return the mid-groups container.
    */
   public Set<MidGroup> getMidGroups()
   {
      return midGroups;
   }

   /**
    * Set the mid-groups container.
    */
   void setMidGroups(Set<MidGroup> midGroups)
   {
      this.midGroups = midGroups;
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
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof MainGroup))
         return false;

      final MainGroup oo = (MainGroup) o;
      if (id != oo.id || address != oo.address || !name.equals(oo.name))
         return false;

      if (!midGroups.equals(oo.midGroups))
         return false;

      return true;
   }

   /**
    * Compare by address and id.
    */
   @Override
   public int compareTo(MainGroup o)
   {
      if (o == null)
         return 1;

      final int d = address - o.address;
      if (d != 0)
         return d;

      return id - o.id;
   }

   /**
    * @return a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      String nm = getName();
      if (nm == null || nm.isEmpty()) nm = I18n.getMessage("MainGroup.DefaultName");

      return String.format("%d  %s", getAddress(), nm);
   }
}
