package org.freebus.fts.emi;



/**
 * External Message Interface (EMI) message types.
 * 
 * Taken from KNX Standard 3.6.3 "External Message Interface"
 */
public enum EmiMessageType
{
   L_BUSMON_IND(0x2b, null, null),
   L_PLAIN_DATA_REQ(0x10, null, null),
   
   /**
    * Link data. Classes {@link L_Data.req}
    */
   L_DATA_REQ(0x11, L_Data.req.class, null),
   L_DATA_CON(0x2e, L_Data.con.class, L_DATA_REQ),
   L_DATA_IND(0x29, L_Data.ind.class, null),

   L_POLL_DATA_REQ(0x13, L_Poll_Data.req.class, null),
   L_POLL_DATA_CON(0x25, null, L_POLL_DATA_REQ),

   N_DATA_INDIVIDUAL_REQ(0x21, null, null),
   N_DATA_INDIVIDUAL_CON(0x4e, null, N_DATA_INDIVIDUAL_REQ),
   N_DATA_INDIVIDUAL_IND(0x49, null, null),

   N_DATA_GROUP_REQ(0x22, null, null),
   N_DATA_GROUP_CON(0x3e, null, N_DATA_GROUP_REQ),
   N_DATA_GROUP_IND(0x3a, null, null),

   N_DATA_BROADCAST_REQ(0x2c, null, null),
   N_DATA_BROADCAST_CON(0x4f, null, N_DATA_BROADCAST_REQ),
   N_DATA_BROADCAST_IND(0x4d, null, null),

   N_POLL_DATA_REQ(0x23, null, null),
   N_POLL_DATA_CON(0x35, null, N_POLL_DATA_REQ),

   T_CONNECT_REQ(0x43, null, null),
   T_CONNECT_CON(0x86, null, T_CONNECT_REQ),
   T_CONNECT_IND(0x85, null, null),

   T_DISCONNECT_REQ(0x44, null, null),
   T_DISCONNECT_CON(0x88, null, T_DISCONNECT_REQ),
   T_DISCONNECT_IND(0x87, null, null),
   
   /**
    * Physical external interface
    */
   PEI_IDENTIFY_REQ(0xa7, PEI_Identify.req.class, null),
   PEI_IDENTIFY_CON(0xa8, PEI_Identify.con.class, PEI_IDENTIFY_REQ),
   PEI_SWITCH_REQ(0xa9, PEI_Switch.req.class, null),

   /**
    * Timer
    */
   TM_TIMER_IND(0xc1, null, null),

   /**
    * An unknown message (this is an internal type)
    */
   UNKNOWN(0, null, null);

   /**
    * The message-type id.
    */
   public final int id;

   /**
    * The message-type for which this message-type is
    * a confirmation. E.g. for L_DATA_CON is a confirmation for L_DATA_REQ.
    * Null if this is no confirmation message-type.
    */
   public final EmiMessageType confirmationForType;

   /**
    * The class that is used for messages of this type. May be null.
    */
   public final Class<? extends EmiMessage> clazz;

   /**
    * @return true if the message-type is a confirmation for another message type.
    */
   public boolean isConfirmation()
   {
      return confirmationForType != null;
   }

   /**
    * @return the message-code for the given id.
    */
   static public EmiMessageType valueOf(int id)
   {
      for (EmiMessageType e: values())
         if (e.id == id) return e;

      throw new IllegalArgumentException("Invalid message-code 0x" + Integer.toHexString(id));
   }

   /**
    * @return if the given transport control field contents is valid.
    */
   static public boolean isValid(int id)
   {
      for (EmiMessageType e: values())
         if (e.id == id) return true;

      return false;
   }

   /**
    * Create a new instance of the corresponding message class.
    * @return the created object, or null if clazz is null.
    */
   public EmiMessage newInstance()
   {
      if (clazz == null) return null;
      try
      {
         return clazz.newInstance();
      }
      catch (InstantiationException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (IllegalAccessException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }

   /*
    * Internal constructor
    */
   private EmiMessageType(int id, Class<? extends EmiMessage> clazz, EmiMessageType confirmationForType)
   {
      this.id = id;
      this.clazz = clazz;
      this.confirmationForType = confirmationForType;
   }
}
