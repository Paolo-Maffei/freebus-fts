package org.freebus.fts.project;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.freebus.fts.persistence.vdx.VdxEntity;
import org.freebus.fts.persistence.vdx.VdxField;
import org.freebus.fts.products.Parameter;

/**
 * The parameter value of a {@link Device} device.
 * In ETS the table is named "device_parameter".
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

   @VdxField(name = "parameter_value_long")
   @Column(name = "value_long")
   private int longValue;

   @VdxField(name = "parameter_value_string")
   @Column(name = "value_string")
   private String stringValue;

   @VdxField(name = "parameter_value_double")
   @Column(name = "value_double")
   private double doubleValue;

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
    * @param parameter the parameter to set
    */
   public void setParameter(Parameter parameter)
   {
      this.parameter = parameter;
   }

   /**
    * @return the longValue
    */
   public int getLongValue()
   {
      return longValue;
   }

   /**
    * @param longValue the longValue to set
    */
   public void setLongValue(int longValue)
   {
      this.longValue = longValue;
   }

   /**
    * @return the stringValue
    */
   public String getStringValue()
   {
      return stringValue;
   }

   /**
    * @param stringValue the stringValue to set
    */
   public void setStringValue(String stringValue)
   {
      this.stringValue = stringValue;
   }

   /**
    * @return the doubleValue
    */
   public double getDoubleValue()
   {
      return doubleValue;
   }

   /**
    * @param doubleValue the doubleValue to set
    */
   public void setDoubleValue(double doubleValue)
   {
      this.doubleValue = doubleValue;
   }
}
