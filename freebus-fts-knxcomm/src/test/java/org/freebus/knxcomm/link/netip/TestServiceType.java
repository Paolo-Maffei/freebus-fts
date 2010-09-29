package org.freebus.knxcomm.link.netip;

import static org.junit.Assert.*;

import org.freebus.knxcomm.link.netip.types.ServiceType;
import org.junit.Test;

public class TestServiceType
{
   @Test
   public final void testValueOf()
   {
      assertEquals(ServiceType.SEARCH_REQUEST, ServiceType.valueOf(0x201));
      assertEquals(ServiceType.CONNECT_RESPONSE, ServiceType.valueOf(0x206));
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testValueOfInvalid()
   {
      ServiceType.valueOf(0);
   }
}
