package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * An application program.
 */
@Entity
@Table(name = "application_program", uniqueConstraints = @UniqueConstraint(columnNames = "program_id"))
public class Program
{
   @Id
   @Column(name = "program_id", columnDefinition = "INT", unique = true, nullable = false)
   private int id;

   @Column(name = "mask_id", columnDefinition = "INT")
   private int maskId;

   @Column(name = "program_name", length = 50)
   private String name;

   @Column(name = "program_version", length = 5)
   private String version;

   @Column(name = "linkable")
   private boolean linkable;

   @Column(name = "device_type", columnDefinition = "INT")
   private int deviceType;

   @Column(name = "pei_type", columnDefinition = "INT")
   private int peiType;

   @Column(name = "address_tab_size", columnDefinition = "INT")
   private int addrTabSize;

   @Column(name = "assoctab_address", columnDefinition = "INT")
   private int assocTabAddr;

   @Column(name = "assoctab_size", columnDefinition = "INT")
   private int assocTabSize;

   @Column(name = "commstab_address", columnDefinition = "INT")
   private int commsTabAddr;

   @Column(name = "commstab_size", columnDefinition = "INT")
   private int commsTabSize;

   @Column(name = "program_serial_number", length = 20)
   private String serial;

   @Column(name = "manufacturer_id", columnDefinition = "INT")
   private int manufacturerId;

   @Column(name = "eeprom_data", columnDefinition = "")
   private String eepromData;

   @Column(name = "dynamic_management", columnDefinition = "BOOLEAN")
   private boolean dynamicManagement;

   @Column(name = "program_type", columnDefinition = "SMALLINT")
   private int programType;

   @Column(name = "ram_size", columnDefinition = "INT")
   private int ramSize;

   @Column(name = "program_style", columnDefinition = "SMALLINT")
   private int programStyle;

   @Column(name = "is_polling_master")
   private boolean pollingMaster;

   @Column(name = "number_of_polling_groups", columnDefinition = "SMALLINT")
   private int numPollingGroups;

   /**
    * @return the program id
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the program id.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the mask id.
    */
   public int getMaskId()
   {
      return maskId;
   }

   /**
    * Set the mask id.
    */
   public void setMaskId(int maskId)
   {
      this.maskId = maskId;
   }

   /**
    * @return the name of the program.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the program.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the version
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * Set the version.
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
    * @return the device type.
    */
   public int getDeviceType()
   {
      return deviceType;
   }

   /**
    * Set the device type.
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
    */
   public void setAssocTabSize(int assocTabSize)
   {
      this.assocTabSize = assocTabSize;
   }

   /**
    * @return the commsTabAddr
    */
   public int getCommsTabAddr()
   {
      return commsTabAddr;
   }

   /**
    * @param commsTabAddr the commsTabAddr to set
    */
   public void setCommsTabAddr(int commsTabAddr)
   {
      this.commsTabAddr = commsTabAddr;
   }

   /**
    * @return the commsTabSize
    */
   public int getCommsTabSize()
   {
      return commsTabSize;
   }

   /**
    * @param commsTabSize the commsTabSize to set
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
    * @return the manufacturerId
    */
   public int getManufacturerId()
   {
      return manufacturerId;
   }

   /**
    * @param manufacturerId the manufacturerId to set
    */
   public void setManufacturerId(int manufacturerId)
   {
      this.manufacturerId = manufacturerId;
   }

   /**
    * @return the eepromData
    */
   public String getEepromData()
   {
      return eepromData;
   }

   /**
    * @param eepromData the eepromData to set
    */
   public void setEepromData(String eepromData)
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
    * @return the programType
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
}
