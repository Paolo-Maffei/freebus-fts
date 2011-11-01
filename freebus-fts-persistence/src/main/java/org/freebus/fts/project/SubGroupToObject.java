package org.freebus.fts.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.PrimaryKey;

/**
 * These objects connect {@link SubGroup sub groups} with {@link DeviceObject
 * device objects}.
 */
@Entity
@Table(name = "sub_group_to_object")
@PrimaryKey(columns = { @Column(name="sub_group_id"), @Column(name="device_object_id") })
public class SubGroupToObject implements Comparable<SubGroupToObject>
{
//   @Id
   @ManyToOne(optional = false)
   @JoinColumn(name = "sub_group_id", nullable = false)
   private SubGroup subGroup;

//   @Id
   @ManyToOne(optional = false)
   @JoinColumn(name = "device_object_id", nullable = false)
   private DeviceObject deviceObject;

   @Column(name = "send", nullable = false)
   private boolean send = true;

   @Column(name = "acknowledge", nullable = false)
   private boolean acknowledge = true;

   /**
    * Create a new object.
    */
   public SubGroupToObject()
   {
   }

   /**
    * Create a new object.
    */
   public SubGroupToObject(SubGroup subGroup, DeviceObject deviceObject)
   {
      this.subGroup = subGroup;
      this.deviceObject = deviceObject;
   }

   /**
    * Prepare the object for being deleted.
    */
   public void dispose()
   {
      if (subGroup != null)
         subGroup.remove(this);
      if (deviceObject != null)
         deviceObject.remove(this);
   }

   /**
    * @return the sub group
    */
   public SubGroup getSubGroup()
   {
      return subGroup;
   }

   /**
    * Set the sub group.
    *
    * @param subGroup - the sub group to set.
    */
   public void setSubGroup(SubGroup subGroup)
   {
      this.subGroup = subGroup;
   }

   /**
    * @return the device object.
    */
   public DeviceObject getDeviceObject()
   {
      return deviceObject;
   }

   /**
    * Set the device object.
    *
    * @param deviceObject - the device object to set
    */
   public void setDeviceObject(DeviceObject deviceObject)
   {
      this.deviceObject = deviceObject;
   }

   /**
    * @return the send flag.
    */
   public boolean isSend()
   {
      return send;
   }

   /**
    * Set the send flag.
    *
    * @param enable - the send flag to set
    */
   public void setSend(boolean enable)
   {
      this.send = enable;
   }

   /**
    * @return the acknowledge flag
    */
   public boolean isAcknowledge()
   {
      return acknowledge;
   }

   /**
    * Set the acknowledge flag.
    *
    * @param enable - the acknowledge flag to set
    */
   public void setAcknowledge(boolean enable)
   {
      this.acknowledge = enable;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return ((subGroup == null ? 0 : subGroup.getId()) << 16) | (deviceObject == null ? 0 : deviceObject.getId());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;
      if (!(o instanceof SubGroupToObject))
         return false;
      final SubGroupToObject oo = (SubGroupToObject) o;

      return (subGroup == null ? oo.subGroup == null : subGroup.equals(oo.subGroup)) &&
         (deviceObject == null ? oo.deviceObject == null : deviceObject.equals(oo.deviceObject));
   }

   /**
    * Compare by device-object and sub-group.
    */
   @Override
   public int compareTo(SubGroupToObject o)
   {
      if (o == null)
         return 1;

      int d;

      if (deviceObject == null)
         d = o.deviceObject == null ? 0 : -1;
      else d = deviceObject.compareTo(o.deviceObject);

      if (d != 0)
         return d;

      if (subGroup == null)
         d = o.subGroup == null ? 0 : -1;
      else d = subGroup.compareTo(o.subGroup);

      return 0;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("device object ");
      if (deviceObject == null) sb.append("<null>");
      else sb.append("#").append(deviceObject.getId());

      sb.append(" group ");
      if (subGroup == null) sb.append("<null>");
      else sb.append(subGroup.getGroupAddress());

      return sb.toString();
   }
}
