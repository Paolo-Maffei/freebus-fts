package org.freebus.knxcomm.emi;

import org.freebus.knxcomm.emi.types.EmiFrameType;

/**
 * Link data request confirmation.
 */
public class L_Data_con extends EmiTelegramFrame
{
   /**
    * Create an empty link data confirmation message.
    */
   public L_Data_con()
   {
      super(EmiFrameType.L_DATA_CON);
   }
}