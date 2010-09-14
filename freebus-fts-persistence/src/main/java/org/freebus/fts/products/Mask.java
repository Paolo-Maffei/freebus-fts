package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * The mask describes the interface of a device, where the specific addresses
 * are located (like RAM, EEPROM, address table), and such.
 */
@Entity
@Table(name = "mask")
public class Mask
{
   @Id
   @TableGenerator(name = "Mask", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "Mask")
   @Column(name = "mask_id", nullable = false)
   private int id;

   @Column(name = "mask_version", nullable = false)
   private int version;

   @Column(name = "mask_version_name")
   private String maskVersionName;

   @Column(name = "user_ram_start", nullable = false)
   private int userRamStart;

   @Column(name = "user_ram_end", nullable = false)
   private int userRamEnd;

   @Column(name = "user_eeprom_start", nullable = false)
   private int userEepromStart;

   @Column(name = "user_eeprom_end", nullable = false)
   private int userEepromEnd;

   @Column(name = "run_error_address", nullable = false)
   private int runErrorAddress;

   @Column(name = "address_tab_address", nullable = false)
   private int addressTabAddress;

   @Column(name = "assoctabptr_address", nullable = false)
   private int assocTabPtrAddress;

   @Column(name = "commstabptr_address", nullable = false)
   private int commsTabPtrAddress;

   @Column(name = "manufacturer_data_address", nullable = false)
   private int manufacturerDataAddress;

   @Column(name = "manufacturer_data_size", nullable = false)
   private int manufacturerDataSize;

   @Column(name = "manufacturer_id_address", nullable = false)
   private int manufacturerIdAddress;

   @Column(name = "routecnt_address", nullable = false)
   private int routeCountAddress;

   @Column(name = "manufacturer_id_protected", nullable = false)
   private boolean manufacturerIdProtected;

   @Lob
   @Column(name = "mask_eeprom_data")
   private byte[] maskEepromData;

   @Column(name = "address_tab_lcs")
   private Integer addressTabLCS;

   @Column(name = "assoc_tab_lcs")
   private Integer assocTabLCS;

   @Column(name = "application_program_lcs")
   private Integer applicationProgramLCS;

   @Column(name = "pei_program_lcs")
   private Integer peiProgramLCS;

   @Column(name = "load_control_address")
   private Integer loadControlAddress;

   @Column(name = "run_control_address")
   private Integer runControlAddress;

   @Column(name = "external_memory_start")
   private Integer externalMemoryStart;

   @Column(name = "external_memory_end")
   private Integer externalMemoryEnd;

   @Column(name = "application_program_rcs")
   private Integer applicationProgramRCS;

   @Column(name = "pei_program_rcs")
   private Integer peiProgramRCS;

   @Column(name = "port_a_ddr")
   private Integer portA_ddr;

   @Column(name = "port_address_protected", nullable = false)
   private boolean portAddressProtected;

   @Column(name = "medium_type_number")
   private Integer mediumTypeNumber;

   @Column(name = "medium_type_number2")
   private Integer mediumTypeNumber2;

   @Column(name = "bcu_type_number")
   private Integer bcuTypeNumber;

   /**
    * @return the id
    */
   public int getId()
   {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the version
    */
   public int getVersion()
   {
      return version;
   }

   /**
    * @param version the version to set
    */
   public void setVersion(int version)
   {
      this.version = version;
   }

   /**
    * @return the maskVersionName
    */
   public String getMaskVersionName()
   {
      return maskVersionName;
   }

   /**
    * @param maskVersionName the maskVersionName to set
    */
   public void setMaskVersionName(String maskVersionName)
   {
      this.maskVersionName = maskVersionName;
   }

   /**
    * @return the userRamStart
    */
   public int getUserRamStart()
   {
      return userRamStart;
   }

   /**
    * @param userRamStart the userRamStart to set
    */
   public void setUserRamStart(int userRamStart)
   {
      this.userRamStart = userRamStart;
   }

   /**
    * @return the userRamEnd
    */
   public int getUserRamEnd()
   {
      return userRamEnd;
   }

   /**
    * @param userRamEnd the userRamEnd to set
    */
   public void setUserRamEnd(int userRamEnd)
   {
      this.userRamEnd = userRamEnd;
   }

   /**
    * @return the userEepromStart
    */
   public int getUserEepromStart()
   {
      return userEepromStart;
   }

   /**
    * @param userEepromStart the userEepromStart to set
    */
   public void setUserEepromStart(int userEepromStart)
   {
      this.userEepromStart = userEepromStart;
   }

   /**
    * @return the userEepromEnd
    */
   public int getUserEepromEnd()
   {
      return userEepromEnd;
   }

   /**
    * @param userEepromEnd the userEepromEnd to set
    */
   public void setUserEepromEnd(int userEepromEnd)
   {
      this.userEepromEnd = userEepromEnd;
   }

   /**
    * @return the runErrorAddress
    */
   public int getRunErrorAddress()
   {
      return runErrorAddress;
   }

   /**
    * @param runErrorAddress the runErrorAddress to set
    */
   public void setRunErrorAddress(int runErrorAddress)
   {
      this.runErrorAddress = runErrorAddress;
   }

   /**
    * @return the addressTabAddress
    */
   public int getAddressTabAddress()
   {
      return addressTabAddress;
   }

   /**
    * @param addressTabAddress the addressTabAddress to set
    */
   public void setAddressTabAddress(int addressTabAddress)
   {
      this.addressTabAddress = addressTabAddress;
   }

   /**
    * @return the assocTabPtrAddress
    */
   public int getAssocTabPtrAddress()
   {
      return assocTabPtrAddress;
   }

   /**
    * @param assocTabPtrAddress the assocTabPtrAddress to set
    */
   public void setAssocTabPtrAddress(int assocTabPtrAddress)
   {
      this.assocTabPtrAddress = assocTabPtrAddress;
   }

   /**
    * @return the commsTabPtrAddress
    */
   public int getCommsTabPtrAddress()
   {
      return commsTabPtrAddress;
   }

   /**
    * @param commsTabPtrAddress the commsTabPtrAddress to set
    */
   public void setCommsTabPtrAddress(int commsTabPtrAddress)
   {
      this.commsTabPtrAddress = commsTabPtrAddress;
   }

   /**
    * @return the manufacturerDataAddress
    */
   public int getManufacturerDataAddress()
   {
      return manufacturerDataAddress;
   }

   /**
    * @param manufacturerDataAddress the manufacturerDataAddress to set
    */
   public void setManufacturerDataAddress(int manufacturerDataAddress)
   {
      this.manufacturerDataAddress = manufacturerDataAddress;
   }

   /**
    * @return the manufacturerDataSize
    */
   public int getManufacturerDataSize()
   {
      return manufacturerDataSize;
   }

   /**
    * @param manufacturerDataSize the manufacturerDataSize to set
    */
   public void setManufacturerDataSize(int manufacturerDataSize)
   {
      this.manufacturerDataSize = manufacturerDataSize;
   }

   /**
    * @return the manufacturerIdAddress
    */
   public int getManufacturerIdAddress()
   {
      return manufacturerIdAddress;
   }

   /**
    * @param manufacturerIdAddress the manufacturerIdAddress to set
    */
   public void setManufacturerIdAddress(int manufacturerIdAddress)
   {
      this.manufacturerIdAddress = manufacturerIdAddress;
   }

   /**
    * @return the routeCountAddress
    */
   public int getRouteCountAddress()
   {
      return routeCountAddress;
   }

   /**
    * @param routeCountAddress the routeCountAddress to set
    */
   public void setRouteCountAddress(int routeCountAddress)
   {
      this.routeCountAddress = routeCountAddress;
   }

   /**
    * @return the manufacturerIdProtected
    */
   public boolean isManufacturerIdProtected()
   {
      return manufacturerIdProtected;
   }

   /**
    * @param manufacturerIdProtected the manufacturerIdProtected to set
    */
   public void setManufacturerIdProtected(boolean manufacturerIdProtected)
   {
      this.manufacturerIdProtected = manufacturerIdProtected;
   }

   /**
    * @return the maskEepromData
    */
   public byte[] getMaskEepromData()
   {
      return maskEepromData;
   }

   /**
    * @param maskEepromData the maskEepromData to set
    */
   public void setMaskEepromData(byte[] maskEepromData)
   {
      this.maskEepromData = maskEepromData;
   }

   /**
    * @return the addressTabLCS
    */
   public Integer getAddressTabLCS()
   {
      return addressTabLCS;
   }

   /**
    * @param addressTabLCS the addressTabLCS to set
    */
   public void setAddressTabLCS(Integer addressTabLCS)
   {
      this.addressTabLCS = addressTabLCS;
   }

   /**
    * @return the assocTabLCS
    */
   public Integer getAssocTabLCS()
   {
      return assocTabLCS;
   }

   /**
    * @param assocTabLCS the assocTabLCS to set
    */
   public void setAssocTabLCS(Integer assocTabLCS)
   {
      this.assocTabLCS = assocTabLCS;
   }

   /**
    * @return the applicationProgramLCS
    */
   public Integer getApplicationProgramLCS()
   {
      return applicationProgramLCS;
   }

   /**
    * @param applicationProgramLCS the applicationProgramLCS to set
    */
   public void setApplicationProgramLCS(Integer applicationProgramLCS)
   {
      this.applicationProgramLCS = applicationProgramLCS;
   }

   /**
    * @return the peiProgramLCS
    */
   public Integer getPeiProgramLCS()
   {
      return peiProgramLCS;
   }

   /**
    * @param peiProgramLCS the peiProgramLCS to set
    */
   public void setPeiProgramLCS(Integer peiProgramLCS)
   {
      this.peiProgramLCS = peiProgramLCS;
   }

   /**
    * @return the loadControlAddress
    */
   public Integer getLoadControlAddress()
   {
      return loadControlAddress;
   }

   /**
    * @param loadControlAddress the loadControlAddress to set
    */
   public void setLoadControlAddress(Integer loadControlAddress)
   {
      this.loadControlAddress = loadControlAddress;
   }

   /**
    * @return the runControlAddress
    */
   public Integer getRunControlAddress()
   {
      return runControlAddress;
   }

   /**
    * @param runControlAddress the runControlAddress to set
    */
   public void setRunControlAddress(Integer runControlAddress)
   {
      this.runControlAddress = runControlAddress;
   }

   /**
    * @return the externalMemoryStart
    */
   public Integer getExternalMemoryStart()
   {
      return externalMemoryStart;
   }

   /**
    * @param externalMemoryStart the externalMemoryStart to set
    */
   public void setExternalMemoryStart(Integer externalMemoryStart)
   {
      this.externalMemoryStart = externalMemoryStart;
   }

   /**
    * @return the externalMemoryEnd
    */
   public Integer getExternalMemoryEnd()
   {
      return externalMemoryEnd;
   }

   /**
    * @param externalMemoryEnd the externalMemoryEnd to set
    */
   public void setExternalMemoryEnd(Integer externalMemoryEnd)
   {
      this.externalMemoryEnd = externalMemoryEnd;
   }

   /**
    * @return the applicationProgramRCS
    */
   public Integer getApplicationProgramRCS()
   {
      return applicationProgramRCS;
   }

   /**
    * @param applicationProgramRCS the applicationProgramRCS to set
    */
   public void setApplicationProgramRCS(Integer applicationProgramRCS)
   {
      this.applicationProgramRCS = applicationProgramRCS;
   }

   /**
    * @return the peiProgramRCS
    */
   public Integer getPeiProgramRCS()
   {
      return peiProgramRCS;
   }

   /**
    * @param peiProgramRCS the peiProgramRCS to set
    */
   public void setPeiProgramRCS(Integer peiProgramRCS)
   {
      this.peiProgramRCS = peiProgramRCS;
   }

   /**
    * @return the portA_ddr
    */
   public Integer getPortA_ddr()
   {
      return portA_ddr;
   }

   /**
    * @param portA_ddr the portA_ddr to set
    */
   public void setPortA_ddr(Integer portA_ddr)
   {
      this.portA_ddr = portA_ddr;
   }

   /**
    * @return the portAddressProtected
    */
   public boolean isPortAddressProtected()
   {
      return portAddressProtected;
   }

   /**
    * @param portAddressProtected the portAddressProtected to set
    */
   public void setPortAddressProtected(boolean portAddressProtected)
   {
      this.portAddressProtected = portAddressProtected;
   }

   /**
    * @return the mediumTypeNumber
    */
   public Integer getMediumTypeNumber()
   {
      return mediumTypeNumber;
   }

   /**
    * @param mediumTypeNumber the mediumTypeNumber to set
    */
   public void setMediumTypeNumber(Integer mediumTypeNumber)
   {
      this.mediumTypeNumber = mediumTypeNumber;
   }

   /**
    * @return the mediumTypeNumber2
    */
   public Integer getMediumTypeNumber2()
   {
      return mediumTypeNumber2;
   }

   /**
    * @param mediumTypeNumber2 the mediumTypeNumber2 to set
    */
   public void setMediumTypeNumber2(Integer mediumTypeNumber2)
   {
      this.mediumTypeNumber2 = mediumTypeNumber2;
   }

   /**
    * @return the bcuTypeNumber
    */
   public Integer getBcuTypeNumber()
   {
      return bcuTypeNumber;
   }

   /**
    * @param bcuTypeNumber the bcuTypeNumber to set
    */
   public void setBcuTypeNumber(Integer bcuTypeNumber)
   {
      this.bcuTypeNumber = bcuTypeNumber;
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
      if (!(o instanceof Mask))
         return false;
      final Mask oo = (Mask) o;
      return id == oo.id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return "mask #" + id + " version " + version;
   }
}
