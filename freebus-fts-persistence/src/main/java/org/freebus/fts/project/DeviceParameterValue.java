package org.freebus.fts.project;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.freebus.fts.persistence.vdx.VdxEntity;
import org.freebus.fts.products.Parameter;

/**
 * The parameter value of a specific {@link Device} device.
 */
@Entity
@VdxEntity(name = "device_parameter")
@Table(name = "device_parameter_value")
public class DeviceParameterValue
{
   @Id
   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "device_id", nullable = false)
   private Device device;

   @Id
   @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "parameter_id", nullable = false)
   private Parameter parameter;

   @Transient
   private Object value;

   @Column(name = "parameter_value", nullable = false)
   private String stringValue;

   /**
    * Create an empty device parameter-value object.
    */
   public DeviceParameterValue()
   {
   }

   /**
    * Create an initialized device parameter-value object.
    */
   public DeviceParameterValue(Device device, Parameter parameter, Object value)
   {
      this.device = device;
      this.parameter = parameter;
      this.value = value;
   }

   /**
    * @return the device
    */
   public Device getDevice()
   {
      return device;
   }

   /**
    * @param device the device to set
    */
   public void setDevice(Device device)
   {
      this.device = device;
   }

   /**
    * @return the parameter
    */
   public Parameter getParameter()
   {
      return parameter;
   }

   /**
    * @return the value as string.
    */
   public String getValue()
   {
      if (stringValue == null) return "";
      if (value == null) value = stringValue;
      return value.toString();
   }

   /**
    * @return the value as integer. Returns zero if the value is not set.
    */
   public Integer getIntValue()
   {
      if (stringValue == null) return 0;
      if (value == null) value = Integer.parseInt(stringValue);
      return (Integer) value;
   }

   /**
    * Set the value.
    */
   public void setValue(Object o)
   {
      value = o;
   }

   /**
    * Prepare the value for persisting.
    */
   @PrePersist
   protected void prePersist()
   {
      if (value == null) stringValue = "";
      else stringValue = value.toString();
   }
}
