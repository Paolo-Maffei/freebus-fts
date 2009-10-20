package org.freebus.fts.eib;


/**
 * The transport-types of EIB bus telegrams.
 * See KNX system specifications, volume 3.3.4, section 2, for details.
 */
public enum TransportType
{
   /**
    * A data telegram.
    *
    * This can be a broadcast, a group-send, or an individual request,
    * depending on the telegram's receiver address. 
    */
   DATA(0x00, 0xfc, false),

   /**
    * A connect request.
    */
   CONNECT(0x80, 0xff, false),

   /**
    * A disconnect request.
    */
   DISCONNECT(0x81, 0xff, false),

   /**
    * A connected-data telegram.
    * Contains a sequence number at bits 2..5 of the transport control field.
    */
   CONNECTED_DATA(0x40, 0xc0, true),

   /**
    * A positive reply.
    * Contains a sequence number at bits 2..5 of the transport control field.
    */
   ACK(0xc2, 0xc3, true),

   /**
    * A negative reply.
    * Contains a sequence number at bits 2..5 of the transport control field.
    */
   NAK(0xc3, 0xc3, true);
   
   /**
    * The bits of the transport control field
    */
   public final int bits;

   /**
    * The bit-mask for the transport control field.
    */
   public final int mask;

   /**
    * True if the telegram has a sequence number at bits 2..5 of the transport control field.
    */
   public final boolean hasSeq;

   /**
    * @return the transport-type for the given transport control field contents.
    */
   static public TransportType valueOf(int transportControlField)
   {
      for (TransportType e: values())
         if (e.bits == (transportControlField & e.mask)) return e;

      throw new IllegalArgumentException("Invalid transport control field contents: 0x" + Integer.toHexString(transportControlField));
   }

   /**
    * @return if the given transport control field contents is valid.
    */
   static public boolean isValid(int transportControlField)
   {
      for (TransportType e: values())
         if (e.bits == (transportControlField & e.mask)) return true;

      return false;
   }

   /*
    * Internal constructor
    */
   private TransportType(int bits, int mask, boolean hasSeq)
   {
      this.bits = bits;
      this.mask = mask;
      this.hasSeq = hasSeq;
   }
}
