package org.freebus.fts.common;

import static org.junit.Assert.*;

import org.freebus.fts.common.types.ObjectPriority;
import org.freebus.fts.common.types.ObjectType;
import org.junit.Test;

public class TestObjectDescriptor
{
   @Test
   public void testObjectDescriptor()
   {
      final ObjectDescriptor od = new ObjectDescriptor();
      assertNotNull(od.getPriority());
      assertNotNull(od.getType());
   }

   @Test
   public void testFromToByteArray()
   {
      final byte[] data = new byte[] { 107, (byte)254, 10 };
      final ObjectDescriptor od = new ObjectDescriptor();
      od.fromByteArray(data, 0);

      assertEquals(107, od.getDataPointer());
      assertTrue(od.isEepromDataPointer());
      assertTrue(od.isReadEnabled());
      assertTrue(od.isTransEnabled());
      assertTrue(od.isWriteEnabled());
      assertEquals(ObjectPriority.HIGH, od.getPriority());
      assertEquals(ObjectType.BYTES_4, od.getType());

      final byte[] dataOut = od.toByteArray();
      assertArrayEquals(data, dataOut);
   }
}
