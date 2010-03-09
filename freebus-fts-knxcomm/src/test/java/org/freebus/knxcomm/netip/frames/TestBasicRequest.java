package org.freebus.knxcomm.netip.frames;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.freebus.knxcomm.netip.blocks.HostProtAddrInfo;
import org.freebus.knxcomm.netip.types.ProtocolType;
import org.junit.Test;

public class TestBasicRequest
{
   @Test
   public final void testBasicRequest()
   {
      final AbstractRequest req = new SearchRequest();
      assertNotNull(req.getHostProtAddrInfo());
   }

   @Test
   public final void testBasicRequestProtocolTypeInetAddressInt() throws UnknownHostException
   {
      final InetAddress addr = Inet4Address.getLocalHost();
      final SearchRequest req = new SearchRequest(ProtocolType.IPv4_TCP, addr, 0x1234);

      final HostProtAddrInfo hpai = req.getHostProtAddrInfo();
      assertNotNull(hpai);
      assertEquals(ProtocolType.IPv4_TCP, hpai.getProtocolType());
      final int[] data = hpai.getData();
      assertNotNull(data);
      for (int i = 0; i < 4; ++i)
         assertEquals(addr.getAddress()[i], data[i]);
      assertEquals(0x12, data[4]);
      assertEquals(0x34, data[5]);
   }

   @Test
   public final void testFromToRawData() throws IOException
   {
      final int[] data = new int[] { 0x06, 0x10, 0x02, 0x01, 0x00, 0x0e, 0x08, 0x02, 0x7f, 0x00, 0x01, 0x01, 0x34, 0x56 };

      final Frame frame = FrameFactory.createFrame(data);
      assertEquals(SearchRequest.class, frame.getClass());

      final int[] dataOut = new int[256];
      final int wlen = frame.toData(dataOut);
      final int[] dataOutTrimmed = Arrays.copyOfRange(dataOut, 0, wlen);

      assertEquals(data.length, wlen);
      assertArrayEquals(data, dataOutTrimmed);
   }
}
