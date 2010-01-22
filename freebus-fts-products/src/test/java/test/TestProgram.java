package test;

import junit.framework.TestCase;

import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Program;

public class TestProgram extends TestCase
{
   public final void testProgram()
   {
      final Program prog = new Program();
      assertNotNull(prog);

      assertEquals(0, prog.getId());
      assertEquals(null, prog.getName());
   }

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

   public final void testGetSetMaskId()
   {
      final Program prog = new Program();

      prog.setMaskId(1);
      assertEquals(1, prog.getMaskId());

      prog.setMaskId(12345);
      assertEquals(12345, prog.getMaskId());

      prog.setMaskId(0);
      assertEquals(0, prog.getMaskId());
   }

   public final void testGetSetName()
   {
      final Program prog = new Program();

      prog.setName("str-1");
      assertEquals("str-1", prog.getName());

      prog.setName("");
      assertEquals("", prog.getName());

      prog.setName(null);
      assertEquals(null, prog.getName());

      prog.setName("str-2");
      assertEquals("str-2", prog.getName());
   }

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

   public final void testIsSetLinkable()
   {
      final Program prog = new Program();

      prog.setLinkable(true);
      assertEquals(true, prog.isLinkable());

      prog.setLinkable(false);
      assertEquals(false, prog.isLinkable());
   }

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

   public final void testGetSetEepromData()
   {
      final Program prog = new Program();

      prog.setEepromData("str-1");
      assertEquals("str-1", prog.getEepromData());

      prog.setEepromData("");
      assertEquals("", prog.getEepromData());

      prog.setEepromData(null);
      assertEquals(null, prog.getEepromData());

      prog.setEepromData("str-2");
      assertEquals("str-2", prog.getEepromData());
   }

   public final void testIsSetDynamicManagement()
   {
      final Program prog = new Program();

      prog.setDynamicManagement(true);
      assertEquals(true, prog.isDynamicManagement());

      prog.setDynamicManagement(false);
      assertEquals(false, prog.isDynamicManagement());
   }

   public final void testGetProgramType()
   {
      fail("Not yet implemented");
   }

   public final void testGetRamSize()
   {
      fail("Not yet implemented");
   }

   public final void testGetProgramStyle()
   {
      fail("Not yet implemented");
   }

   public final void testIsPollingMaster()
   {
      fail("Not yet implemented");
   }

   public final void testGetNumPollingGroups()
   {
      fail("Not yet implemented");
   }

}
