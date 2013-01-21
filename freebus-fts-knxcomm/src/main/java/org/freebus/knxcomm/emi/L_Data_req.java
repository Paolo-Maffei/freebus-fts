package org.freebus.knxcomm.emi;

import org.freebus.knxcomm.emi.types.EmiFrameType;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * Link data request. This message type is used to send telegrams to the EIB
 * bus.
 */
public class L_Data_req extends EmiTelegramFrame
{
   /**
    * Create a link data request with the given telegram.
    */
   public L_Data_req(EmiFrameType type, Telegram telegram)
   {
      super(type, telegram);
   }

   /**
    * Create an empty link data request message.
    */
   public L_Data_req(EmiFrameType type)
   {
      super(type);
   }
}