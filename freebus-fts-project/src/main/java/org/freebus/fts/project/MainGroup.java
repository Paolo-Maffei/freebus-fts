package org.freebus.fts.project;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A main-group in a {@link Project}. A main-group is the top-level group for
 * the logical structure of a project. The main-group holds the first number of
 * a group address.
 */
@Entity
@Table(name = "main_group")
public class MainGroup
{
   @Id
   @Column(name = "main_group_id", nullable = false)
   private int id;

   @Column(name = "main_group_name", nullable = true)
   private String name = "";

   @Column(name = "main_group_address", nullable = false)
   private int address;

   @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
   private Project project;

   @OneToMany(mappedBy = "mainGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
   private Set<MidGroup> midGroups = new HashSet<MidGroup>();

   /**
    * Create a new main-group.
    */
   public MainGroup()
   {
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
    * Set the address.
    */
   public void setAddress(int address)
   {
      this.address = address;
   }

   /**
    * Add a mid-group to the main-group.
    */
   public void add(MidGroup midGroup)
   {
      midGroup.setMainGroup(this);
      midGroups.add(midGroup);
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
   public void setMidGroups(Set<MidGroup> midGroups)
   {
      this.midGroups = midGroups;
   }

   /**
    * @return a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      return String.format("%d  %s", getAddress(), getName());
   }
}
