package org.freebus.knxcomm.serial;

/**
 * Types of FT1.2 message frames
 */
public enum Ft12FrameFormat
{
   /**
    * Fixed length (short) message
    * 
    * @see {@link Ft12ShortMessage} for short message types.
    */
   FIXED(0x10),

   /**
    * Variable length (long) message
    */
   VARIABLE(0x68),

   /**
    * Acknowledge (in reply to a sent short or long message)
    */
   ACK(0xe5);

   /**
    * The message code byte
    */
   public final int code;

   /**
    * Lookup the message type
    * 
    * @param code the message code byte
    * @return the FT1.2 message type, or null if no matching type is found.
    */
   static public Ft12FrameFormat valueOf(int code)
   {
      for (Ft12FrameFormat type : values())
      {
         if (type.code == code)
            return type;
      }

      return null;
   }

   /*
    * Internal constructor
    */
   private Ft12FrameFormat(int code)
   {
      this.code = code;
   }
}
