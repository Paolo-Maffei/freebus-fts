package org.freebus.fts.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.freebus.fts.products.Parameter;

/**
 * The parameter of a specific {@link Device} device, including the current
 * parameter value. The parameter values are set by the parameter editor when
 * the user parameterizes a device.
 */
@Entity
@Table(name = "device_parameter")
public class DeviceParameter
{
   @Id
   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "device_id", nullable = false)
   private Device device;

   @Id
   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "parameter_id", nullable = false)
   private Parameter parameter;

   @Transient
   private Object value;

   // The string value is only used for persistence
   @Column(name = "parameter_value")
   private String stringValue;

   /**
    * Create an empty device parameter object.
    */
   public DeviceParameter()
   {
   }

   /**
    * Create an initialized device parameter object.
    */
   public DeviceParameter(Device device, Parameter parameter, Object value)
   {
      this.device = device;
      this.parameter = parameter;
      this.value = value;
   }

   /**
    * An internal constructor, mainly for unit tests
    */
   DeviceParameter(String stringValue)
   {
      this.stringValue = stringValue;
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
    * @return The parameter
    */
   public Parameter getParameter()
   {
      return parameter;
   }

   /**
    * @return The parent device parameter, or null if the parameter has no parent.
    */
   public DeviceParameter getParent()
   {
      if (parameter == null)
         return null;

      final Parameter parentParam = parameter.getParent();
      if (parentParam == null)
         return null;

      return  device.getDeviceParameter(parentParam);
   }

   /**
    * @return the value as string.
    */
   public String getValue()
   {
      if (value == null)
      {
         if (stringValue == null)
            return null;

         value = stringValue;
      }

      return value.toString();
   }

   /**
    * @return the value as integer. Returns zero if the value is not set.
    *
    * @throws ClassCastException if the value cannot be cast to an
    *            {@link Integer}.
    */
   public Integer getIntValue()
   {
      if (value != null)
         return (Integer) value;

      if (stringValue == null)
         return null;

      value = stringValue.isEmpty() ? 0 : Integer.parseInt(stringValue);
      return (Integer) value;
   }

   /**
    * Set the value.
    */
   public void setValue(Object o)
   {
      value = o;
      stringValue = null;
   }

   /**
    * Test if the parameter is visible.
    *
    * @return true if the parameter is visible.
    */
   public boolean isVisible()
   {
      if (!isEnabled())
         return false;

      return getParameter().getHighAccess() != 0;
   }

   /**
    * @return True if the parameter is enabled.
    */
   public boolean isEnabled()
   {
      final DeviceParameter parent = getParent();

      if (parent == null)
         return true;

      if (!parent.isEnabled())
         return false;

      final Integer expectedParentValue = parameter.getParentValue();
      if (expectedParentValue == null)
         return true;

      return expectedParentValue.equals(parent.getIntValue());
   }

   /**
    * Test if the parameter shall be stored in the eeprom.
    *
    * @return true if the parameter shall be stored in the eeprom.
    */
   public boolean isUsed()
   {
      // TODO verify the if-condition ... it is only a guess!
      if (parameter.getLowAccess() == 0 && parameter.getHighAccess() == 0)
         return false;

      final DeviceParameter parent = getParent();
      if (parent == null)
         return true;

      if (!parent.isUsed())
         return false;

      final Integer expectedParentValue = parameter.getParentValue();
      if (expectedParentValue == null)
         return true;

      return expectedParentValue.equals(parent.getIntValue());
   }

   /**
    * Prepare the value for persisting.
    */
   @PrePersist
   protected void prePersist()
   {
      if (stringValue == null)
      {
         if (value == null)
            stringValue = "";
         else stringValue = value.toString();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return parameter == null ? 0 : parameter.hashCode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return "#" + (parameter == null ? -1 : parameter.getId()) + " value " + value;
   }
}
