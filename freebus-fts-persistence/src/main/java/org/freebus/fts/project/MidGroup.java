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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.interfaces.Addressable;
import org.freebus.fts.interfaces.Named;
import org.freebus.fts.project.internal.I18n;

/**
 * A mid-group in a {@link MainGroup}. A mid-group is the mid-level group for
 * the logical structure of a project. Every mid-group belongs to a
 * {@link MainGroup}, and can contain {@link SubGroup}s.
 */
@Entity
@Table(name = "mid_group")
public class MidGroup implements Comparable<MidGroup>, Addressable, Named
{
   @Id
   @TableGenerator(name = "MidGroup", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "MidGroup")
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
   private Set<SubGroup> subGroups = new TreeSet<SubGroup>();

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
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * Set the name.
    *
    * @param name - the name to set
    */
   public void setName(String name)
   {
      this.name = name == null ? "" : name;
   }

   /**
    * @return the address.
    */
   @Override
   public int getAddress()
   {
      return address;
   }

   /**
    * Set the address (0..7).
    *
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
    * Remove the mid-group from its parent.
    */
   public void detach()
   {
      if (mainGroup != null)
         mainGroup.remove(this);
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

      final MidGroup prev = group.getMidGroup();
      if (prev != null)
         prev.remove(group);

      group.setMidGroup(this);
      subGroups.add(group);
   }

   /**
    * Delete the sub-group from the mid-group.
    *
    * @param subGroup - the sub-group to remove.
    */
   public void remove(SubGroup subGroup)
   {
      if (!subGroups.contains(subGroup))
         throw new IllegalArgumentException("Sub-group is not part of this mid-group: " + this + ", sub-group: " + subGroup);

      subGroup.setMidGroup(null);
      subGroups.remove(subGroup);
   }

   /**
    * @return the sub-groups container.
    */
   public Set<SubGroup> getSubGroups()
   {
      return subGroups;
   }

   /**
    * Set the sub-groups container.
    */
   void setSubGroups(TreeSet<SubGroup> groups)
   {
      this.subGroups = groups;
   }

   /**
    * Get a specific {@link SubGroup sub-group} by it's address.
    * 
    * @param addr - the address of the sub-group.
    * 
    * @return the sub-group or null if no sub-group is found with this address.
    */
   public SubGroup findSubGroupByAddr(int addr)
   {
      for (final SubGroup grp : subGroups)
      {
         if (grp.getAddress() == addr)
            return grp;
      }

      return null;
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
    * Compare by address and id.
    */
   @Override
   public int compareTo(MidGroup o)
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
      String mainAddr;
      if (mainGroup == null)
         mainAddr = "?";
      else mainAddr = Integer.toString(mainGroup.getAddress());

      String nm = getName();
      if (nm == null || nm.isEmpty()) nm = I18n.getMessage("MidGroup.DefaultName");

      return String.format("%s/%d  %s", mainAddr, getAddress(), nm);
   }
}
