package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
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

      line.setName("line-1");
      assertEquals("line-1", line.getName());

      line.setName("line-2");
      assertEquals("line-2", line.getName());

      line.setName("");
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

      Set<Device> newDevices = new HashSet<Device>();
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
}
