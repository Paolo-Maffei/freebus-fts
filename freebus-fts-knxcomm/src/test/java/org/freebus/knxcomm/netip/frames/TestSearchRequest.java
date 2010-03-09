package org.freebus.knxcomm.netip.frames;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Inet4Address;

import org.freebus.knxcomm.netip.types.ProtocolType;
import org.junit.Test;

public class TestSearchRequest
{
   @Test
   public final void testSearchRequest()
   {
      final SearchRequest req = new SearchRequest();
      assertNotNull(req.getHostProtAddrInfo());

      assertFalse(req.equals(null));
      assertFalse(req.equals(new Object()));
      assertTrue(req.equals(req));
   }

   @Test
   public final void testToFromRawData() throws IOException
   {
      final int[] data = new int[256];
      final SearchRequest req = new SearchRequest(ProtocolType.IPv4_UDP, Inet4Address.getLocalHost(), 1234);

      final int wlen = req.toData(data);
      assertEquals(14, wlen);

      assertEquals(0x06, data[0]); // header size

      final SearchRequest reqParsed = (SearchRequest) FrameFactory.createFrame(data);
      assertNotNull(reqParsed);

      assertEquals(req.getHostProtAddrInfo(), reqParsed.getHostProtAddrInfo());
   }
}
