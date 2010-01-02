package org.freebus.fts.project;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.freebus.knxcomm.telegram.GroupAddress;

/**
 * A group in a {@link MidGroup}. A group is the low-level group for the logical
 * structure of a project. Every group belongs to a {@link MidGroup}.
 */
@Entity
@Table(name = "group")
public class Group
{
   @Id
   @Column(name = "group_id", nullable = false)
   private int id;

   @Column(name = "group_name", nullable = true)
   private String name = "";

   @Column(name = "group_address", nullable = false)
   private int address;

   @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
   private MidGroup midGroup;

   /**
    * Create a new group.
    */
   public Group()
   {
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
      this.name = name;
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
      if (midGroup == null) return GroupAddress.BROADCAST;
      final MainGroup mainGroup = midGroup.getMainGroup();
      if (mainGroup == null) return GroupAddress.BROADCAST;
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
    * @return a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      return String.format("%s %s", getGroupAddress().toString(), getName());
   }
}
