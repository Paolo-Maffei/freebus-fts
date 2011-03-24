package org.freebus.fts.products;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.interfaces.Named;

/**
 * An application program.
 * 
 * Device type and program version identify the application program.
 */
@Entity
@Table(name = "application_program")
public class Program implements Named
{
   @Id
   @TableGenerator(name = "Program", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "Program")
   @Column(name = "program_id", nullable = false)
   private int id;

   @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
   @JoinColumn(name = "mask_id", nullable = false)
   private Mask mask;

   @Column(name = "program_name", nullable = false, length = 50)
   private String name = "";

   @Column(name = "program_version", length = 5)
   private String version;

   @Column(name = "linkable")
   private boolean linkable;

   @Column(name = "device_type")
   private int deviceType;

   @Column(name = "pei_type")
   private int peiType;

   @Column(name = "address_tab_size")
   private int addrTabSize;

   @Column(name = "assoctab_address")
   private int assocTabAddr;

   @Column(name = "assoctab_size")
   private int assocTabSize;

   @Column(name = "commstab_address")
   private int commsTabAddr;

   @Column(name = "commstab_size")
   private int commsTabSize;

   @Column(name = "program_serial_number", length = 20)
   private String serial;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "manufacturer_id", nullable = false)
   private Manufacturer manufacturer;

   @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @JoinColumn(name = "program_id")
   private Set<Parameter> parameters;

   @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @JoinColumn(name = "program_id")
   private Set<ParameterType> parameterTypes;

   @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @JoinColumn(name = "program_id")
   private Set<CommunicationObject> communicationObjects = new HashSet<CommunicationObject>();

   @Lob
   @Column(name = "eeprom_data")
   private byte[] eepromData;

   @Column(name = "dynamic_management")
   private boolean dynamicManagement;

   @Column(name = "program_type")
   private int programType;

   @Column(name = "ram_size")
   private int ramSize;

   @Column(name = "program_style")
   private int programStyle;

   @Column(name = "is_polling_master")
   private boolean pollingMaster;

   @Column(name = "number_of_polling_groups")
   private int numPollingGroups;

   @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @JoinColumn(name = "program_id")
   private final Set<S19Block> s19Blocks = new HashSet<S19Block>();

   @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "program")
   private ProgramDescription description;

   /**
    * Create an empty program object.
    */
   public Program()
   {
   }

   /**
    * Create a program object.
    * 
    * @param id - the database ID of the object.
    * @param name - the name of the object.
    * @param manufacturer - the manufacturer.
    * @param mask - the mask.
    */
   public Program(int id, String name, Manufacturer manufacturer, Mask mask)
   {
      this.id = id;
      this.name = name;
      this.manufacturer = manufacturer;
      this.mask = mask;
   }

   /**
    * @return the program id
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the program id.
    * 
    * @param id - the id to set.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the mask.
    */
   public Mask getMask()
   {
      return mask;
   }

   /**
    * Set the mask.
    * 
    * @param mask - the mask to set.
    */
   public void setMask(Mask mask)
   {
      this.mask = mask;
   }

   /**
    * @return the name of the program.
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the program.
    * 
    * @param name - the name to set.
    */
   public void setName(String name)
   {
      this.name = name == null ? "" : name;
   }

   /**
    * Get the version of the program. The name is a bit misleading,
    * as higher version numbers do not mean newer revisions of the
    * program. The version is used to distinguish different application
    * program types for the same physical device.
    * 
    * @return The version
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * Set the version. See {@link #getVersion()} for details about
    * a version.
    * 
    * @param version - the version to set.
    */
   public void setVersion(String version)
   {
      this.version = version;
   }

   /**
    * @return the linkable flag.
    */
   public boolean isLinkable()
   {
      return linkable;
   }

   /**
    * @param linkable the linkable to set
    */
   public void setLinkable(boolean linkable)
   {
      this.linkable = linkable;
   }

   /**
    * The device type. Is written to the KNX device also.
    * 
    * @return the device type.
    */
   public int getDeviceType()
   {
      return deviceType;
   }

   /**
    * Set the device type.
    * 
    * @param deviceType - the device type to set.
    */
   public void setDeviceType(int deviceType)
   {
      this.deviceType = deviceType;
   }

   /**
    * @return the type of the physical external interface (PEI).
    */
   public int getPeiType()
   {
      return peiType;
   }

   /**
    * Set the type of the physical external interface (PEI).
    * 
    * @param peiType - the PEI type to set.
    */
   public void setPeiType(int peiType)
   {
      this.peiType = peiType;
   }

   /**
    * @return the address-tab size.
    */
   public int getAddrTabSize()
   {
      return addrTabSize;
   }

   /**
    * Set the address-tab size.
    * 
    * @param addrTabSize - the address-tab size to set.
    */
   public void setAddrTabSize(int addrTabSize)
   {
      this.addrTabSize = addrTabSize;
   }

   /**
    * @return the association-tab address.
    */
   public int getAssocTabAddr()
   {
      return assocTabAddr;
   }

   /**
    * Set the association-tab address.
    * 
    * @param assocTabAddr - the association-tab address to set.
    */
   public void setAssocTabAddr(int assocTabAddr)
   {
      this.assocTabAddr = assocTabAddr;
   }

   /**
    * @return the association-tab size.
    */
   public int getAssocTabSize()
   {
      return assocTabSize;
   }

   /**
    * Set the association-tab size.
    * 
    * @param assocTabSize - the association-tab size.
    */
   public void setAssocTabSize(int assocTabSize)
   {
      this.assocTabSize = assocTabSize;
   }

   /**
    * @return The address of the communications table.
    */
   public int getCommsTabAddr()
   {
      return commsTabAddr;
   }

   /**
    * Set the address of the communications table.
    * 
    * @param commsTabAddr - the address to set.
    */
   public void setCommsTabAddr(int commsTabAddr)
   {
      this.commsTabAddr = commsTabAddr;
   }

   /**
    * @return The size of the communications table.
    */
   public int getCommsTabSize()
   {
      return commsTabSize;
   }

   /**
    * Set the size of the communications table.
    * 
    * @param commsTabSize - the size to set
    */
   public void setCommsTabSize(int commsTabSize)
   {
      this.commsTabSize = commsTabSize;
   }

   /**
    * @return the serial
    */
   public String getSerial()
   {
      return serial;
   }

   /**
    * @param serial the serial to set
    */
   public void setSerial(String serial)
   {
      this.serial = serial;
   }

   /**
    * @return the manufacturer.
    */
   public Manufacturer getManufacturer()
   {
      return manufacturer;
   }

   /**
    * Set the manufacturer.
    * 
    * @param manufacturer - the manufacturer to set.
    */
   public void setManufacturer(Manufacturer manufacturer)
   {
      this.manufacturer = manufacturer;
   }

   /**
    * Get a parameter by parameter-id.
    * 
    * @param id - the parameter id
    * 
    * @return the parameter, or null if not found.
    */
   public Parameter getParameter(int id)
   {
      if (parameters == null)
         return null;

      for (final Parameter param : parameters)
      {
         if (param.getId() == id)
            return param;
      }

      return null;
   }

   /**
    * Add a parameter to the program.
    * 
    * @param param - the parameter to add.
    */
   public void addParameter(Parameter param)
   {
      if (parameters == null)
         parameters = new HashSet<Parameter>();

      parameters.add(param);
      param.setProgram(this);
   }

   /**
    * Remove a parameter from the program.
    * 
    * @param param - the parameter to remove.
    */
   public void removeParameter(Parameter param)
   {
      if (parameters != null)
         parameters.remove(param);

      param.setProgram(null);
   }

   /**
    * Remove all parameter from the program.
    */
   public void removeAllParameters()
   {
      if (parameters != null)
      {
         for (final Parameter param : parameters)
            param.setProgram(null);

         parameters.clear();
      }
   }

   /**
    * Set the parameters of the program.
    * 
    * @param parameters - the parameters to set.
    */
   public void setParameters(Set<Parameter> parameters)
   {
      this.parameters = parameters;
   }

   /**
    * @return the parameters of the program.
    */
   public Set<Parameter> getParameters()
   {
      return parameters;
   }

   /**
    * Set the communication objects of the program.
    * 
    * @param communicationObjects the set of communication objects.
    */
   public void setCommunicationObjects(Set<CommunicationObject> communicationObjects)
   {
      this.communicationObjects = communicationObjects;
   }

   /**
    * @return the communication objects of the program.
    */
   public Set<CommunicationObject> getCommunicationObjects()
   {
      return communicationObjects;
   }

   /**
    * @return the eepromData
    */
   public byte[] getEepromData()
   {
      return eepromData;
   }

   /**
    * @param eepromData the eepromData to set
    */
   public void setEepromData(byte[] eepromData)
   {
      this.eepromData = eepromData;
   }

   /**
    * @return the dynamicManagement
    */
   public boolean isDynamicManagement()
   {
      return dynamicManagement;
   }

   /**
    * @param dynamicManagement the dynamicManagement to set
    */
   public void setDynamicManagement(boolean dynamicManagement)
   {
      this.dynamicManagement = dynamicManagement;
   }

   /**
    * Get the program type. The program type is a 16 bit number that is unique
    * for a specific manufacturer. It can be used to identify the program that
    * is running in a BCU.
    * 
    * @return The program type
    */
   public int getProgramType()
   {
      return programType;
   }

   /**
    * @param programType the programType to set
    */
   public void setProgramType(int programType)
   {
      this.programType = programType;
   }

   /**
    * @return the ramSize
    */
   public int getRamSize()
   {
      return ramSize;
   }

   /**
    * @param ramSize the ramSize to set
    */
   public void setRamSize(int ramSize)
   {
      this.ramSize = ramSize;
   }

   /**
    * @return the programStyle
    */
   public int getProgramStyle()
   {
      return programStyle;
   }

   /**
    * @param programStyle the programStyle to set
    */
   public void setProgramStyle(int programStyle)
   {
      this.programStyle = programStyle;
   }

   /**
    * @return the pollingMaster
    */
   public boolean isPollingMaster()
   {
      return pollingMaster;
   }

   /**
    * @param pollingMaster the pollingMaster to set
    */
   public void setPollingMaster(boolean pollingMaster)
   {
      this.pollingMaster = pollingMaster;
   }

   /**
    * @return the numPollingGroups
    */
   public int getNumPollingGroups()
   {
      return numPollingGroups;
   }

   /**
    * @param numPollingGroups the numPollingGroups to set
    */
   public void setNumPollingGroups(int numPollingGroups)
   {
      this.numPollingGroups = numPollingGroups;
   }

   /**
    * @param parameterTypes the parameterTypes to set
    */
   public void setParameterTypes(Set<ParameterType> parameterTypes)
   {
      this.parameterTypes = parameterTypes;
   }

   /**
    * @return the parameterTypes
    */
   public Set<ParameterType> getParameterTypes()
   {
      return parameterTypes;
   }

   /**
    * Set the program description.
    * 
    * @param description - the program description to set.
    */
   public void setDescription(ProgramDescription description)
   {
      this.description = description;
   }

   /**
    * @return The program description.
    */
   public ProgramDescription getDescription()
   {
      return description;
   }

   /**
    * @return the S19 Blocks
    */
   public Set<S19Block> getS19Blocks()
   {
      return s19Blocks;
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

      if (!(o instanceof Program))
         return false;

      final Program oo = (Program) o;

      return id == oo.id && peiType == oo.peiType && deviceType == oo.deviceType
            && (version == null ? oo.version == null : version.equals(oo.version))
            && (mask == null ? oo.mask == null : mask.equals(oo.mask));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return name;
   }
}
