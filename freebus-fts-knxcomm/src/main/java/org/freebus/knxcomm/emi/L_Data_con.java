package org.freebus.knxcomm.emi;

import org.freebus.knxcomm.emi.types.EmiFrameType;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * Link data request confirmation.
 */
public class L_Data_con extends EmiTelegramFrame
{
   /**
    * Create a link data confirmation with the given telegram.
    */
   public L_Data_con(Telegram telegram)
   {
      super(EmiFrameType.L_DATA_CON, telegram);
   }

   /**
    * Create an empty link data confirmation message.
    */
   public L_Data_con()
   {
      super(EmiFrameType.L_DATA_CON);
   }
}