package org.freebus.fts.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ParameterAtomicType;
import org.freebus.fts.products.ParameterType;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.VirtualDevice;
import org.junit.Test;

public class TestDevice
{
   @Test
   public final void testDevice()
   {
      final Device device = new Device();
      assertNotNull(device);
      assertEquals(0, device.getId());
      assertEquals(0, device.getAddress());
      assertNull(device.getCatalogEntry());
      assertNull(device.getProgram());
      assertNull(device.getLine());
      assertNull(device.getRoom());
   }

   @Test
   public final void testGetSetId()
   {
      final Device device = new Device();

      device.setId(1234);
      assertEquals(1234, device.getId());

      device.setId(0);
      assertEquals(0, device.getId());
   }

   @Test
   public final void testGetSetAddress()
   {
      final Device device = new Device(33, null, null);

      device.setAddress(123);
      assertEquals(123, device.getAddress());

      device.setAddress(0);
      assertEquals(0, device.getAddress());
   }

   @Test
   public final void testGetPhysicalAddr()
   {
      final Device device = new Device(null, null);
      device.setAddress(12);
      assertEquals(PhysicalAddress.NULL, device.getPhysicalAddress());

      final Line line = new Line();
      line.setAddress(9);
      line.add(device);
      assertEquals(PhysicalAddress.NULL, device.getPhysicalAddress());

      final Area area = new Area();
      area.setAddress(7);
      area.add(line);
      assertEquals(new PhysicalAddress(7, 9, 12), device.getPhysicalAddress());
   }

   @Test
   public final void testGetSetCatalogEntryProgram()
   {
      final Device device = new Device();
      final CatalogEntry catEntry = new CatalogEntry();

      device.setCatalogEntry(catEntry);
      assertEquals(catEntry, device.getCatalogEntry());

      device.setCatalogEntry(null);
      assertNull(device.getCatalogEntry());

      final Program program = new Program();

      device.setProgram(program);
      assertEquals(program, device.getProgram());
   }

   @Test
   public final void testGetSetLine()
   {
      final Device device = new Device();
      final Line line = new Line();

      device.setLine(line);
      assertEquals(line, device.getLine());

      device.setLine(null);
      assertNull(device.getLine());
   }

   @Test
   public final void testGetSetRoom()
   {
      final Device device = new Device();
      final Room room = new Room();

      device.setRoom(room);
      assertEquals(room, device.getRoom());

      device.setRoom(null);
      assertNull(device.getRoom());
   }

   @Test
   public final void testGetSetParameterValue()
   {
      final Program program = new Program();

      final ParameterType paramType = new ParameterType(ParameterAtomicType.UNSIGNED);
      paramType.setProgram(program);

      final Parameter param = new Parameter(paramType);
      program.addParameter(param);
      param.setParameterType(paramType);
      param.setDefaultLong(123);
      param.setDefaultDouble(3.141);
      param.setDefaultString("anything");

      final Device device = new Device();
      device.setProgram(program);

      assertEquals(123, device.getParameterIntValue(param));

      device.setParameterValue(param, 17);
      assertEquals(17, device.getParameterIntValue(param));
   }

   @Test
   public final void testEquals()
   {
      final Device device1 = new Device(1, new VirtualDevice());
      final Device device2 = new Device(1, new VirtualDevice());
      final Device device3 = new Device(2, null, null);

      assertTrue(device1.equals(device1));
      assertTrue(device1.equals(device2));
      assertFalse(device1.equals(null));
      assertFalse(device1.equals(device3));
      assertFalse(device1.equals(new Object()));
   }

   @Test
   public final void testToString()
   {
      final Device device = new Device();
      assertNotNull(device.toString());
   }
}
