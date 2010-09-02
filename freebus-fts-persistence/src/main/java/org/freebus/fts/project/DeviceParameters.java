package org.freebus.fts.project;

import java.lang.ref.WeakReference;
import java.util.Map;

import org.freebus.fts.products.Parameter;

/**
 * A container that manages all parameter values of a {@link Device}.
 * 
 * @see Device#getParameters()
 */
public class DeviceParameters
{
   private final WeakReference<Device> device;

   /**
    * Use {@link Device#getParameters()} to access this object.
    * 
    * @param device - the owning device.
    */
   DeviceParameters(Device device)
   {
      this.device = new WeakReference<Device>(device);
   }

   /**
    * Clear all parameter values
    */
   public void clear()
   {
      final Map<Parameter, DeviceParameterValue> values = getParameterValues();
      if (values != null)
         values.clear();
   }

   /**
    * Returns the string value for the parameter <code>param</code>. If no value
    * is yet set, the parameter's default value is returned.
    * 
    * @param param the parameter whose value is requested.
    * @return the parameter's value.
    */
   public String getValue(final Parameter param)
   {
      final Map<Parameter, DeviceParameterValue> values = getParameterValues();

      final DeviceParameterValue val = values != null ? values.get(param) : null;
      if (val == null)
         return param.getDefaultString();

      return val.getValue();
   }

   /**
    * Returns the integer value for the parameter <code>param</code>. If no
    * value is yet set, the parameter's default value is returned.
    * 
    * @param param the parameter whose value is requested.
    * @return the parameter's value.
    */
   public int getIntValue(final Parameter param)
   {
      final Map<Parameter, DeviceParameterValue> values = getParameterValues();

      if (values == null)
         return param.getDefaultLong();

      final DeviceParameterValue val = values.get(param);
      if (val == null)
         return param.getDefaultLong();

      final Integer ival = val.getIntValue();
      return ival == null ? 0 : ival;
   }

   /**
    * Set the value of a parameter to an integer.
    * 
    * @param param - the parameter to set.
    * @param value - the value to set.
    */
   public void setValue(Parameter param, int value)
   {
      setValue(param, (Object) value);
   }

   /**
    * Set the value of a parameter to a string.
    * 
    * @param param - the parameter to set.
    * @param value - the value to set.
    */
   public void setValue(Parameter param, String value)
   {
      setValue(param, (Object) value);
   }

   /**
    * Set the value of a parameter to an object.
    * 
    * @param param - the parameter to set.
    * @param value - the value to set.
    */
   public void setValue(Parameter param, Object value)
   {
      final Map<Parameter, DeviceParameterValue> values = getParameterValues();
      if (values != null)
         values.put(param, new DeviceParameterValue(device.get(), param, value));
   }

   /**
    * Test if a parameter is visible.
    * 
    * @param param - the parameter to test.
    * @return true if the parameter is visible.
    */
   public boolean isVisible(final Parameter param)
   {
      if (param.getLowAccess() == 0)
         return false;

      final Map<Parameter, DeviceParameterValue> values = getParameterValues();
      if (values == null)
         return false;

      final DeviceParameterValue val = values.get(param);
      return val != null && val.isVisible();
   }

   /**
    * Set the visibility of a parameter. A parameter-value with value null is
    * created for the parameter, if the parameter has no value.
    * 
    * @param param - the parameter for which the visibility is set.
    * @param visible - the visibility indicator.
    */
   public void setVisible(final Parameter param, boolean visible)
   {
      final Map<Parameter, DeviceParameterValue> values = getParameterValues();
      if (values == null)
         return;

      DeviceParameterValue val = values.get(param);
      if (val == null)
      {
         val = new DeviceParameterValue(getDevice(), param, null);
         values.put(param, val);
      }

      val.setVisible(visible);
   }

   /**
    * @return The {@link Device device} to which this object belongs. May be
    *         null (if the device is already destroyed).
    */
   public Device getDevice()
   {
      return device.get();
   }

   /**
    * @return The map of parameter values, or null if the device is null.
    */
   private Map<Parameter, DeviceParameterValue> getParameterValues()
   {
      final Device dev = device.get();
      if (dev == null)
         return null;
      
      return dev.getParameterValues();
   }
}
