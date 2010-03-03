package org.freebus.knxcomm.netip;

import static org.junit.Assert.*;

import org.freebus.knxcomm.netip.types.DeviceDescriptionType;
import org.junit.Test;

public class TestDeviceDescriptionType
{
   @Test
   public final void testValueOf()
   {
      assertEquals(DeviceDescriptionType.DEVICE_INFO, DeviceDescriptionType.valueOf(0x1));
      assertEquals(DeviceDescriptionType.RESERVED, DeviceDescriptionType.valueOf(0xa1));
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testValueOfInvalid()
   {
      DeviceDescriptionType.valueOf(-1);
   }
}
