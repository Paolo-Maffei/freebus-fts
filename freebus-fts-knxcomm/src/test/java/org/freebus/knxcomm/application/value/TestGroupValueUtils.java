package org.freebus.knxcomm.application.value;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestGroupValueUtils
{
   @Test
   public void testIntToFromBytes()
   {
      byte[] raw;

      raw = GroupValueUtils.intToBytes(255, 1);
      assertEquals(255, GroupValueUtils.bytesToInt(raw, 0, 1));

      raw = GroupValueUtils.intToBytes(255, 1);
      assertEquals(255, GroupValueUtils.bytesToInt(raw, 0, 1));

      raw = GroupValueUtils.intToBytes(424242, 4);
      assertEquals(424242, GroupValueUtils.bytesToInt(raw, 0, 4));
   }
}
