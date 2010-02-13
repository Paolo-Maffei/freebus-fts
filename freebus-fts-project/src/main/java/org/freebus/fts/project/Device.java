package org.freebus.fts.project;

import java.util.Map;

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

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ParameterAtomicType;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.knxcomm.telegram.PhysicalAddress;

/**
 * A KNX/EIB bus device. This is a device that is part of a project, that gets
 * installed somewhere in a house, and gets programmed.
 */
@Entity
@Table(name = "device")
public final class Device
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "GenDeviceId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "device_id", nullable = false)
   private int id;

   @Column(name = "device_address", nullable = false)
   private int address;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "catalog_entry_id", nullable = false)
   public CatalogEntry catalogEntry;

   @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.PERSIST)
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
   private Map<Parameter,DeviceParameterValue> parameterValues;

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
    * @param address the address to set
    */
   public void setAddress(int address)
   {
      this.address = address;
   }

   /**
    * @return the address
    */
   public int getAddress()
   {
      return address;
   }

   /**
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
    * {@link #clearDeviceParameterValues()} when changing the program.
    */
   public void setProgram(Program program)
   {
      this.program = program;
   }

   /**
    * Returns the value for the parameter <code>param</code>. If the parameter
    * has no value set, the parameter's default value is returned. The returned
    * object can be {@link Integer}, {@link Double}, or {@link String}, depending
    * on the parameter's {@link ParameterAtomicType}.
    * 
    * @param param the parameter whose value is requested.
    * @param valueClass the class that the value is expected to have.
    * 
    * @return the parameter's value.
    */
   public <T extends Object> T getParameterValue(final Parameter param, Class<T> valueClass)
   {
//      T result;
      // TODO

//      if (parameterValues.contains(param))
//      {
//         T result = (T) parameterValues.get(param);
//      }

//      final VdxEntityInfo info = inspector.getInfo(entityClass);
//
//      if (info.getObjs() == null)
//      {
//         loadEntities(info);
//         if (info.getObjs() == null)
//            return null;
//      }
//
//      @SuppressWarnings("unchecked")
//      T result = (T) info.getIds().get(id.toString());
//
//      return result;
      return null;
   }

   /**
    * Clear all parameter values of the device. Should be called when the program
    * is changed.
    */
   public void clearDeviceParameterValues()
   {
      parameterValues.clear();
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

      if (catalogEntry == null && oo.catalogEntry != null)
         return false;

      if (program == null && oo.program != null)
         return false;

      return id == oo.id && address == oo.address
            && (catalogEntry == oo.catalogEntry || catalogEntry.equals(oo.catalogEntry))
            && (program == oo.program || program.equals(oo.program));
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
      final StringBuilder sb = new StringBuilder();
      sb.append(getPhysicalAddress().toString()).append(" ").append(catalogEntry).append(" ").append(program);
      return sb.toString();
   }
}
