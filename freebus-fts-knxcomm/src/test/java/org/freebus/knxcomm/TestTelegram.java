package org.freebus.knxcomm;

import static org.junit.Assert.*;

import org.freebus.knxcomm.telegram.Application;
import org.freebus.knxcomm.telegram.GroupAddress;
import org.freebus.knxcomm.telegram.InvalidDataException;
import org.freebus.knxcomm.telegram.PhysicalAddress;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;
import org.junit.Test;

public class TestTelegram
{
   @Test
   public void testTelegram()
   {
      final Telegram telegram = new Telegram();

      assertFalse(telegram.isRepeated());
      assertEquals(Priority.LOW, telegram.getPriority());
      assertEquals(Application.None, telegram.getApplication());
   }

   @Test
   public void testGetSetApplication()
   {
      final Telegram telegram = new Telegram();
      assertEquals(Application.None, telegram.getApplication());

      telegram.setApplication(Application.ADC_Read);
      assertEquals(Application.ADC_Read, telegram.getApplication());

      telegram.setApplication(Application.GroupValue_Write);
      assertEquals(Application.GroupValue_Write, telegram.getApplication());
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
   public void testGetSetData()
   {
      final Telegram telegram = new Telegram();
      assertNull(telegram.getData());

      final int[] data = new int[3];
      telegram.setData(data);
      assertEquals(data, telegram.getData());

      final int[] data2 = new int[3];
      telegram.setData(data2);
      assertEquals(data2, telegram.getData());

      telegram.setData(null);
      assertNull(telegram.getData());
   }

   @Test
   public void testFromRawData() throws InvalidDataException
   {
      final Telegram telegram = new Telegram();

      final int[] data1 = new int[] { 0xbc, 0x11, 0x01, 0x0a, 0x05, 0xe0, 0x00, 0x81 };

      telegram.fromRawData(data1, 0);
      assertFalse(telegram.isRepeated());
      assertEquals(Priority.LOW, telegram.getPriority());
      assertEquals(6, telegram.getRoutingCounter());
      assertEquals(new PhysicalAddress(1, 1, 1), telegram.getFrom());
      assertEquals(new GroupAddress(1, 2, 5), telegram.getDest());
      assertEquals(Application.GroupValue_Write, telegram.getApplication());
      assertEquals(1, telegram.getData()[0]);
   }

   @Test(expected = InvalidDataException.class)
   public void testFromRawDataWrongLen() throws InvalidDataException
   {
      final Telegram telegram = new Telegram();

      final int[] data1 = new int[] { 0x90, 0x33, 0x07, 0x00, 0x00, 0x64, 0x43, 0x40, 0x00, 0x12 };

      telegram.fromRawData(data1, 0);
   }

   @Test
   public void testToRawData()
   {
      final Telegram telegram = new Telegram();
      final int[] data = new int[1024];
      int len;

      telegram.setFrom(new PhysicalAddress(1, 1, 1));
      telegram.setDest(new GroupAddress(1, 2, 5));
      telegram.setRoutingCounter(3);
      telegram.setApplication(Application.GroupValue_Write);
      telegram.setData(new int[] { 1 });

      len = telegram.toRawData(data, 0);
      assertEquals(8, len);

      assertEquals(0xbc, data[0]);
      assertEquals(0x11, data[1]);
      assertEquals(0x01, data[2]);
      assertEquals(0x0a, data[3]);
      assertEquals(0x05, data[4]);
      assertEquals(0xb1, data[5]);
      assertEquals(0x00, data[6]);
      assertEquals(0x81, data[7]);
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
