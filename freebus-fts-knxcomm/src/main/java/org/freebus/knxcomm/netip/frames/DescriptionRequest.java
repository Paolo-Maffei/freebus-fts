package org.freebus.knxcomm.netip.frames;

import java.net.InetAddress;

import org.freebus.knxcomm.netip.types.ProtocolType;
import org.freebus.knxcomm.netip.types.ServiceType;

/**
 * Request a description of the KNXnet/IP server.
 */
public class DescriptionRequest extends AbstractRequest
{
   /**
    * Create a request object.
    * 
    * @param protocol - the protocol type.
    * @param addr - the address of the sender.
    * @param port - the port of the sender
    */
   public DescriptionRequest(ProtocolType protocol, InetAddress addr, int port)
   {
      super(protocol, addr, port);
   }

   /**
    * Create a description request object.
    */
   public DescriptionRequest()
   {
      super();
   }

   /**
    * @return {@link ServiceType#DESCRIPTION_REQUEST}.
    */
   @Override
   public ServiceType getServiceType()
   {
      return ServiceType.DESCRIPTION_REQUEST;
   }
}
