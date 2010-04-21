package org.freebus.knxcomm.netip.frames;

import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.netip.types.StatusCode;

/**
 * Response to a {@link ConnectionStateRequest connection state request}.
 *
 * @see ConnectionStateRequest
 */
public class ConnectionStateResponse extends AbstractConnectionResponse
{
   /**
    * Create a connection state response object.
    *
    * @param channelId - the ID if the communication channel that is queried.
    * @param status - the status code.
    */
   public ConnectionStateResponse(int channelId, StatusCode status)
   {
      super(channelId, status);
   }

   /**
    * Create a connection state response object.
    */
   public ConnectionStateResponse()
   {
      super();
   }

   /**
    * @return {@link ServiceType#CONNECTIONSTATE_RESPONSE}.
    */
   @Override
   public ServiceType getServiceType()
   {
      return ServiceType.CONNECTIONSTATE_RESPONSE;
   }
}
