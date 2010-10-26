package org.freebus.fts.products;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestProgram
{
   @Test
   public final void testProgram()
   {
      final Program prog = new Program();
      assertNotNull(prog);

      assertEquals(0, prog.getId());
      assertEquals("", prog.getName());
      assertNotNull(prog.toString());
   }

   @Test
   public final void testGetSetId()
   {
      final Program prog = new Program();

      prog.setId(1);
      assertEquals(1, prog.getId());

      prog.setId(12345);
      assertEquals(12345, prog.getId());

      prog.setId(0);
      assertEquals(0, prog.getId());
   }

   @Test
   public final void testGetSetMaskId()
   {
      final Program prog = new Program();
      final Mask mask = new Mask();

      prog.setMask(mask);
      assertEquals(mask, prog.getMask());

      prog.setMask(null);
      assertNull(prog.getMask());
   }

   @Test
   public final void testGetSetName()
   {
      final Program prog = new Program();

      prog.setName("str-1");
      assertEquals("str-1", prog.getName());

      prog.setName("");
      assertEquals("", prog.getName());

      prog.setName(null);
      assertEquals("", prog.getName());

      prog.setName("str-2");
      assertEquals("str-2", prog.getName());
   }

   @Test
   public final void testGetSetVersion()
   {
      final Program prog = new Program();

      prog.setVersion("str-1");
      assertEquals("str-1", prog.getVersion());

      prog.setVersion("");
      assertEquals("", prog.getVersion());

      prog.setVersion(null);
      assertEquals(null, prog.getVersion());

      prog.setVersion("str-2");
      assertEquals("str-2", prog.getVersion());
   }

   @Test
   public final void testIsSetLinkable()
   {
      final Program prog = new Program();

      prog.setLinkable(true);
      assertEquals(true, prog.isLinkable());

      prog.setLinkable(false);
      assertEquals(false, prog.isLinkable());
   }

   @Test
   public final void testGetSetDeviceType()
   {
      final Program prog = new Program();

      prog.setDeviceType(1);
      assertEquals(1, prog.getDeviceType());

      prog.setDeviceType(12345);
      assertEquals(12345, prog.getDeviceType());

      prog.setDeviceType(0);
      assertEquals(0, prog.getDeviceType());
   }

   @Test
   public final void testGetSetPeiType()
   {
      final Program prog = new Program();

      prog.setPeiType(1);
      assertEquals(1, prog.getPeiType());

      prog.setPeiType(12345);
      assertEquals(12345, prog.getPeiType());

      prog.setPeiType(0);
      assertEquals(0, prog.getPeiType());
   }

   @Test
   public final void testGetSetAddrTabSize()
   {
      final Program prog = new Program();

      prog.setAddrTabSize(1);
      assertEquals(1, prog.getAddrTabSize());

      prog.setAddrTabSize(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, prog.getAddrTabSize());

      prog.setAddrTabSize(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, prog.getAddrTabSize());

      prog.setAddrTabSize(0);
      assertEquals(0, prog.getAddrTabSize());
   }

   @Test
   public final void testGetSetAssocTabAddr()
   {
      final Program prog = new Program();

      prog.setAssocTabAddr(1);
      assertEquals(1, prog.getAssocTabAddr());

      prog.setAssocTabAddr(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, prog.getAssocTabAddr());

      prog.setAssocTabAddr(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, prog.getAssocTabAddr());

      prog.setAssocTabAddr(0);
      assertEquals(0, prog.getAssocTabAddr());
   }

   @Test
   public final void testGetSetAssocTabSize()
   {
      final Program prog = new Program();

      prog.setAssocTabSize(1);
      assertEquals(1, prog.getAssocTabSize());

      prog.setAssocTabSize(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, prog.getAssocTabSize());

      prog.setAssocTabSize(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, prog.getAssocTabSize());

      prog.setAssocTabSize(0);
      assertEquals(0, prog.getAssocTabSize());
   }

   @Test
   public final void testGetSetCommsTabAddr()
   {
      final Program prog = new Program();

      prog.setCommsTabAddr(1);
      assertEquals(1, prog.getCommsTabAddr());

      prog.setCommsTabAddr(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, prog.getCommsTabAddr());

      prog.setCommsTabAddr(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, prog.getCommsTabAddr());

      prog.setCommsTabAddr(0);
      assertEquals(0, prog.getCommsTabAddr());
   }

   @Test
   public final void testGetSetCommsTabSize()
   {
      final Program prog = new Program();

      prog.setCommsTabSize(1);
      assertEquals(1, prog.getCommsTabSize());

      prog.setCommsTabSize(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, prog.getCommsTabSize());

      prog.setCommsTabSize(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, prog.getCommsTabSize());

      prog.setCommsTabSize(0);
      assertEquals(0, prog.getCommsTabSize());
   }

   @Test
   public final void testGetSetSerial()
   {
      final Program prog = new Program();

      prog.setSerial("str-1");
      assertEquals("str-1", prog.getSerial());

      prog.setSerial("");
      assertEquals("", prog.getSerial());

      prog.setSerial(null);
      assertEquals(null, prog.getSerial());

      prog.setSerial("str-2");
      assertEquals("str-2", prog.getSerial());
   }

   @Test
   public final void testGetSetManufacturer()
   {
      final Program prog = new Program();
      final Manufacturer manu = new Manufacturer(1, "manu-1");

      assertNull(prog.getManufacturer());

      prog.setManufacturer(manu);
      assertEquals(manu, prog.getManufacturer());

      prog.setManufacturer(null);
      assertNull(prog.getManufacturer());
   }

   @Test
   public final void testGetSetEepromData()
   {
      final Program prog = new Program();

      final byte[] data1 = new byte[] { 0, 1, 2, 3, 0, 4, 5, 6 };
      prog.setEepromData(data1);
      assertArrayEquals(data1, prog.getEepromData());

      prog.setEepromData(new byte[] { });
      assertArrayEquals(new byte[] { }, prog.getEepromData());

      prog.setEepromData(null);
      assertArrayEquals(null, prog.getEepromData());
   }

   @Test
   public final void testIsSetDynamicManagement()
   {
      final Program prog = new Program();

      prog.setDynamicManagement(true);
      assertEquals(true, prog.isDynamicManagement());

      prog.setDynamicManagement(false);
      assertEquals(false, prog.isDynamicManagement());
   }

   @Test
   public final void testGetSetProgramType()
   {
      final Program prog = new Program();

      prog.setProgramType(1);
      assertEquals(1, prog.getProgramType());

      prog.setProgramType(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, prog.getProgramType());

      prog.setProgramType(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, prog.getProgramType());

      prog.setProgramType(0);
      assertEquals(0, prog.getProgramType());
   }

   @Test
   public final void testGetSetRamSize()
   {
      final Program prog = new Program();
      assertEquals(0, prog.getRamSize());

      prog.setRamSize(1);
      assertEquals(1, prog.getRamSize());

      prog.setRamSize(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, prog.getRamSize());

      prog.setRamSize(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, prog.getRamSize());

      prog.setRamSize(0);
      assertEquals(0, prog.getRamSize());
   }

   @Test
   public final void testGetSetProgramStyle()
   {
      final Program prog = new Program();
      assertEquals(0, prog.getProgramStyle());

      prog.setProgramStyle(1);
      assertEquals(1, prog.getProgramStyle());

      prog.setProgramStyle(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, prog.getProgramStyle());

      prog.setProgramStyle(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, prog.getProgramStyle());

      prog.setProgramStyle(0);
      assertEquals(0, prog.getProgramStyle());
   }

   @Test
   public final void testIsSetPollingMaster()
   {
      final Program prog = new Program();
      assertEquals(false, prog.isPollingMaster());

      prog.setPollingMaster(true);
      assertEquals(true, prog.isPollingMaster());

      prog.setPollingMaster(false);
      assertEquals(false, prog.isPollingMaster());
   }

   @Test
   public final void testGetSetNumPollingGroups()
   {
      final Program prog = new Program();
      assertEquals(0, prog.getNumPollingGroups());

      prog.setNumPollingGroups(1);
      assertEquals(1, prog.getNumPollingGroups());

      prog.setNumPollingGroups(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, prog.getNumPollingGroups());

      prog.setNumPollingGroups(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, prog.getNumPollingGroups());

      prog.setNumPollingGroups(0);
      assertEquals(0, prog.getNumPollingGroups());
   }

   @Test
   public final void testEqualsHashCode()
   {
      final Program prog1 = new Program();
      prog1.setId(1);

      final Program prog2 = new Program();
      prog2.setId(1);

      final Program prog3 = new Program();
      prog3.setId(3);

      assertFalse(prog1.equals(null));
      assertFalse(prog1.equals(new Object()));

      assertTrue(prog2.equals(prog1));
      assertEquals(prog2.hashCode(), prog1.hashCode());

      assertTrue(prog1.equals(prog1));
      assertTrue(prog1.equals(prog2));
      assertFalse(prog1.equals(prog3));

      prog1.setDeviceType(1234);
      assertFalse(prog1.equals(prog2));

      prog2.setDeviceType(1234);
      assertTrue(prog1.equals(prog2));
   }
}
