package org.freebus.knxcomm.netip.frames;

import java.net.InetAddress;

import org.freebus.knxcomm.netip.types.ProtocolType;
import org.freebus.knxcomm.netip.types.ServiceType;

/**
 * A request for searching KNXnet/IP servers.
 */
public class SearchRequest extends AbstractRequest
{
   /**
    * Create a search request object.
    * 
    * @param protocol - the protocol type.
    * @param addr - the address of the sender.
    * @param port - the port of the sender
    */
   public SearchRequest(ProtocolType protocol, InetAddress addr, int port)
   {
      super(protocol, addr, port);
   }

   /**
    * Create a description request object.
    */
   public SearchRequest()
   {
      super();
   }
 
   /**
    * @return {@link ServiceType#SEARCH_REQUEST}.
    */
   @Override
   public ServiceType getServiceType()
   {
      return ServiceType.SEARCH_REQUEST;
   }
}
