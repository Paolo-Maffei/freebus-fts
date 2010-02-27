package org.freebus.knxcomm;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.PEI_Identify_req;
import org.freebus.knxcomm.internal.SimulatedFt12Connection;
import org.freebus.knxcomm.serial.Ft12MessageType;
import org.freebus.knxcomm.serial.Ft12ShortMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFt12Connection
{
   private SimulatedFt12Connection con;
   private final static int[] ackData = new int[] { 0xe5, 0xe5, 0xe5, 0xe5, 0xe5 };

   static
   {
      // Configure Log4J
      BasicConfigurator.configure();
   }

   @Before
   public final void setUp()
   {
      con = new SimulatedFt12Connection();
   }

   @After
   public final void tearDown()
   {
      if (con != null)
      {
         con.close();
         con = null;
      }
   }

   @Test
   public final void testOpen() throws IOException
   {
      con.setReadableData(ackData);
      con.open();
   }

   @Test
   public final void testSendLong() throws IOException
   {
      con.setReadableData(ackData);
      con.open();

      final EmiFrame emiFrame = new PEI_Identify_req();
      con.setReadableData(ackData);
      con.send(emiFrame);

      final int[] data = con.getWrittenData();
      assertEquals(8, data.length);
      assertEquals(Ft12MessageType.LONG.code, data[0]);
      assertEquals(2, data[1]);
      assertEquals(2, data[2]);
      assertEquals(Ft12MessageType.LONG.code, data[3]);
      assertEquals(0x16, data[data.length - 1]);
   }

   @Test
   public final void testSendShort() throws IOException
   {
      con.setReadableData(ackData);
      con.open();

      con.setReadableData(ackData);
      con.send(Ft12ShortMessage.STATUS_REQ);

      final int[] data = con.getWrittenData();
      assertEquals(4, data.length);
      assertEquals(Ft12MessageType.SHORT.code, data[0]);
      assertEquals(Ft12ShortMessage.STATUS_REQ.code, data[1]);
      assertEquals(Ft12ShortMessage.STATUS_REQ.code, data[2]);
      assertEquals(0x16, data[3]);
   }
}
