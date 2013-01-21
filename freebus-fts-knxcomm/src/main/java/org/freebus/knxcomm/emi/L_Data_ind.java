package org.freebus.knxcomm.emi;

import org.freebus.knxcomm.emi.types.EmiFrameType;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * Link data indicator. This message contains a telegram that
 * was received from the EIB bus by the bus-interface.
 */
public class L_Data_ind extends EmiTelegramFrame
{
   /**
    * Create a link data indicator with the given telegram.
    */
   public L_Data_ind(EmiFrameType type, Telegram telegram)
   {
      super(type, telegram);
   }

   /**
    * Create an empty link data indicator message.
    */
   public L_Data_ind(EmiFrameType type)
   {
      super(type);
   }
}