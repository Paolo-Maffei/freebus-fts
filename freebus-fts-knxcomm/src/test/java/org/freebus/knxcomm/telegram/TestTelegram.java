package org.freebus.knxcomm.telegram;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.GenericApplication;
import org.freebus.knxcomm.application.IndividualAddressWrite;
import org.junit.Ignore;
import org.junit.Test;

public class TestTelegram
{
   @Test
   public void testTelegram()
   {
      final Telegram telegram = new Telegram();

      assertFalse(telegram.isRepeated());
      assertEquals(Priority.LOW, telegram.getPriority());
      assertNull(telegram.getApplication());
      assertEquals(ApplicationType.None, telegram.getApplicationType());
      assertNotNull(telegram.toString());
   }

   @Test
   public void testGetSetFrom()
   {
      final Telegram telegram = new Telegram();
      assertEquals(PhysicalAddress.NULL, telegram.getFrom());

      final PhysicalAddress from = new PhysicalAddress(1, 2, 4);
      telegram.setFrom(from);
      assertEquals(from, telegram.getFrom());

      telegram.setFrom(PhysicalAddress.NULL);
      assertEquals(PhysicalAddress.NULL, telegram.getFrom());
   }

   @Test
   public void testGetSetDest()
   {
      final Telegram telegram = new Telegram();
      assertEquals(GroupAddress.BROADCAST, telegram.getDest());

      final PhysicalAddress destPhys = new PhysicalAddress(4, 8, 16);
      telegram.setDest(destPhys);
      assertEquals(destPhys, telegram.getDest());

      telegram.setDest(PhysicalAddress.NULL);
      assertEquals(PhysicalAddress.NULL, telegram.getDest());

      final GroupAddress destGrp = new GroupAddress(3, 3, 1);
      telegram.setDest(destGrp);
      assertEquals(destGrp, telegram.getDest());

      telegram.setTransport(null);
      telegram.setDest(destGrp);
      assertEquals(destGrp, telegram.getDest());

      telegram.setTransport(null);
      telegram.setDest(destPhys);
      assertEquals(destPhys, telegram.getDest());
   }

   @Test
   public void testGetSetPriority()
   {
      final Telegram telegram = new Telegram();
      assertEquals(Priority.LOW, telegram.getPriority());

      telegram.setPriority(Priority.SYSTEM);
      assertEquals(Priority.SYSTEM, telegram.getPriority());

      telegram.setPriority(Priority.NORMAL);
      assertEquals(Priority.NORMAL, telegram.getPriority());
   }

   @Test
   public void testIsSetRepeated()
   {
      final Telegram telegram = new Telegram();
      assertFalse(telegram.isRepeated());

      telegram.setRepeated(true);
      assertTrue(telegram.isRepeated());

      telegram.setRepeated(false);
      assertFalse(telegram.isRepeated());
   }

   @Test
   public void testGetSetRoutingCounter()
   {
      final Telegram telegram = new Telegram();
      assertEquals(6, telegram.getRoutingCounter());

      telegram.setRoutingCounter(0);
      assertEquals(0, telegram.getRoutingCounter());

      telegram.setRoutingCounter(-1);
      assertEquals(0, telegram.getRoutingCounter());

      telegram.setRoutingCounter(3);
      assertEquals(3, telegram.getRoutingCounter());

      telegram.setRoutingCounter(7);
      assertEquals(7, telegram.getRoutingCounter());

      telegram.setRoutingCounter(8);
      assertEquals(7, telegram.getRoutingCounter());
   }

   @Test
   public void testGetSetApplicationApplicationType()
   {
      final Telegram telegram = new Telegram();
      assertEquals(ApplicationType.None, telegram.getApplicationType());

      telegram.setApplication(ApplicationType.ADC_Read);
      assertEquals(ApplicationType.ADC_Read, telegram.getApplicationType());

      telegram.setApplication(ApplicationType.GroupValue_Write);
      assertEquals(ApplicationType.GroupValue_Write, telegram.getApplicationType());
   }

   @Test
   public void testGetSetApplication()
   {
      final Telegram telegram = new Telegram();
      assertNull(telegram.getApplication());

      final Application app1 = new IndividualAddressWrite(PhysicalAddress.ONE);
      telegram.setApplication(app1);
      assertEquals(app1, telegram.getApplication());

      final Application app2 = new GenericApplication(ApplicationType.NetworkParameter_Read);
      telegram.setApplication(app2);
      assertEquals(app2, telegram.getApplication());

      final int[] app3data = new int[] { 1, 2 };
      telegram.setApplication(ApplicationType.ADC_Read, app3data);
      assertNotNull(telegram.getApplication());
      assertEquals(ApplicationType.ADC_Read, telegram.getApplication().getType());
   }

   @Test
   public void testFromRawData() throws InvalidDataException
   {
      final Telegram telegram = new Telegram();

      final int[] data1 = new int[] { 0xbc, 0x11, 0x01, 0x0a, 0x05, 0xe0, 0x01, 0x00 };

      telegram.fromRawData(data1, 0);
      assertFalse(telegram.isRepeated());
      assertEquals(Priority.LOW, telegram.getPriority());
      assertEquals(6, telegram.getRoutingCounter());
      assertEquals(new PhysicalAddress(1, 1, 1), telegram.getFrom());
      assertEquals(new GroupAddress(1, 2, 5), telegram.getDest());
      assertEquals(ApplicationType.IndividualAddress_Read, telegram.getApplicationType());
   }

   @Test(expected = InvalidDataException.class)
   public void testFromRawDataWrongLen() throws InvalidDataException
   {
      final Telegram telegram = new Telegram();
      final int[] data1 = new int[] { 0x90, 0x33, 0x07, 0x00, 0x00, 0x64, 0x43, 0x40, 0x00, 0x12 };
      telegram.fromRawData(data1, 0);
   }

   /**
    * A negative reply from a Merten switch.
    */
   @Test
   public void testFromRawDataConnectedNack() throws InvalidDataException
   {
      final Telegram telegram = new Telegram();
      final int[] data = new int[] { 0x90, 0x11, 0x06, 0x11, 0xff, 0x60, 0x81, 0x77 };
      telegram.fromRawData(data, 0);
   }

   /**
    * A negative reply (T_NAK-PDU) from a real switch.
    *
    * TODO this test currently fails. The test might as well be simply wrong ...
    * seems to be a T_Disconnect anyways ...
    */
   @Ignore
   @Test
   public void testFromToRawDataConnectedNack() throws InvalidDataException
   {
      final int[] data = new int[] { 0x90, 0x11, 0x06, 0x11, 0xff, 0x60, 0x81, 0x77 };
      final Telegram telegram = new Telegram();

      telegram.fromRawData(data, 0);
      assertEquals(Transport.Disconnect, telegram.getTransport());

      final int[] dataOut = new int[data.length];
      telegram.toRawData(dataOut, 0);

      assertArrayEquals(data, dataOut);
   }

   @Test
   public void testFromRawDataDeviceDescriptorResponse() throws InvalidDataException
   {
      final Telegram telegram = new Telegram();

      telegram.fromRawData(new int[] { 0x90, 0x33, 0x07, 0x00, 0x00, 0x61, 0x43, 0x00 }, 0);
   }

   @Test
   public void testToRawData()
   {
      final int[] data = new int[256];
      int len;

      final Telegram telegram = new Telegram();
      telegram.setFrom(new PhysicalAddress(1, 1, 1));
      telegram.setDest(new GroupAddress(1, 2, 5));
      telegram.setRoutingCounter(3);
      telegram.setApplication(ApplicationType.GroupValue_Write, new int[] { 1 });

      len = telegram.toRawData(data, 0);
      assertArrayEquals(new int[] { 0xbc, 0x11, 0x01, 0x0a, 0x05, 0xb1, 0x00, 0x81 }, Arrays.copyOf(data, len));
   }

   @Test
   public void testToRawData2()
   {
      final int[] data = new int[256];
      int len;

      // Telegram without application data
      final Telegram telegramNullData = new Telegram();
      telegramNullData.setFrom(new PhysicalAddress(1, 1, 1));
      telegramNullData.setDest(new PhysicalAddress(3, 3, 7));
      telegramNullData.setPriority(Priority.SYSTEM);
      telegramNullData.setRepeated(true);
      telegramNullData.setTransport(Transport.Connected);
      telegramNullData.setSequence(0);
      telegramNullData.setApplication(ApplicationType.IndividualAddress_Read);

      len = telegramNullData.toRawData(data, 0);
      assertArrayEquals(new int[] { 0x90, 0x11, 0x01, 0x33, 0x07, 0x61, 0x41, 0x00 }, Arrays.copyOf(data, len));
   }

   @Test
   public void testToRawData3()
   {
      final int[] rawData = new int[256];

      final Telegram telegram = new Telegram();
      telegram.setFrom(new PhysicalAddress(1, 1, 255));
      telegram.setDest(new PhysicalAddress(1, 1, 6));
      telegram.setPriority(Priority.SYSTEM);
      telegram.setTransport(Transport.Connect);

      final int len = telegram.toRawData(rawData, 0);
      assertArrayEquals(new int[] { 0xb0, 0x11, 0xff, 0x11, 0x06, 0x60, 0x80 }, Arrays.copyOf(rawData, len));
   }

   @Test
   public void testGetSetTransport()
   {
      final Telegram telegram = new Telegram();

      telegram.setTransport(Transport.Group);
      assertEquals(Transport.Group, telegram.getTransport());

      telegram.setTransport(Transport.Individual);
      assertEquals(Transport.Individual, telegram.getTransport());
   }

   @Test
   public void testGetSetSequence()
   {
      final Telegram telegram = new Telegram();
      assertEquals(0, telegram.getSequence());

      telegram.setSequence(123);
      assertEquals(123, telegram.getSequence());
   }

   @Test
   public void testClone()
   {
      final Telegram telegram = new Telegram();
      final Telegram clonedTelegram = telegram.clone();
      assertEquals(telegram, clonedTelegram);
      assertEquals(telegram.hashCode(), clonedTelegram.hashCode());
   }
}
