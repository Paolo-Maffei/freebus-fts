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
   L_BUSMON_IND(0x2b, L_Busmon_ind.class), L_PLAIN_DATA_REQ(0x10),

   /**
    * Link data send request.
    */
   L_DATA_REQ(0x11, L_Data_req.class),

   /**
    * Link data send confirmation.
    */
   L_DATA_CON(0x2e, L_Data_con.class, L_DATA_REQ),

   /**
    * Link data receive indication.
    */
   L_DATA_IND(0x29, L_Data_ind.class),

   L_POLL_DATA_REQ(0x13, L_Poll_Data_req.class), L_POLL_DATA_CON(0x25, null, L_POLL_DATA_REQ),

   /**
    * Raw data send request. cEMI frames only.
    */
   L_RAW_REQ(0x10),

   /**
    * Raw data receive indication. cEMI frames only.
    */
   L_RAW_IND(0x2d),

   /**
    * Raw data send confirmation. cEMI frames only.
    */
   L_RAW_CON(0x2f),

   N_DATA_INDIVIDUAL_REQ(0x21), N_DATA_INDIVIDUAL_CON(0x4e, null, N_DATA_INDIVIDUAL_REQ), N_DATA_INDIVIDUAL_IND(0x49),

   N_DATA_GROUP_REQ(0x22), N_DATA_GROUP_CON(0x3e, null, N_DATA_GROUP_REQ), N_DATA_GROUP_IND(0x3a),

   N_DATA_BROADCAST_REQ(0x2c), N_DATA_BROADCAST_CON(0x4f, null, N_DATA_BROADCAST_REQ), N_DATA_BROADCAST_IND(0x4d),

   N_POLL_DATA_REQ(0x23), N_POLL_DATA_CON(0x35, null, N_POLL_DATA_REQ),

   T_CONNECT_REQ(0x43), T_CONNECT_CON(0x86, null, T_CONNECT_REQ), T_CONNECT_IND(0x85),

   T_DISCONNECT_REQ(0x44), T_DISCONNECT_CON(0x88, null, T_DISCONNECT_REQ), T_DISCONNECT_IND(0x87),

   U_VALUE_READ_REQ(0x74), U_VALUE_READ_CON(0xe4),

   U_FLAGS_READ_REQ(0x7c), U_FLAGS_READ_CON(0xec),

   U_EVENT_IND(0xe7), U_VALUE_WRITE_REQ(0x71),

   /**
    * Physical external interface
    */
   PEI_IDENTIFY_REQ(0xa7, PEI_Identify_req.class), PEI_IDENTIFY_CON(0xa8, PEI_Identify_con.class, PEI_IDENTIFY_REQ), PEI_SWITCH_REQ(
         0xa9, PEI_Switch_req.class),

   /**
    * Timer
    */
   TM_TIMER_IND(0xc1),

   /**
    * An unknown message (this is an internal type)
    */
   UNKNOWN(0x00);

   /**
    * The message-type code.
    */
   public final int code;

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
      return confirmationForType != null;
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
   private EmiFrameType(int code, Class<? extends EmiFrame> clazz, EmiFrameType confirmationForType)
   {
      this.code = code;
      this.clazz = clazz;
      this.confirmationForType = confirmationForType;
   }

   /*
    * Internal constructor
    */
   private EmiFrameType(int code, Class<? extends EmiFrame> clazz)
   {
      this(code, clazz, null);
   }

   /*
    * Internal constructor
    */
   private EmiFrameType(int code)
   {
      this(code, null, null);
   }
}
