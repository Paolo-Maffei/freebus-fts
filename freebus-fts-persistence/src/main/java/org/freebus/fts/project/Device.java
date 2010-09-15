package org.freebus.fts.project;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.VirtualDevice;

/**
 * A KNX/EIB bus device. This is a device that is part of a project, that gets
 * installed somewhere in a house, and gets programmed.
 */
@Entity
@Table(name = "device")
public final class Device
{
   @Id
   @TableGenerator(name = "Device", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "Device")
   @Column(name = "device_id", nullable = false)
   private int id;

   @Column(name = "name", nullable = true)
   private String name;

   @Column(name = "device_address", nullable = false)
   private int address;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "catalog_entry_id", nullable = false)
   public CatalogEntry catalogEntry;

   @ManyToOne(fetch = FetchType.LAZY, optional = true)
   @JoinColumn(name = "program_id", nullable = true)
   private Program program;

   @ManyToOne(optional = false)
   @JoinColumn(name = "line_id")
   private Line line;

   @ManyToOne(optional = true)
   @JoinColumn(name = "room_id")
   private Room room;

   @OneToMany(mappedBy = "device", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @MapKey(name = "parameter")
   private Map<Parameter, DeviceParameter> parameterValues = new HashMap<Parameter, DeviceParameter>();

   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "device")
   private List<DeviceObject> deviceObjects = new Vector<DeviceObject>();

   @Transient
   private DeviceParameters deviceParams;

   /**
    * Create an empty device object.
    */
   public Device()
   {
   }

   /**
    * Create a device object.
    */
   public Device(int id, CatalogEntry catalogEntry, Program program)
   {
      this.id = id;
      this.catalogEntry = catalogEntry;
      this.program = program;

      updateDeviceObjects();
   }

   /**
    * Create a device object.
    */
   public Device(CatalogEntry catalogEntry, Program program)
   {
      this(0, catalogEntry, program);
   }

   /**
    * Create a device object by using {@link CatalogEntry} and {@link Program}
    * of a given {@link VirtualDevice}.
    * 
    * @param virtualDevice the virtual device, from which the
    *           {@link CatalogEntry} and {@link Program} are taken.
    */
   public Device(VirtualDevice virtualDevice)
   {
      this(0, virtualDevice.getCatalogEntry(), virtualDevice.getProgram());
   }

   /**
    * Create a device object by using {@link CatalogEntry} and {@link Program}
    * of a given {@link VirtualDevice}.
    * 
    * @param id the id of the object
    * @param virtualDevice the virtual device, from which the
    *           {@link CatalogEntry} and {@link Program} are taken.
    */
   public Device(int id, VirtualDevice virtualDevice)
   {
      this(id, virtualDevice.getCatalogEntry(), virtualDevice.getProgram());
   }

   /**
    * Remove the device from the line and the room.
    */
   public void detach()
   {
      if (line != null)
         line.remove(this);

      if (room != null)
         room.remove(this);
   }

   /**
    * @param id the id to set
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the id of the device.
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the address of the device. This is the last number of a physical
    * address (e.g. 12 for 1.3.12)
    * 
    * @param address the address to set
    */
   public void setAddress(int address)
   {
      this.address = address;
   }

   /**
    * @return the address of the device.
    */
   public int getAddress()
   {
      return address;
   }

   /**
    * Get the physical address of the device.
    * 
    * @return the physical address of the device.
    */
   public PhysicalAddress getPhysicalAddress()
   {
      if (line == null)
         return PhysicalAddress.NULL;
      final Area area = line.getArea();
      if (area == null)
         return PhysicalAddress.NULL;
      return new PhysicalAddress(area.getAddress(), line.getAddress(), address);
   }

   /**
    * @return the line to which the device belongs.
    */
   public Line getLine()
   {
      return line;
   }

   /**
    * Set the line to which the device belongs. To add a device to a line, use
    * {@link Line#add(Device)} and not this method.
    * 
    * @param line - the line to set.
    */
   public void setLine(Line line)
   {
      this.line = line;
   }

   /**
    * Set the room in which the device is physically installed. To add a device
    * to a room, use {@link Room#add(Device)} and not this method.
    * 
    * @param room - the room to set.
    */
   public void setRoom(Room room)
   {
      this.room = room;
   }

   /**
    * @return the room in which the device is physically installed.
    */
   public Room getRoom()
   {
      return room;
   }

   /**
    * @return the catalog entry of the device.
    */
   public CatalogEntry getCatalogEntry()
   {
      return catalogEntry;
   }

   /**
    * Set the catalog entry of the device.
    */
   public void setCatalogEntry(CatalogEntry catalogEntry)
   {
      this.catalogEntry = catalogEntry;
   }

   /**
    * @return the program of the device.
    */
   public Program getProgram()
   {
      return program;
   }

   /**
    * Set the program of the device. You should consider calling
    * {@link #clearParameterValues()} when changing the program.
    */
   public void setProgram(Program program)
   {
      if (program != this.program)
      {
         this.program = program;
         updateDeviceObjects();
      }
   }

   /**
    * Get a device parameter. If no device parameter exists, one is created with
    * the parameter's default value as value. Use {@link #getDeviceParameters()}
    * to access the device parameters.
    * 
    * @param param - the parameter for which the device parameter is searched.
    * @return The requested device parameter.
    */
   public synchronized DeviceParameter getDeviceParameter(Parameter param)
   {
      if (deviceParams == null)
         deviceParams = new DeviceParameters(this);

      DeviceParameter deviceParam = parameterValues.get(param);
      if (deviceParam == null)
      {
         deviceParam = new DeviceParameter(this, param, param.getDefault());
         parameterValues.put(param, deviceParam);
      }

      return deviceParam;
   }

   /**
    * @return the device parameters object that is used to access the parameters
    *         of the device.
    */
   public synchronized DeviceParameters getDeviceParameters()
   {
      if (deviceParams == null)
         deviceParams = new DeviceParameters(this);

      return deviceParams;
   }

   /**
    * @return the list of device objects
    */
   public List<DeviceObject> getDeviceObjects()
   {
      return deviceObjects;
   }

   /**
    * @return The name of the device
    */
   public String getName()
   {
      if (name != null)
         return name;

      if (catalogEntry != null)
         return catalogEntry.getName();

      return "device#" + id;
   }

   /**
    * Set the name of the device.
    * 
    * @param name - the name to set.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Remove the device object from the device.
    * 
    * @param deviceObject - the device object to remove.
    * @see DeviceObject#dispose()
    */
   public void remove(DeviceObject deviceObject)
   {
      deviceObjects.remove(deviceObject);
      deviceObject.setDevice(null);
   }

   /**
    * Get the map of parameter values of the device. This is an internal method.
    * 
    * @return the parameter values of the device.
    */
   synchronized Map<Parameter, DeviceParameter> getParameterValues()
   {
      if (parameterValues == null)
         parameterValues = new HashMap<Parameter, DeviceParameter>();

      return parameterValues;
   }

   /**
    * Test if a specific communication object is visible for this device.
    * 
    * @param comObject - the communication object to test.
    * @return true if the communication object is visible.
    */
   public boolean isVisible(final CommunicationObject comObject)
   {
      final Parameter param = comObject.getParameter();
      if (param == null)
         return true;

      final DeviceParameter devParam = getDeviceParameter(param);
      if (!devParam.isVisible())
         return false;

      final Integer expectedParamValue = comObject.getParameterValue();
      if (expectedParamValue == null)
         return true;

      return expectedParamValue.equals(devParam.getIntValue());
   }

   /**
    * Find all active / visible device objects.
    * 
    * @return An array of all visible device objects.
    */
   public DeviceObject[] getVisibleDeviceObjects()
   {
      final Vector<DeviceObject> devObjects = new Vector<DeviceObject>();

      for (final DeviceObject devObject : getDeviceObjects())
      {
         if (isVisible(devObject.getComObject()))
            devObjects.add(devObject);
      }

      final DeviceObject[] arr = new DeviceObject[devObjects.size()];
      devObjects.toArray(arr);
      Arrays.sort(arr);

      return arr;
   }

   /**
    * Find all active / visible communication objects.
    * 
    * @return An array of all visible communication objects.
    * @see #getVisibleDeviceObjects()
    */
   public CommunicationObject[] getVisibleCommunicationObjects()
   {
      final Vector<CommunicationObject> comObjects = new Vector<CommunicationObject>();

      for (final CommunicationObject comObject : program.getCommunicationObjects())
      {
         if (isVisible(comObject))
            comObjects.add(comObject);
      }

      final CommunicationObject[] arr = new CommunicationObject[comObjects.size()];
      comObjects.toArray(arr);
      Arrays.sort(arr);

      return arr;
   }

   /**
    * Collect all group addresses that the device uses.
    * 
    * @return The list of group addresses.
    */
   public Collection<GroupAddress> getGroupAdresses()
   {
      final Set<GroupAddress> result = new HashSet<GroupAddress>(64);

      for (final DeviceObject devObject: getVisibleDeviceObjects())
      {
         for (final SubGroupToObject sgo : devObject.getSubGroupToObjects())
            result.add(sgo.getSubGroup().getGroupAddress());
      }

      return result;
   }

   /**
    * Update the device objects. Called when the program is set or changed.
    * Drops all existing device objects and creates a set for the new program's
    * communication objects.
    */
   public void updateDeviceObjects()
   {
      if (program == null)
      {
         deviceObjects.clear();
         return;
      }

      final Map<Integer, CommunicationObject> visibleComObjects = new HashMap<Integer, CommunicationObject>();
      for (final CommunicationObject comObject : program.getCommunicationObjects())
      {
         if (isVisible(comObject))
         {
            final int number = comObject.getNumber();
            if (visibleComObjects.containsKey(number))
            {
               Logger.getLogger(getClass()).error(
                     "Inconsistency detected: com-objects #" + visibleComObjects.get(number).getId() + " and #"
                           + comObject.getId() + " are visible and have the same unique com-object number " + number);
            }
            visibleComObjects.put(number, comObject);
         }
      }

      final DeviceObject[] oldDevObjects = new DeviceObject[deviceObjects.size()];
      deviceObjects.toArray(oldDevObjects);

      deviceObjects.clear();

      for (final DeviceObject devObject : oldDevObjects)
      {
         final CommunicationObject comObject = devObject.getComObject();
         final int comObjectNumber = comObject.getNumber();

         if (visibleComObjects.containsKey(comObjectNumber))
         {
            // Device object is still in use
            // TODO test if the type of the communication object is still
            // correct
            devObject.setComObject(comObject);
            deviceObjects.add(devObject);
            visibleComObjects.put(comObjectNumber, null);
         }
         else
         {
            // Device object is no longer used
            devObject.dispose();
         }
      }

      for (final Entry<Integer, CommunicationObject> e : visibleComObjects.entrySet())
      {
         final CommunicationObject comObject = e.getValue();
         if (comObject == null)
            continue;

         final DeviceObject devObject = new DeviceObject(comObject);
         devObject.setDevice(this);
         deviceObjects.add(devObject);
      }

      // for (final DeviceObject devObject : deviceObjects)
      // Logger.getLogger(getClass()).debug("  visible device object #" +
      // devObject.getId() + " (com-object #" + devObject.getComObject().getId()
      // + ")");
   }

   /**
    * Update the device parameters. Call when the parameters of the device
    * changed.
    */
   public void updateDeviceParameters()
   {
      // TODO
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof Device))
         return false;

      final Device oo = (Device) o;

      if (id != oo.id || address != oo.address)
         return false;

      if ((catalogEntry == null && oo.catalogEntry != null)
            || (catalogEntry != null && !catalogEntry.equals(oo.catalogEntry)))
         return false;

      if ((program == null && oo.program != null) || (program != null && !program.equals(oo.program)))
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
      return getPhysicalAddress().toString() + ' ' + getName();
   }
}
