package test;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;

public class TestLine extends TestCase
{

   public final void testLine()
   {
      final Line line = new Line();
      assertNotNull(line);
      assertEquals(0, line.getId());
      assertEquals(0, line.getAddress());
      assertNotNull(line.getDevices());
   }

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

   public final void testGetSetDevices()
   {
      final Line line = new Line();
      assertNotNull(line.getDevices());

      Set<Device> newDevices = new HashSet<Device>();
      line.setDevices(newDevices);
      assertEquals(newDevices, line.getDevices());
   }

   public final void testAdd()
   {
      final Line line = new Line();
      assertTrue(line.getDevices().isEmpty());

      final Device device = new Device();
      line.add(device);
      assertEquals(1, line.getDevices().size());
      assertEquals(device, line.getDevices().iterator().next());

      line.add(new Device());
      assertEquals(2, line.getDevices().size());
   }
}
