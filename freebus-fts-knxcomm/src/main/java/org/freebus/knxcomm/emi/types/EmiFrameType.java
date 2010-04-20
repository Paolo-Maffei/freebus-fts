package org.freebus.knxcomm.emi.types;

import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.L_Busmon_ind;
import org.freebus.knxcomm.emi.L_Data_con;
import org.freebus.knxcomm.emi.L_Data_ind;
import org.freebus.knxcomm.emi.L_Data_req;
import org.freebus.knxcomm.emi.L_Poll_Data_req;
import org.freebus.knxcomm.emi.PEI_Identify_con;
import org.freebus.knxcomm.emi.PEI_Identify_req;
import org.freebus.knxcomm.emi.PEI_Switch_req;

/**
 * External Message Interface (EMI) frame types.
 */
public enum EmiFrameType
{
   /**
    * A telegram in bus monitor mode was received.
    */
   L_BUSMON_IND(0x2b, EmiFrameClass.RECEIVE, L_Busmon_ind.class),

   /**
    * Request to send plain data.
    */
   L_PLAIN_DATA_REQ(0x10, EmiFrameClass.SEND),

   /**
    * Link data send request.
    */
   L_DATA_REQ(0x11, EmiFrameClass.SEND, L_Data_req.class),

   /**
    * Link data send confirmation.
    */
   L_DATA_CON(0x2e, EmiFrameClass.CONFIRM, L_Data_con.class, L_DATA_REQ),

   /**
    * Link data receive indication.
    */
   L_DATA_IND(0x29, EmiFrameClass.RECEIVE, L_Data_ind.class),

   /**
    * Poll data request.
    */
   L_POLL_DATA_REQ(0x13, EmiFrameClass.SEND, L_Poll_Data_req.class),

   /**
    * Poll data send confirmation.
    */
   L_POLL_DATA_CON(0x25, EmiFrameClass.CONFIRM, null, L_POLL_DATA_REQ),

   /**
    * Raw data send request. cEMI frames only.
    */
   L_RAW_REQ(0x10, EmiFrameClass.SEND),

   /**
    * Raw data receive indication. cEMI frames only.
    */
   L_RAW_IND(0x2d, EmiFrameClass.RECEIVE),

   /**
    * Raw data send confirmation. cEMI frames only.
    */
   L_RAW_CON(0x2f, EmiFrameClass.CONFIRM),

   N_DATA_INDIVIDUAL_REQ(0x21, EmiFrameClass.SEND),

   N_DATA_INDIVIDUAL_CON(0x4e, EmiFrameClass.CONFIRM, null, N_DATA_INDIVIDUAL_REQ),

   N_DATA_INDIVIDUAL_IND(0x49, EmiFrameClass.RECEIVE),

   N_DATA_GROUP_REQ(0x22, EmiFrameClass.SEND),

   N_DATA_GROUP_CON(0x3e, EmiFrameClass.CONFIRM, null, N_DATA_GROUP_REQ),

   N_DATA_GROUP_IND(0x3a, EmiFrameClass.RECEIVE),

   N_DATA_BROADCAST_REQ(0x2c, EmiFrameClass.SEND),

   N_DATA_BROADCAST_CON(0x4f, EmiFrameClass.CONFIRM, null, N_DATA_BROADCAST_REQ),

   N_DATA_BROADCAST_IND(0x4d, EmiFrameClass.RECEIVE),

   N_POLL_DATA_REQ(0x23, EmiFrameClass.SEND),

   N_POLL_DATA_CON(0x35, EmiFrameClass.CONFIRM, null, N_POLL_DATA_REQ),

   T_CONNECT_REQ(0x43, EmiFrameClass.SEND),

   T_CONNECT_CON(0x86, EmiFrameClass.CONFIRM, null, T_CONNECT_REQ),

   T_CONNECT_IND(0x85, EmiFrameClass.RECEIVE),

   T_DISCONNECT_REQ(0x44, EmiFrameClass.SEND),

   T_DISCONNECT_CON(0x88, EmiFrameClass.CONFIRM, null, T_DISCONNECT_REQ),

   T_DISCONNECT_IND(0x87, EmiFrameClass.RECEIVE),

   U_VALUE_READ_REQ(0x74, EmiFrameClass.SEND),

   U_VALUE_READ_CON(0xe4, EmiFrameClass.CONFIRM),

   U_FLAGS_READ_REQ(0x7c, EmiFrameClass.SEND),

   U_FLAGS_READ_CON(0xec, EmiFrameClass.CONFIRM),

   U_EVENT_IND(0xe7, EmiFrameClass.RECEIVE),

   U_VALUE_WRITE_REQ(0x71, EmiFrameClass.SEND),

   /**
    * Physical external interface
    */
   PEI_IDENTIFY_REQ(0xa7, EmiFrameClass.SEND, PEI_Identify_req.class),

   PEI_IDENTIFY_CON(0xa8, EmiFrameClass.CONFIRM, PEI_Identify_con.class, PEI_IDENTIFY_REQ),

   PEI_SWITCH_REQ(0xa9, EmiFrameClass.SEND, PEI_Switch_req.class),

   /**
    * Timer
    */
   TM_TIMER_IND(0xc1, EmiFrameClass.RECEIVE),

   /**
    * An unknown message (this is an internal type)
    */
   UNKNOWN(0x00, null);

   /**
    * The message-type code.
    */
   public final int code;

   /**
    * The classification of the frame.
    */
   public final EmiFrameClass frameClass;

   /**
    * The message-type for which this message-type is a confirmation. E.g. for
    * L_DATA_CON is a confirmation for L_DATA_REQ. Null if this is no
    * confirmation message-type.
    */
   public final EmiFrameType confirmationForType;

   /**
    * The class that is used for messages of this type. May be null.
    */
   public final Class<? extends EmiFrame> clazz;

   /**
    * @return true if the message-type is a confirmation for another message
    *         type.
    */
   public boolean isConfirmation()
   {
      return frameClass == EmiFrameClass.CONFIRM;
   }

   /**
    * @return a human readable label for the frame type.
    */
   public String getLabel()
   {
      int pos = name().lastIndexOf('_');
      if (pos < 1)
         return name();

      return name().substring(0, pos) + '.' + name().substring(pos + 1).toLowerCase();
   }

   /**
    * Lookup the EMI frame type for a frame type code.
    *
    * @param code - the frame type code
    * @return the message-code for the given id.
    *
    * @throws IllegalArgumentException if the frame type code is invalid
    */
   static public EmiFrameType valueOf(int code)
   {
      for (EmiFrameType e : values())
         if (e.code == code)
            return e;

      throw new IllegalArgumentException("Unknown EMI frame type code 0x" + Integer.toHexString(code));
   }

   /**
    * Test if the frame type code is valid.
    *
    * @param code - the frame type code to test.
    * @return true if the given code is valid.
    */
   static public boolean isValid(int code)
   {
      for (EmiFrameType e : values())
         if (e.code == code)
            return true;

      return false;
   }

   /*
    * Internal constructor
    */
   private EmiFrameType(int code, EmiFrameClass frameClass, Class<? extends EmiFrame> clazz, EmiFrameType confirmationForType)
   {
      this.code = code;
      this.frameClass = frameClass;
      this.clazz = clazz;
      this.confirmationForType = confirmationForType;
   }

   /*
    * Internal constructor
    */
   private EmiFrameType(int code, EmiFrameClass frameClass, Class<? extends EmiFrame> clazz)
   {
      this(code, frameClass, clazz, null);
   }

   /*
    * Internal constructor
    */
   private EmiFrameType(int code, EmiFrameClass frameClass)
   {
      this(code, frameClass, null, null);
   }
}
