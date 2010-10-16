package org.freebus.fts.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.TreeSet;

import org.junit.Test;

public class TestLine
{
   @Test
   public final void testLine()
   {
      final Line line = new Line();
      assertNotNull(line);
      assertEquals(0, line.getId());
      assertEquals(0, line.getAddress());
      assertNotNull(line.getDevices());
   }

   @Test
   public final void testGetSetId()
   {
      final Line line = new Line();

      line.setId(1);
      assertEquals(1, line.getId());

      line.setId(15);
      assertEquals(15, line.getId());

      line.setId(0);
      assertEquals(0, line.getId());
   }

   @Test
   public final void testGetSetName()
   {
      final Line line = new Line();
      assertEquals("", line.getName());

      line.setName("line-1");
      assertEquals("line-1", line.getName());

      line.setName("line-2");
      assertEquals("line-2", line.getName());

      line.setName("");
      assertEquals("", line.getName());

      line.setName(null);
      assertEquals("", line.getName());
   }

   @Test
   public final void testGetSetAddress()
   {
      final Line line = new Line();

      line.setAddress(1);
      assertEquals(1, line.getAddress());

      line.setAddress(15);
      assertEquals(15, line.getAddress());

      line.setAddress(0);
      assertEquals(0, line.getAddress());
   }

   @Test
   public final void testGetSetArea()
   {
      final Line line = new Line();
      assertNull(line.getArea());

      final Area area = new Area();
      line.setArea(area);
      assertEquals(area, line.getArea());

      line.setArea(null);
      assertNull(line.getArea());
   }

   @Test
   public final void testGetSetDevices()
   {
      final Line line = new Line();
      assertNotNull(line.getDevices());

      TreeSet<Device> newDevices = new TreeSet<Device>();
      newDevices.add(new Device());
      line.setDevices(newDevices);
      assertEquals(newDevices, line.getDevices());
   }

   @Test
   public final void testAdd()
   {
      final Line line = new Line();
      assertTrue(line.getDevices().isEmpty());

      final Device device = new Device(1, null, null);
      line.add(device);
      assertEquals(1, line.getDevices().size());
      assertEquals(device, line.getDevices().iterator().next());

      line.add(new Device(2, null, null));
      assertEquals(2, line.getDevices().size());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testAddTwice()
   {
      final Line line = new Line();
      final Device device = new Device();
      line.add(device);
      line.add(device);
   }

   @Test
   public final void testEquals()
   {
      final Line line1 = new Line();
      final Line line2 = new Line();

      assertTrue(line1.equals(line1));
      assertTrue(line1.equals(line2));
      assertTrue(line2.equals(line1));

      line1.add(new Device());
      assertFalse(line1.equals(line2));
      assertFalse(line2.equals(line1));

      line2.add(new Device());
      assertTrue(line1.equals(line2));
      assertTrue(line2.equals(line1));

      line1.setName("line-1");
      line2.setName("line-2");
      assertFalse(line1.equals(line2));
      assertFalse(line2.equals(line1));

      line1.setAddress(1);
      line2.setAddress(2);
      assertFalse(line1.equals(line2));
      assertFalse(line2.equals(line1));

      line1.setId(1);
      line2.setId(2);
      assertFalse(line1.equals(line2));
      assertFalse(line2.equals(line1));
   }
}
