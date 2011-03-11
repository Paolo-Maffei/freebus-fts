package org.freebus.fts.service.job.device;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import org.freebus.fts.common.types.ObjectType;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Mask;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceObject;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.fts.project.SubGroup;
import org.freebus.fts.service.devicecontroller.internal.Bcu1DeviceController;
import org.freebus.knxcomm.test.SimulatedMemoryConnection;
import org.freebus.knxcomm.test.SimulatedMemoryConnection.MemoryBlock;
import org.junit.Test;

public class TestBcu1ProgrammerJob
{
   /*
    * Helper Method: create a Bcu1DeviceController object with a device
    * with the physical address 2.4.9
    */
   public Bcu1DeviceController getBcu1DeviceController()
   {
      // Test values taken from Jung's "2094nabs" "Universal/Schalten" application
      final Program program = new Program();
      program.setManufacturer(new Manufacturer(4, "Tester"));
      program.setAddrTabSize(67);
      program.setAssocTabAddr(345);
      program.setAssocTabSize(65);
      program.setCommsTabAddr(410);
      program.setCommsTabSize(50);

      final CommunicationObject comObject1 = new CommunicationObject(1);
      comObject1.setNumber(1);
      comObject1.setType(ObjectType.BITS_1);
      program.getCommunicationObjects().add(comObject1);

      final CommunicationObject comObject2 = new CommunicationObject(2);
      comObject2.setNumber(0);
      comObject2.setType(ObjectType.BYTES_1);
      program.getCommunicationObjects().add(comObject2);

      final Mask mask = new Mask();
      program.setMask(mask);
      mask.setAddressTabAddress(278);
      mask.setAssocTabPtrAddress(273);
      mask.setCommsTabPtrAddress(274);
      mask.setManufacturerDataAddress(257);
      mask.setManufacturerDataSize(3);
      mask.setManufacturerIdAddress(260);
      mask.setManufacturerIdProtected(true);
      mask.setUserRamStart(206);
      mask.setUserRamEnd(223);
      mask.setUserEepromStart(256);
      mask.setUserEepromEnd(510);

      final int start = mask.getUserEepromStart();
      final byte[] eepromData = new byte[256];
      eepromData[411 - start] = (byte) 206;
      eepromData[412 - start] = (byte) 206;
      eepromData[415 - start] = (byte) 207;
      eepromData[418 - start] = (byte) 208;
      program.setEepromData(eepromData);

      final Device device = new Device();
      device.setProgram(program);

      final Area area = new Area();
      area.setAddress(2);

      final Line line = new Line();
      line.setAddress(4);
      line.setArea(area);
      
      device.setAddress(9);
      device.setLine(line);

      final Bcu1DeviceController controller = new Bcu1DeviceController(device);
      return controller;
   }

   /*
    * Helper Method: create a SubGroup object
    */
   public SubGroup createGroup(int mainId, int midId, int subId)
   {
      final MainGroup mainGroup = new MainGroup();
      final MidGroup midGroup = new MidGroup();
      final SubGroup subGroup = new SubGroup();

      mainGroup.setAddress(mainId);
      midGroup.setAddress(midId);
      subGroup.setAddress(subId);

      midGroup.setMainGroup(mainGroup);
      subGroup.setMidGroup(midGroup);

      return subGroup;
   }

   /*
    * Test the EEPROM byte getter
    */
   @Test
   public void getEepromByte()
   {
      final Bcu1DeviceController controller = getBcu1DeviceController();
      final Bcu1ProgrammerJob job = new Bcu1ProgrammerJob(controller);

      assertEquals((byte) 206, job.getEepromByte(411));
   }
   
   /*
    * Upload an empty address table (containing only the physical address of the
    * device).
    */
   @Test
   public void uploadAddrTabEmpty() throws IOException, TimeoutException
   {
      final Bcu1DeviceController controller = getBcu1DeviceController();

      final SimulatedMemoryConnection memCon = new SimulatedMemoryConnection(0, 510);
      final Bcu1ProgrammerJob job = new Bcu1ProgrammerJob(controller);
      job.uploadAddrTab(memCon);

      final Vector<MemoryBlock> written = memCon.getWritten();
      assertEquals(1, written.size());

      final MemoryBlock block = written.get(0);
      assertEquals(278, block.startAddress);
      assertEquals(3, block.data.length);
      assertEquals(1, block.data[0]);
      assertEquals(0x24, block.data[1]);
      assertEquals(0x09, block.data[2]);
   }

   /*
    * Upload an address table with one group address
    */
   @Test
   public void uploadAddrTab() throws IOException, TimeoutException
   {
      final Bcu1DeviceController controller = getBcu1DeviceController();
      final Device device = controller.getDevice();

      final DeviceObject deviceObject = new DeviceObject();
      deviceObject.add(createGroup(2, 4, 7));
      device.getDeviceObjects().add(deviceObject);

      final SimulatedMemoryConnection memCon = new SimulatedMemoryConnection(0, 510);
      final Bcu1ProgrammerJob job = new Bcu1ProgrammerJob(controller);
      job.uploadAddrTab(memCon);

      final byte[] mem = memCon.getMem();
      assertEquals(2, mem[278]);
      assertEquals(0x24, mem[279]);
      assertEquals(0x09, mem[280]);
      assertEquals(0x14, mem[281]);
      assertEquals(0x07, mem[282]);
      assertEquals(0x00, mem[283]);
      assertEquals(0x00, mem[284]);
   }

   /*
    * Upload an empty device objects table
    */
   @Test
   public void uploadCommObjsTabEmpty() throws IOException, TimeoutException
   {
      final Bcu1DeviceController controller = getBcu1DeviceController();

      // Remove the device- and com-objects that getBcu1DeviceController created
      controller.getDevice().getProgram().getCommunicationObjects().clear();
      controller.getDevice().getDeviceObjects().clear();
      
      final SimulatedMemoryConnection memCon = new SimulatedMemoryConnection(0, 510);
      final Bcu1ProgrammerJob job = new Bcu1ProgrammerJob(controller);
      job.uploadCommObjsTab(memCon);

      final byte[] mem = memCon.getMem();
      assertEquals(0, mem[410]);
      assertEquals((byte) 206, mem[411]);
   }

   /*
    * Upload a device objects table
    */
   @Test
   public void uploadCommObjsTab() throws IOException, TimeoutException
   {
      final Bcu1DeviceController controller = getBcu1DeviceController();
      
      final SimulatedMemoryConnection memCon = new SimulatedMemoryConnection(0, 510);
      final Bcu1ProgrammerJob job = new Bcu1ProgrammerJob(controller);
      job.uploadCommObjsTab(memCon);

      final byte[] mem = memCon.getMem();
      assertEquals(2, mem[410]);
      assertEquals((byte) 206, mem[411]);
   }
}
