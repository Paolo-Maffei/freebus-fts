package org.freebus.knxcomm.serial;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.freebus.fts.common.HexString;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.PEI_Identify_req;
import org.freebus.knxcomm.emi.PEI_Switch_req;
import org.freebus.knxcomm.internal.SimulatedFt12Connection;
import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFt12Connection
{
   private SimulatedFt12Connection con;
   private final static byte[] ackData = HexString.valueOf("e5 e5 e5 e5 e5");

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

   @Test(expected = InvalidDataException.class)
   public final void testReadInvalidFrame() throws IOException
   {
      con.setReadableData(HexString.valueOf("01 01 01 16"));
      con.open();

      con.dataAvailable();
   }

   @Test
   public final void testReadAck() throws IOException
   {
      con.setReadableData(HexString.valueOf("e5"));
      con.open();

      con.dataAvailable();
   }

   @Test
   public final void testReadFixedFrame() throws IOException
   {
      con.setReadableData(HexString.valueOf("10 01 01 16"));
      con.open();
      con.dataAvailable();
   }

   @Test
   public final void testReadVariableFrame() throws IOException
   {
      con.setReadableData(HexString.valueOf("68 02 02 68 53 a7 fa 16"));
      con.open();
      con.dataAvailable();
   }

   @Test(expected = InvalidDataException.class)
   public final void testReadVariableFrameInvalidFunction() throws IOException
   {
      con.processVariableFrame(Ft12Function.RESET, new byte[0]);
   }

   @Test(expected = InvalidDataException.class)
   public final void testReadVariableFrameInvalidMark() throws IOException
   {
      con.setReadableData(HexString.valueOf("68 02 02 11 53 a7 fa 16"));
      con.open();
      con.dataAvailable();
   }

   @Test(expected = InvalidDataException.class)
   public final void testReadVariableFrameInvalidChecksum() throws IOException
   {
      con.setReadableData(HexString.valueOf("68 02 02 68 53 a7 00 16"));
      con.open();
      con.dataAvailable();
   }

   @Test(expected = InvalidDataException.class)
   public final void testReadVariableFrameInvalidEof() throws IOException
   {
      con.setReadableData(HexString.valueOf("68 02 02 68 53 a7 fa 00"));
      con.open();
      con.dataAvailable();
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testReadFixedFrameInvalidFunc() throws IOException
   {
      con.setReadableData(HexString.valueOf("10 ff ff 16"));
      con.open();

      con.dataAvailable();
   }

   @Test
   public final void testSendVariable() throws IOException
   {
      con.setReadableData(ackData);
      con.open();

      final EmiFrame emiFrame = new PEI_Identify_req();
      con.setReadableData(ackData);
      con.send(emiFrame);

      final byte[] data = con.getWrittenData();
      Logger.getLogger(getClass()).debug("written: " + HexString.toString(data));

      assertEquals(8, data.length);
      assertEquals(Ft12FrameFormat.VARIABLE.code, data[0]);
      assertEquals(2, data[1]);
      assertEquals(data[1], data[2]);
      assertEquals(Ft12FrameFormat.VARIABLE.code, data[3]);
      assertEquals(0x16, data[data.length - 1]);

      int checksum = 0;
      for (int i = 4; i < data.length - 2; ++i)
         checksum += data[i];
      assertEquals(checksum & 255, data[data.length - 2] & 255);
   }

   @Test
   public final void testSendFixed() throws IOException
   {
      con.setReadableData(ackData);
      con.open();

      con.setReadableData(ackData);
      con.send(Ft12Function.STATUS_REQ);

      final byte[] data = con.getWrittenData();
      assertEquals(4, data.length);
      assertEquals(Ft12FrameFormat.FIXED.code, data[0]);
      assertEquals(Ft12Function.STATUS_REQ.code, data[1]);
      assertEquals(data[1], data[2]);
      assertEquals(0x16, data[3]);
   }

   @Test(expected = IOException.class)
   public final void testSendFixedNoAck() throws IOException
   {
      con.setReadableData(null);
      con.open();

      con.send(Ft12Function.STATUS_RESP);
   }

   @Test(expected = IOException.class)
   public final void testSendVariableNoAck() throws IOException
   {
      con.setReadableData(null);
      con.open();

      final EmiFrame emiFrame = new PEI_Switch_req();
      con.send(emiFrame);
   }
}
