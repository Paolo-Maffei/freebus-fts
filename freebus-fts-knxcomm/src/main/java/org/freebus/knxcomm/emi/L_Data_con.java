package org.freebus.knxcomm.emi;

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