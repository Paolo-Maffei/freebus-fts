package org.freebus.knxcomm.serial;

/**
 * Types of FT1.2 short (1-byte code) messages
 */
public enum Ft12ShortMessage
{
   /**
    * Reset the BCU.
    */
   RESET(0x40),

   /**
    * Status Request.
    */
   STATUS_REQ(0x49);

   /**
    * The message code byte
    */
   final public int code;

   /*
    * Internal constructor
    */
   private Ft12ShortMessage(int code)
   {
      this.code = code;
   }
}
