package org.freebus.fts.project;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.freebus.fts.persistence.vdx.VdxEntity;

/**
 * Details about the programming of a physical KNX device.
 */
@Entity
@Table(name = "device_programming")
@VdxEntity(name = "device_programming_non_vd")
public class DeviceProgramming
{
   @Id
   @JoinColumn(name = "device_id", nullable = false)
   @OneToOne(optional = false)
   private Device device;

   @Column(name = "last_modified")
   @Temporal(TemporalType.TIMESTAMP)
   private Date lastModified;

   @Column(name = "last_upload")
   @Temporal(TemporalType.TIMESTAMP)
   private Date lastUpload;

   @Column(name = "communication_valid", nullable = false)
   private boolean communicationValid;

   @Column(name = "parameters_valid", nullable = false)
   private boolean parametersValid;

   @Column(name = "physical_address_valid", nullable = false)
   private boolean physicalAddressValid;

   @Column(name = "program", nullable = false)
   private boolean programValid;

   /**
    * Create a device programming details object.
    */
   public DeviceProgramming()
   {
   }

   /**
    * Create a program description object.
    * 
    * @param device - the device to which the programming object belongs.
    */
   public DeviceProgramming(Device device)
   {
      this.device = device;
   }

   /**
    * @return The device to which the object belongs.
    */
   public Device getDevice()
   {
      return device;
   }

   /**
    * Set the device to which the object belongs.
    * 
    * @param device - the device to set.
    */
   public void setDevice(Device device)
   {
      this.device = device;
   }

   /**
    * Get the timestamp when the {@link #getDevice() device} was last modified.
    * 
    * @return the last modified timestamp
    */
   public Date getLastModified()
   {
      return lastModified;
   }

   /**
    * Set the timestamp when the {@link #getDevice() device} was last modified.
    * 
    * @param lastModified - the last modified timestamp to set
    */
   public void setLastModified(Date lastModified)
   {
      this.lastModified = lastModified;
   }

   /**
    * Set the timestamp when the {@link #getDevice() device} was last modified
    * to now.
    */
   public void setLastModifiedNow()
   {
      this.lastModified = new Date();
   }

   /**
    * Get the timestamp when the device was last programmed. This does not imply
    * that everything in the device is up to date.
    * 
    * @return the last upload timestamp.
    */
   public Date getLastUpload()
   {
      return lastUpload;
   }

   /**
    * Set the timestamp when the {@link #getDevice() device} was last programmed
    * to now.
    */
   public void setLastUploadNow()
   {
      this.lastUpload = new Date();
   }

   /**
    * Set the timestamp when the device was last programmed.
    * 
    * @param lastUpload - the last upload timestamp.
    */
   public void setLastUpload(Date lastUpload)
   {
      this.lastUpload = lastUpload;
   }

   /**
    * Test if everything in the device is up to date.
    */
   public boolean isValid()
   {
      return communicationValid && parametersValid && physicalAddressValid && programValid;
   }

   /**
    * Test if the communication objects in the device are up to date.
    * 
    * @return the communication valid flag.
    */
   public boolean isCommunicationValid()
   {
      return communicationValid;
   }

   /**
    * Set if the communication objects in the device are up to date.
    * 
    * @param communicationValid - the communication valid flag to set
    */
   public void setCommunicationValid(boolean communicationValid)
   {
      this.communicationValid = communicationValid;
   }

   /**
    * Test if the parameters in the device are up to date.
    * 
    * @return the parameters valid flag.
    */
   public boolean isParametersValid()
   {
      return parametersValid;
   }

   /**
    * Set if the parameters in the device are up to date.
    * 
    * @param parametersValid - the parameters valid flag to set
    */
   public void setParametersValid(boolean parametersValid)
   {
      this.parametersValid = parametersValid;
   }

   /**
    * Test if the physical address of the device is up to date.
    * 
    * @return the physical address valid flag.
    */
   public boolean isPhysicalAddressValid()
   {
      return physicalAddressValid;
   }

   /**
    * Set if the physical address of the device is up to date.
    * 
    * @param physicalAddressValid - the physical address valid flag to set.
    */
   public void setPhysicalAddressValid(boolean physicalAddressValid)
   {
      this.physicalAddressValid = physicalAddressValid;
   }

   /**
    * Test if the application program of the device is up to date.
    * 
    * @return the program valid flag.
    */
   public boolean isProgramValid()
   {
      return programValid;
   }

   /**
    * Set if the application program of the device is up to date.
    * 
    * @param programValid - the program valid flag to set.
    */
   public void setProgramValid(boolean programValid)
   {
      this.programValid = programValid;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return device == null ? 0 : device.getId();
   }
}
