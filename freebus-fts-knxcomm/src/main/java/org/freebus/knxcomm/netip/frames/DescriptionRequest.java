package org.freebus.knxcomm.netip.frames;

import java.net.InetAddress;

import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.netip.types.TransportType;

/**
 * Request a description of the KNXnet/IP server.
 */
public class DescriptionRequest extends AbstractEndPointFrame
{
   /**
    * Create a description request object.
    *
    * @param type - the transport type of the sender.
    * @param addr - the address of the sender.
    * @param port - the port of the sender.
    */
   public DescriptionRequest(TransportType type, InetAddress addr, int port)
   {
      super(type, addr, port);
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
