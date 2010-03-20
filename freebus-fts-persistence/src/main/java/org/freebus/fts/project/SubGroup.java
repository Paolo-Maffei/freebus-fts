package org.freebus.fts.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.project.internal.I18n;

/**
 * A group in a {@link MidGroup}. A group is the low-level group for the logical
 * structure of a project. Every group belongs to a {@link MidGroup}.
 */
@Entity
@Table(name = "sub_group")
public class SubGroup
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequence",  name = "GenGroupId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "sub_group_id", nullable = false)
   private int id;

   @Column(name = "sub_group_name", nullable = false)
   private String name = "";

   @Column(name = "sub_group_address", nullable = false)
   private int address;

   @ManyToOne(optional = false)
   @JoinColumn(name = "mid_group_id")
   private MidGroup midGroup;

   /**
    * Create a new group.
    */
   public SubGroup()
   {
   }

   /**
    * Create a new group.
    */
   public SubGroup(int id)
   {
      this.id = id;
   }

   /**
    * @return the group-id
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the group-id
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the name of the group.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the group.
    */
   public void setName(String name)
   {
      this.name = name == null ? "" : name;
   }

   /**
    * @return the address of the group.
    */
   public int getAddress()
   {
      return address;
   }

   /**
    * Set the address of the group.
    */
   public void setAddress(int address)
   {
      this.address = address;
   }

   /**
    * @return the {@link GroupAddress} group-address of the group.
    */
   public GroupAddress getGroupAddress()
   {
      if (midGroup == null)
         return GroupAddress.BROADCAST;
      final MainGroup mainGroup = midGroup.getMainGroup();
      if (mainGroup == null)
         return GroupAddress.BROADCAST;
      return new GroupAddress(mainGroup.getAddress(), midGroup.getAddress(), address);
   }

   /**
    * @return the mid-group
    */
   public MidGroup getMidGroup()
   {
      return midGroup;
   }

   /**
    * Set the mid-group
    */
   public void setMidGroup(MidGroup midGroup)
   {
      this.midGroup = midGroup;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof SubGroup))
         return false;

      final SubGroup oo = (SubGroup) o;
      return id == oo.id && address == oo.address && name.equals(oo.name);
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
      String nm = getName();
      if (nm == null || nm.isEmpty())
         nm = I18n.getMessage("Group.DefaultName");

      return String.format("%s  %s", getGroupAddress().toString(), nm);
   }
}
