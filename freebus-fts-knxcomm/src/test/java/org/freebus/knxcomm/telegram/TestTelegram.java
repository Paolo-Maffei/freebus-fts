package org.freebus.knxcomm.telegram;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.freebus.fts.common.HexString;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.application.GenericApplication;
import org.freebus.knxcomm.application.GenericDataApplication;
import org.freebus.knxcomm.application.IndividualAddressWrite;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor0;
import org.junit.Test;

public class TestTelegram
{
   @Test
   public void testTelegram()
   {
      final Telegram telegram = new Telegram();

      assertTrue(telegram.isRepeated());
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

      telegram.setApplicationType(ApplicationType.ADC_Read);
      assertEquals(ApplicationType.ADC_Read, telegram.getApplicationType());

      telegram.setApplicationType(ApplicationType.GroupValue_Write);
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
   }

   @Test
   public void testFromRawData() throws IOException
   {
      final byte[] data = HexString.valueOf("bc 11 01 0a 05 e0 01 00");
      final Telegram telegram = TelegramFactory.createTelegram(data);

      assertFalse(telegram.isRepeated());
      assertEquals(Priority.LOW, telegram.getPriority());
      assertEquals(6, telegram.getRoutingCounter());
      assertEquals(new PhysicalAddress(1, 1, 1), telegram.getFrom());
      assertEquals(new GroupAddress(1, 2, 5), telegram.getDest());
      assertEquals(ApplicationType.IndividualAddress_Read, telegram.getApplicationType());
   }

   @Test(expected = IOException.class)
   public void testFromRawDataWrongLen() throws IOException
   {
      TelegramFactory.createTelegram(HexString.valueOf("90 33 07 00 00 64 43 40 00 12"));
   }

   @Test
   public final void testFromRawData3() throws IOException
   {
      final byte[] data = HexString.valueOf("90 33 07 00 00 63  43 40 00 12");
      final Telegram telegram = TelegramFactory.createTelegram(data);

      assertEquals(ApplicationType.DeviceDescriptor_Response, telegram.getApplicationType());
      final DeviceDescriptorResponse app = (DeviceDescriptorResponse) telegram.getApplication();

      assertEquals(ApplicationType.DeviceDescriptor_Response, app.getType());
      assertEquals(0, app.getDescriptorType());
      assertEquals(new DeviceDescriptor0(0x0012), app.getDescriptor());
   }

   @Test
   public void testFromRawDataDisconnect() throws IOException
   {
      final byte[] data = HexString.valueOf("90 11 06 11 ff 60 81");
      final Telegram telegram = TelegramFactory.createTelegram(data);

      assertEquals(Transport.Disconnect, telegram.getTransport());

      final byte[] dataOut = telegram.toByteArray();
      assertArrayEquals(data, dataOut);
   }

   @Test
   public void testFromRawDataDeviceDescriptorResponse() throws IOException
   {
      final byte[] data = HexString.valueOf("90 33 07 00 00 61 43 00");
      final Telegram telegram = TelegramFactory.createTelegram(data);

      assertEquals(Transport.Connected, telegram.getTransport());
   }

   @Test
   public void testFromRawDataPhysAddrResp() throws IOException
   {
      final byte[] data = HexString.valueOf("b0 11 ff 00 00 e1 01 00");
      final Telegram telegram = TelegramFactory.createTelegram(data);
      assertNotNull(telegram);
   }

   @Test
   public void testFromRawDataGroupValueWrite() throws IOException
   {
      final byte[] data = HexString.valueOf("9c 11 06 08 0a e1 00 81");
      final Telegram telegram = TelegramFactory.createTelegram(data);

      assertEquals(Transport.Group, telegram.getTransport());
      assertEquals(ApplicationType.GroupValue_Write, telegram.getApplicationType());

      final GenericDataApplication app = (GenericDataApplication) telegram.getApplication();
      assertEquals(1, app.getApciValue());
      assertNull(app.getData());
   }

   @Test
   public void testToRawData()
   {
      final Telegram telegram = new Telegram();
      telegram.setFrom(new PhysicalAddress(1, 1, 1));
      telegram.setDest(new GroupAddress(1, 2, 5));
      telegram.setRoutingCounter(3);
      telegram.setRepeated(false);
      telegram.setApplication(new GenericDataApplication(ApplicationType.GroupValue_Write, new int[] { 0, 1 }));

      final byte[] dataOut = telegram.toByteArray();
      assertArrayEquals(HexString.valueOf("bc 11 01 0a 05 b2 00 80 01"), dataOut);
   }

   @Test
   public void testToRawData2()
   {
      // Telegram without application data
      final Telegram telegram = new Telegram();
      telegram.setFrom(new PhysicalAddress(1, 1, 1));
      telegram.setDest(new PhysicalAddress(3, 3, 7));
      telegram.setPriority(Priority.SYSTEM);
      telegram.setRepeated(true);
      telegram.setTransport(Transport.Connected);
      telegram.setSequence(0);
      telegram.setApplicationType(ApplicationType.IndividualAddress_Read);

      assertArrayEquals(HexString.valueOf("90 11 01 33 07 61 41 00"), telegram.toByteArray());
   }

   @Test
   public void testToRawData3()
   {
      final Telegram telegram = new Telegram();
      telegram.setFrom(new PhysicalAddress(1, 1, 255));
      telegram.setDest(new PhysicalAddress(1, 1, 6));
      telegram.setPriority(Priority.SYSTEM);
      telegram.setTransport(Transport.Connect);
      telegram.setRepeated(false);

      assertArrayEquals(HexString.valueOf("b0 11 ff 11 06 60 80"), telegram.toByteArray());
   }

   @Test
   public void testGetSetTransport()
   {
      final Telegram telegram = new Telegram();

      telegram.setTransport(Transport.Individual);
      assertEquals(Transport.Individual, telegram.getTransport());

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

   @Test
   public void testIsSimilar() throws IOException
   {
      final Telegram telegram1 = TelegramFactory.createTelegram(HexString.valueOf("bc 11 ff 11 06 60 81"));
      final Telegram telegram2 = TelegramFactory.createTelegram(HexString.valueOf("bc 11 ff 11 06 60 81"));

      assertTrue(telegram1.isSimilar(telegram2));
      assertTrue(telegram2.isSimilar(telegram1));
   }

   @Test
   public void testIsSimilar2() throws IOException
   {
      final Telegram telegram1 = TelegramFactory.createTelegram(HexString.valueOf("b0 11 ff 00 00 e1 01 00"));
      final Telegram telegram2 = TelegramFactory.createTelegram(HexString.valueOf("b0 11 ff 00 00 e1 01 00"));

      assertTrue(telegram1.isSimilar(telegram2));
      assertTrue(telegram2.isSimilar(telegram1));
   }
}
