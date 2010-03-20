package org.freebus.fts.project;

import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.project.internal.I18n;

/**
 * A mid-group in a {@link MainGroup}. A mid-group is the mid-level group for
 * the logical structure of a project. Every mid-group belongs to a
 * {@link MainGroup}, and can contain {@link SubGroup}s.
 */
@Entity
@Table(name = "mid_group")
public class MidGroup
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequence",  name = "GenMidGroupId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "mid_group_id", nullable = false)
   private int id;

   @Column(name = "mid_group_name", nullable = false)
   private String name = "";

   @Column(name = "mid_group_address", nullable = false)
   private int address;

   @ManyToOne(optional = false)
   @JoinColumn(name = "main_group_id")
   private MainGroup mainGroup;

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "midGroup")
   @OrderBy("address")
   private List<SubGroup> subGroups = new Vector<SubGroup>();

   /**
    * Create a new mid-group.
    */
   public MidGroup()
   {
   }

   /**
    * Create a new mid-group.
    */
   public MidGroup(int id)
   {
      this.id = id;
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
    * Add a sub-group to the mid-group.
    *
    * @param group - the sub-group to add
    *
    * @throws IllegalArgumentException - if the mid-group already contains the sub-group.
    */
   public void add(SubGroup group)
   {
      if (subGroups.contains(group))
         throw new IllegalArgumentException("Sub-group was previously added: " + group);

      group.setMidGroup(this);
      subGroups.add(group);
   }

   /**
    * @return the sub-groups container.
    */
   public List<SubGroup> getSubGroups()
   {
      return subGroups;
   }

   /**
    * Set the sub-groups container.
    */
   public void setSubGroups(List<SubGroup> groups)
   {
      this.subGroups = groups;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof MidGroup))
         return false;

      final MidGroup oo = (MidGroup) o;
      if (id != oo.id || address != oo.address || !name.equals(oo.name))
         return false;

      if (!subGroups.equals(oo.subGroups))
         return false;

      return true;
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
      String mainAddr;
      if (mainGroup == null)
         mainAddr = "?";
      else mainAddr = Integer.toString(mainGroup.getAddress());

      String nm = getName();
      if (nm == null || nm.isEmpty()) nm = I18n.getMessage("MidGroup.DefaultName");

      return String.format("%s/%d  %s", mainAddr, getAddress(), nm);
   }
}
