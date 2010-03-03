package org.freebus.knxcomm.netip.types;

import org.freebus.knxcomm.netip.frames.DescriptionRequest;
import org.freebus.knxcomm.netip.frames.FrameBody;
import org.freebus.knxcomm.netip.frames.SearchRequest;
import org.freebus.knxcomm.netip.frames.SearchResponse;


/**
 * KNXnet/IP service types.
 */
public enum ServiceType
{
   /**
    * Sent by a client to search available KNXnet/IP servers. The server answers
    * with a {@link #SEARCH_RESPONSE}.
    */
   SEARCH_REQUEST(0x201, SearchRequest.class),

   /**
    * The response of a KNXnet/IP servers to a {@link #SEARCH_REQUEST}.
    */
   SEARCH_RESPONSE(0x202, SearchResponse.class),

   /**
    * KNXnet/IP client to server: request information about the server. The
    * server answers with a {@link #DESCRIPTION_RESPONSE}.
    */
   DESCRIPTION_REQUEST(0x203, DescriptionRequest.class),

   /**
    * KNXnet/IP server to client: the information about the server that was
    * requested with {@link #DESCRIPTION_REQUEST}.
    */
   DESCRIPTION_RESPONSE(0x204, null),

   /**
    * The KNXnet/IP client wants to connect to a server. The server answers with
    * a {@link #CONNECT_RESPONSE}.
    */
   CONNECT_REQUEST(0x205, null),

   /**
    * The KNXnet/IP server's reply to a connection request
    * {@link #CONNECT_REQUEST}.
    */
   CONNECT_RESPONSE(0x206, null),

   /**
    * The KNXnet/IP client to server: request information about an established
    * connection. The server answers with a {@link #CONNECTIONSTATE_RESPONSE}.
    */
   CONNECTIONSTATE_REQUEST(0x207, null),

   /**
    * KNXnet/IP server to client: the information about an established
    * connection that was requested by the client with a
    * {@link #CONNECTIONSTATE_REQUEST}.
    */
   CONNECTIONSTATE_RESPONSE(0x208, null),

   /**
    * The KNXnet/IP client to server: close an established connection. The
    * server answers with a {@link #DISCONNECT_RESPONSE}.
    */
   DISCONNECT_REQUEST(0x209, null),

   /**
    * KNXnet/IP server to client: the answer to the client's
    * {@link #DISCONNECT_REQUEST}.
    */
   DISCONNECT_RESPONSE(0x20a, null);

   /**
    * The service type code as used in the data frames.
    */
   public final int code;

   /**
    * Default class of the frame body of this type.
    */
   public final Class<? extends FrameBody> frameBodyClass;

   /**
    * Create an instance of the default frame body class of this type.
    * 
    * @return the created object, or null if no default class is set.
    */
   public FrameBody newFrameBodyInstance()
   {
      if (frameBodyClass == null)
         return null;

      try
      {
         return frameBodyClass.newInstance();
      }
      catch (InstantiationException e)
      {
         e.printStackTrace();
      }
      catch (IllegalAccessException e)
      {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * @return the service type for the given value.
    * @throws IllegalArgumentException if no matching service type is found.
    */
   static public ServiceType valueOf(int code)
   {
      for (ServiceType v : values())
      {
         if (v.code == code)
            return v;
      }

      throw new IllegalArgumentException("Invalid KNXnet/IP service type: " + code);
   }

   /*
    * Internal constructor.
    */
   private ServiceType(int code, Class<? extends FrameBody> frameBodyClass)
   {
      this.code = code;
      this.frameBodyClass = frameBodyClass;
   }
}
