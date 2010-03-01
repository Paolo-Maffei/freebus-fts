package org.freebus.knxcomm;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.PEI_Identify_req;
import org.freebus.knxcomm.internal.SimulatedFt12Connection;
import org.freebus.knxcomm.serial.Ft12FrameFormat;
import org.freebus.knxcomm.serial.Ft12Function;
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
   public final void testSendVariable() throws IOException
   {
      con.setReadableData(ackData);
      con.open();

      final EmiFrame emiFrame = new PEI_Identify_req();
      con.setReadableData(ackData);
      con.send(emiFrame);

      final int[] data = con.getWrittenData();

      assertEquals(8, data.length);
      assertEquals(Ft12FrameFormat.VARIABLE.code, data[0]);
      assertEquals(2, data[1]);
      assertEquals(data[1], data[2]);
      assertEquals(Ft12FrameFormat.VARIABLE.code, data[3]);
      assertEquals(0x16, data[data.length - 1]);

      int checksum = 0;
      for (int i = 4; i < data.length - 2; ++i)
         checksum += data[i];
      assertEquals(checksum & 0xff, data[data.length - 2]);
   }

   @Test
   public final void testSendFixed() throws IOException
   {
      con.setReadableData(ackData);
      con.open();

      con.setReadableData(ackData);
      con.send(Ft12Function.STATUS_REQ);

      final int[] data = con.getWrittenData();
      assertEquals(4, data.length);
      assertEquals(Ft12FrameFormat.FIXED.code, data[0]);
      assertEquals(Ft12Function.STATUS_REQ.code, data[1]);
      assertEquals(data[1], data[2]);
      assertEquals(0x16, data[3]);
   }

   @Test
   public final void testProcessFixedFrame() throws IOException
   {
      con.processFrame(Ft12FrameFormat.FIXED, Ft12Function.ACK, null);
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testProcessFrameNullFormat() throws IOException
   {
      con.processFrame(null, null, null);
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testProcessFrameNullFunc() throws IOException
   {
      con.processFrame(Ft12FrameFormat.FIXED, null, null);
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testProcessFrameInvalidFunction() throws IOException
   {
      con.processFrame(Ft12FrameFormat.FIXED, Ft12Function.RESET, null);
   }
}
