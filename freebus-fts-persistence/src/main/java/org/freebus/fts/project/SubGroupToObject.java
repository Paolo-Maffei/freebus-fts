package org.freebus.fts.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * These objects connect {@link SubGroup sub groups} with {@link DeviceObject
 * device objects}.
 */
@Entity
@Table(name = "sub_group_to_object")
public class SubGroupToObject
{
   @Id
   @ManyToOne(optional = false)
   @JoinColumn(name = "sub_group_id", nullable = false)
   private SubGroup subGroup;

   @Id
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
}
