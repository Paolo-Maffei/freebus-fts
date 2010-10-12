package org.freebus.fts.project;

import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.common.types.ObjectPriority;
import org.freebus.fts.common.types.ObjectType;
import org.freebus.fts.products.CommunicationObject;

/**
 * A device object specifies the details of a communication object of a specific
 * device. Device objects represent the input and output connections of devices
 * that are usually connected via address groups.
 */
@Entity
@Table(name = "device_object")
public class DeviceObject implements Comparable<DeviceObject>
{
   @Id
   @TableGenerator(name = "DeviceObject", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "DeviceObject")
   @Column(name = "device_object_id", nullable = false)
   private int id;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "device_id", nullable = false)
   private Device device;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "object_id", nullable = false)
   private CommunicationObject comObject;

   @Column(name = "object_read", nullable = false)
   private boolean read = true;

   @Column(name = "object_write", nullable = false)
   private boolean write = true;

   @Column(name = "object_comm", nullable = false)
   private boolean comm = true;

   // enable transmissions
   @Column(name = "object_trans", nullable = false)
   private boolean trans = true;

   @Column(name = "object_update", nullable = false)
   private boolean update;

   @Column(name = "object_readoninit", nullable = false)
   private boolean readOnInit;

   // @Column(name = "device_object_visible", nullable = false)
   // private boolean visible;

   @Enumerated(EnumType.ORDINAL)
   @Column(name = "device_object_type", nullable = true)
   private ObjectType type;

   @Enumerated(EnumType.ORDINAL)
   @Column(name = "object_priority", nullable = false)
   private ObjectPriority priority;

   @Column(name = "dpt_type")
   // @VdxField(name = "eib_data_type_code" * 1000 + "eib_data_subtype_code")
   private Integer dptType;

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "deviceObject")
   private List<SubGroupToObject> subGroupToObjects = new Vector<SubGroupToObject>();

   /**
    * Create a device object.
    */
   public DeviceObject()
   {
   }

   /**
    * Create a device object.
    */
   public DeviceObject(CommunicationObject comObject)
   {
      this.comObject = comObject;
      copyComObjectFlags();
   }

   /**
    * Prepare the device object for being deleted.
    */
   public void dispose()
   {
      if (device != null)
         device.remove(this);

      for (final SubGroupToObject sgo : subGroupToObjects)
         sgo.dispose();

      subGroupToObjects.clear();
   }

   /**
    * @return the id
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the id of the device object.
    * 
    * @param id - the id to set
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the device
    */
   public Device getDevice()
   {
      return device;
   }

   /**
    * Set the device.
    * 
    * @param device - the device to set
    */
   public void setDevice(Device device)
   {
      this.device = device;
   }

   /**
    * @return the communication object
    */
   public CommunicationObject getComObject()
   {
      return comObject;
   }

   /**
    * Set the communication object.
    * 
    * @param comObject - the communication object to set
    */
   public void setComObject(CommunicationObject comObject)
   {
      this.comObject = comObject;
      copyComObjectFlags();
   }

   /**
    * @return the read flag.
    */
   public boolean isReadEnabled()
   {
      return read;
   }

   /**
    * Set the read flag.
    * 
    * @param read - the read flag to set.
    */
   public void setReadEnabled(boolean read)
   {
      this.read = read;
   }

   /**
    * @return the write flag.
    */
   public boolean isWriteEnabled()
   {
      return write;
   }

   /**
    * Set the write flag.
    * 
    * @param write - the write flag to set
    */
   public void setWriteEnabled(boolean write)
   {
      this.write = write;
   }

   /**
    * @return the comm flag
    */
   public boolean isCommEnabled()
   {
      return comm;
   }

   /**
    * Set the comm flag.
    * 
    * @param comm - the comm flag to set
    */
   public void setCommEnabled(boolean comm)
   {
      this.comm = comm;
   }

   /**
    * @return the trans flag
    */
   public boolean isTransEnabled()
   {
      return trans;
   }

   /**
    * @param trans - the trans flag to set
    */
   public void setTransEnabled(boolean trans)
   {
      this.trans = trans;
   }

   /**
    * @return the update
    */
   public boolean isUpdate()
   {
      return update;
   }

   /**
    * @param update the update to set
    */
   public void setUpdate(boolean update)
   {
      this.update = update;
   }

   /**
    * @return the read on init flag
    */
   public boolean isReadOnInit()
   {
      return readOnInit;
   }

   /**
    * Set the read on init flag.
    * 
    * @param readOnInit - the read on init to set
    */
   public void setReadOnInit(boolean readOnInit)
   {
      this.readOnInit = readOnInit;
   }

   /**
    * @return the visible
    */
   public boolean isVisible()
   {
      return device.isVisible(getComObject());
   }

   public void setPriority(ObjectPriority priority)
   {
      this.priority = priority;
   }

   public ObjectPriority getPriority()
   {
      return priority;
   }

   /**
    * Get the object type. Returns the object type of the communication
    * object if null.
    *
    * @return the object type.
    */
   public ObjectType getType()
   {
      if (type == null && comObject != null)
         return comObject.getType();
      return type;
   }

   /**
    * Set the object type.
    * 
    * @param type - the object type to set
    */
   public void setType(ObjectType type)
   {
      this.type = type;
   }

   /**
    * @return the datapoint type, or zero if undefined.
    */
   public int getDptType()
   {
      return dptType == null ? 0 : dptType / 1000;
   }

   /**
    * @return the datapoint sub type, or zero if undefined.
    */
   public int getDptSubType()
   {
      return dptType == null ? 0 : dptType % 1000;
   }

   /**
    * Set the datapoint type.
    * 
    * @param type - the datapoint type to set
    * @param subType - the datapoint sub type to set
    */
   public void setDptType(int type, int subType)
   {
      this.dptType = Integer.valueOf(type * 1000 + subType);
   }

   /**
    * Clear the datapoint type.
    */
   public void clearDptType()
   {
      this.dptType = null;
   }

   /**
    * @return The list of subgroup-to-object mappings.
    */
   public List<SubGroupToObject> getSubGroupToObjects()
   {
      return subGroupToObjects;
   }

   /**
    * Add a sub-group to this device object. A new {@link SubGroupToObject}
    * object is created to represent the mapping. The {@link SubGroupToObject}
    * object is also added to the sub-group.
    * 
    * @param subGroup - the sub-group to add.
    * @return The created {@link SubGroupToObject} object.
    */
   public SubGroupToObject add(SubGroup subGroup)
   {
      final SubGroupToObject sgo = new SubGroupToObject(subGroup, this);
      add(sgo);
      subGroup.add(sgo);
      return sgo;
   }

   /**
    * Add a sub-group-to-object object.
    * 
    * @param sgo - the sub-group-to-object object to add.
    */
   public void add(SubGroupToObject sgo)
   {
      if (subGroupToObjects.contains(sgo))
         throw new IllegalArgumentException("Object was prevously added");

      subGroupToObjects.add(sgo);
   }

   /**
    * Remove a sub-group-to-object object.
    * 
    * @param sgo - the sub-group-to-object object to remove.
    */
   public void remove(SubGroupToObject sgo)
   {
      subGroupToObjects.remove(sgo);
      sgo.setDeviceObject(null);
   }

   /**
    * Copy the communication flags and the object priority from the
    * communication object to this object.
    */
   private void copyComObjectFlags()
   {
      trans = comObject.isTransEnabled();
      read = comObject.isReadEnabled();
      write = comObject.isWriteEnabled();
      comm = comObject.isCommEnabled();
      priority = comObject.getPriority();
   }

   /**
    * Test if the device object contains a connection to the given sub-group.
    * The comparison is done with {@link SubGroup#equals(Object)}.
    * 
    * @param subGroup - the sub-group to test.
    * @return true if a connection to the sub-group exists.
    */
   public boolean contains(SubGroup subGroup)
   {
      for (final SubGroupToObject sgo : subGroupToObjects)
      {
         if (sgo.getSubGroup().equals(subGroup))
            return true;
      }

      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(DeviceObject o)
   {
      if (comObject == null)
         return o.comObject == null ? -1 : 0;
      if (o.comObject == null)
         return 1;

      return comObject.compareTo(o.comObject);
   }
}
