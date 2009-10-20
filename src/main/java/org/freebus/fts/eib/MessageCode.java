package org.freebus.fts.eib;

/**
 * Message codes of EIB telegrams.
 * 
 * Taken from KNX Standard 3.6.3
 */
public enum MessageCode
{
   UNKNOWN(0),
   L_BUSMON_IND(0x2b),
   L_PLAIN_DATA_REQ(0x10),
   L_DATA_REQ(0x11),
   L_DATA_CON(0x2e),
   L_DATA_IND(0x29),
   L_POLL_DATA_REQ(0x13),
   L_POLL_DATA_CON(0x25),
   N_DATA_INDIVIDUAL_REQ(0x21),
   N_DATA_INDIVIDUAL_CON(0x4e),
   N_DATA_INDIVIDUAL_IND(0x49),
   N_DATA_GROUP_REQ(0x22),
   N_DATA_GROUP_CON(0x3e),
   N_DATA_GROUP_IND(0x3a),
   N_DATA_BROADCAST_REQ(0x2c),
   N_DATA_BROADCAST_CON(0x4f),
   N_DATA_BROADCAST_IND(0x4d),
   N_POLL_DATA_REQ(0x23),
   N_POLL_DATA_CON(0x35),
   T_CONNECT_REQ(0x43),
   T_CONNECT_CON(0x86),
   T_CONNECT_IND(0x85),
   T_DISCONNECT_REQ(0x44),
   T_DISCONNECT_CON(0x88),
   T_DISCONNECT_IND(0x87);

   /**
    * The message-code.
    */
   public final int id;

   /**
    * @return the message-code for the given id.
    */
   static public MessageCode valueOf(int id)
   {
      for (MessageCode e: values())
         if (e.id == id) return e;

      throw new IllegalArgumentException("Invalid message-code 0x" + Integer.toHexString(id));
   }

   /**
    * @return if the given transport control field contents is valid.
    */
   static public boolean isValid(int id)
   {
      for (MessageCode e: values())
         if (e.id == id) return true;

      return false;
   }

   /*
    * Internal constructor
    */
   private MessageCode(int id)
   {
      this.id = id;
   }
}
