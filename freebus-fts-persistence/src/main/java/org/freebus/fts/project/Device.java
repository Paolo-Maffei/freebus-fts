package org.freebus.fts.project;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequence", name = "GenDeviceId")
   @GeneratedValue(strategy = GenerationType.TABLE)
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
   private Map<Parameter, DeviceParameter> parameterValues;

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
    * @param id the id of the object
    * @param virtualDevice the virtual device, from which the
    *           {@link CatalogEntry} and {@link Program} are taken.
    */
   public Device(int id, VirtualDevice virtualDevice)
   {
      this(id, virtualDevice.getCatalogEntry(), virtualDevice.getProgram());
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
    * Set the line to which the device belongs.
    */
   public void setLine(Line line)
   {
      this.line = line;
   }

   /**
    * Set the room in which the device is physically installed.
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
      this.program = program;
   }

   /**
    * Get a device parameter. If no device parameter exists, one is created with
    * the parameter's default value as value.
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
    * Test if a specific communication object is visible.
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
    * Find all active / visible communication objects.
    * 
    * @return An array of all visible communication objects.
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
