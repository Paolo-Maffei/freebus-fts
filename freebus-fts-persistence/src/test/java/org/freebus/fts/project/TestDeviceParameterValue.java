package org.freebus.fts.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.freebus.fts.products.Parameter;
import org.junit.Test;

public class TestDeviceParameterValue
{
   @Test
   public final void testDeviceParameterValue()
   {
      final DeviceParameter paramValue = new DeviceParameter();
      assertNull(paramValue.getIntValue());
      assertNull(paramValue.getValue());
      assertNull(paramValue.getDevice());
   }

   @Test
   public final void testDeviceParameterValueDeviceParameterNull()
   {
      final Device device = new Device();
      final Parameter param = new Parameter();
      final DeviceParameter paramValue = new DeviceParameter(device, param, null);

      assertEquals(device, paramValue.getDevice());
      assertEquals(param, paramValue.getParameter());
      assertNull(paramValue.getIntValue());
      assertNull(paramValue.getValue());
   }

   @Test
   public final void testDeviceParameterValueDeviceParameterInt()
   {
      final Device device = new Device();
      final Parameter param = new Parameter();
      final int val = 12;
      final DeviceParameter paramValue = new DeviceParameter(device, param, val);

      assertEquals(device, paramValue.getDevice());
      assertEquals(param, paramValue.getParameter());
      assertEquals(Integer.valueOf(val), paramValue.getIntValue());
      assertEquals("12", paramValue.getValue());
   }

   @Test
   public final void testDeviceParameterValueDeviceParameterString()
   {
      final Device device = new Device();
      final Parameter param = new Parameter();
      final String val = "str";
      final DeviceParameter paramValue = new DeviceParameter(device, param, val);

      assertEquals(device, paramValue.getDevice());
      assertEquals(param, paramValue.getParameter());
      assertEquals("str", paramValue.getValue());
   }

   @Test
   public final void testGetSetDevice()
   {
      final DeviceParameter paramValue = new DeviceParameter();

      final Device device = new Device();
      paramValue.setDevice(device);
      assertEquals(device, paramValue.getDevice());

      paramValue.setDevice(null);
      assertNull(paramValue.getDevice());
   }

   @Test
   public final void testGetSetStringValue()
   {
      final DeviceParameter paramValue = new DeviceParameter("string-value");

      paramValue.setValue("test-1");
      assertEquals("test-1", paramValue.getValue());

      paramValue.setValue(null);
      assertNull(paramValue.getValue());
   }

   @Test
   public final void testGetSetIntValue()
   {
      final DeviceParameter paramValue = new DeviceParameter("1234");

      paramValue.setValue(176);
      assertEquals(Integer.valueOf(176), paramValue.getIntValue());

      paramValue.setValue(null);
      assertNull(paramValue.getIntValue());
   }

   @Test
   public final void testSetIsVisible()
   {
      final DeviceParameter paramValue = new DeviceParameter();

      paramValue.setVisible(true);
      assertTrue(paramValue.isVisible());

      paramValue.setVisible(false);
      assertFalse(paramValue.isVisible());
   }
}
