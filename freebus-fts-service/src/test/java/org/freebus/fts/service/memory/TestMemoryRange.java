package org.freebus.fts.service.memory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.freebus.fts.service.memory.MemoryRange;
import org.freebus.fts.service.memory.MemoryValueType;
import org.junit.Test;

public class TestMemoryRange
{
   @Test
   public void testMemoryRange()
   {
      final MemoryRange range = new MemoryRange(100, 10);

      assertEquals(100, range.getStart());
      assertEquals(10, range.size());

      assertEquals(0, range.getValue(100));
      assertEquals(MemoryValueType.UNSET, range.getValueType(100));

      assertEquals(0, range.getValue(109));
   }

   @Test
   public void testMemoryRangeInitial()
   {
      final MemoryRange range = new MemoryRange(100, new byte[] { 4, 7, 12 });

      assertEquals(100, range.getStart());
      assertEquals(3, range.size());

      assertEquals(4, range.getValue(100));
      assertEquals(MemoryValueType.INITIAL, range.getValueType(100));

      assertEquals(7, range.getValue(101));
      assertEquals(MemoryValueType.INITIAL, range.getValueType(101));

      assertEquals(12, range.getValue(102));
      assertEquals(MemoryValueType.INITIAL, range.getValueType(102));

      assertArrayEquals(new byte[] { 4, 7 }, range.getValues(100, 2));
      assertArrayEquals(new byte[] { 7, 12 }, range.getValues(101, 2));
   }

   @Test
   public void testGetSetValue()
   {
      final MemoryRange range = new MemoryRange(1000, 5);

      assertEquals(0, range.getValue(1000));
      assertEquals(MemoryValueType.UNSET, range.getValueType(1000));

      range.setInitialValue(1000, (byte) 12);
      assertEquals(12, range.getValue(1000));
      assertEquals(MemoryValueType.INITIAL, range.getValueType(1000));

      range.setValue(1000, (byte) 47);
      assertEquals(47, range.getValue(1000));
      assertEquals(MemoryValueType.MODIFIED, range.getValueType(1000));

      range.setValue(1004, (byte) 41);
      assertEquals(41, range.getValue(1004));
      assertEquals(47, range.getValue(1000));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testGetValueOutOfRange()
   {
      final MemoryRange range = new MemoryRange(1000, 5);
      range.getValue(999);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testGetValueOutOfRange2()
   {
      final MemoryRange range = new MemoryRange(1000, 5);
      range.getValue(1005);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testGetValueTypeOutOfRange()
   {
      final MemoryRange range = new MemoryRange(1000, 5);
      range.getValueType(-1);
   }
}
