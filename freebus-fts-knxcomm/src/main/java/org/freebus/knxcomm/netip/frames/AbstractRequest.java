package org.freebus.knxcomm.netip.frames;

import java.net.InetAddress;

import org.freebus.knxcomm.netip.blocks.HostProtAddrInfo;
import org.freebus.knxcomm.netip.types.ProtocolType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A abstract request that consists only of one {@link HostProtAddrInfo} block
 * and can be used as a base class for most request types.
 */
public abstract class AbstractRequest extends AbstractFrame
{
   private final HostProtAddrInfo hpai;

   /**
    * Create a request object.
    * 
    * @param protocol - the protocol type.
    * @param addr - the address of the sender.
    * @param port - the port of the sender
    */
   public AbstractRequest(ProtocolType protocol, InetAddress addr, int port)
   {
      hpai = new HostProtAddrInfo(protocol, addr, port);
   }

   /**
    * Create an empty request object.
    */
   public AbstractRequest()
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
   public int bodyFromData(int[] data, int start) throws InvalidDataException
   {
      return hpai.fromData(data, start);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int bodyToData(int[] data, int start)
   {
      return hpai.toData(data, start);
   }
}
