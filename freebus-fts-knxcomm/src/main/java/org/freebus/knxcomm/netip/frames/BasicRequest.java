package org.freebus.knxcomm.netip.frames;

import java.net.InetAddress;

import org.freebus.knxcomm.netip.blocks.HostProtAddrInfo;
import org.freebus.knxcomm.netip.types.ProtocolType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A basic request structure that consists only of one {@link HostProtAddrInfo}
 * block.
 */
public abstract class BasicRequest implements FrameBody
{
   private final HostProtAddrInfo hpai;

   /**
    * Create a request object.
    * 
    * @param protocol - the protocol type.
    * @param addr - the address of the sender.
    * @param port - the port of the sender
    */
   public BasicRequest(ProtocolType protocol, InetAddress addr, int port)
   {
      hpai = new HostProtAddrInfo(protocol, addr, port);
   }

   /**
    * Create an empty request object.
    */
   public BasicRequest()
   {
      hpai = new HostProtAddrInfo();
   }

   /**
    * @return the host protocol address info object.
    */
   public HostProtAddrInfo getHostProtAddrInfo()
   {
      return hpai;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int fromRawData(int[] rawData, int start) throws InvalidDataException
   {
      return hpai.fromRawData(rawData, start);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      return hpai.toRawData(rawData, start);
   }

}
