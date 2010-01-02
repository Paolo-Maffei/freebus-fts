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

import org.freebus.fts.project.internal.I18n;

/**
 * A mid-group in a {@link MainGroup}. A mid-group is the mid-level group for
 * the logical structure of a project. Every mid-group belongs to a
 * {@link MainGroup}, and can contain {@link Group}s.
 */
@Entity
@Table(name = "mid_group")
public class MidGroup
{
   @Id
   @Column(name = "mid_group_id", nullable = false)
   private int id;

   @Column(name = "mid_group_name", nullable = true)
   private String name = "";

   @Column(name = "mid_group_address", nullable = false)
   private int address;

   @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
   private MainGroup mainGroup;

   @OneToMany(mappedBy = "midGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
   private Set<Group> groups = new HashSet<Group>();

   /**
    * Create a new mid-group.
    */
   public MidGroup()
   {
   }

   /**
    * @return the mid-group id
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the mid-group id
    */
   public void setId(int id)
   {
      this.id = id;
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
    * @return the main-group
    */
   public MainGroup getMainGroup()
   {
      return mainGroup;
   }

   /**
    * Set the main-group
    */
   public void setMainGroup(MainGroup mainGroup)
   {
      this.mainGroup = mainGroup;
   }

   /**
    * Add a group to the mid-group.
    */
   public void add(Group group)
   {
      group.setMidGroup(this);
      groups.add(group);
   }

   /**
    * @return the groups container.
    */
   public Set<Group> getGroups()
   {
      return groups;
   }

   /**
    * Set the groups container.
    */
   public void setGroups(Set<Group> groups)
   {
      this.groups = groups;
   }

   /**
    * @return a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      String mainAddr;
      if (mainGroup == null)
         mainAddr = "?";
      else mainAddr = Integer.toString(mainGroup.getAddress());

      String nm = getName();
      if (nm == null || nm.isEmpty()) nm = I18n.getMessage("MidGroup.DefaultName");

      return String.format("%s/%d  %s", mainAddr, getAddress(), nm);
   }
}
