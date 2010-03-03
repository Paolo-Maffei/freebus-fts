package org.freebus.knxcomm.netip.types;


/**
 * KNXnet/IP error codes.
 */
public final class ErrorCode
{
   /**
    * Successful operation.
    */
   public final static int OK = 0;

   /**
    * The requested host protocol is not supported by the KNXnet/IP server.
    */
   public final static int HOST_PROTOCOL_TYPE = 1;

   /**
    * The requested protocol version is not supported by the KNXnet/IP server.
    */
   public final static int VERSION_NOT_SUPPORTED = 2;

   /**
    * The received sequence number is out of order. 
    */
   public final static int SEQUENCE_NUMBER = 4;

   /*
    * Must not create objects of this class.
    */
   private ErrorCode()
   {
   }
}
