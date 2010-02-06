package org.freebus.knxcomm.emi;

/**
 * Link data indicator. This message contains a telegram that
 * was received from the EIB bus by the bus-interface.
 */
public class L_Data_ind extends EmiTelegramFrame
{
   /**
    * Create an empty link data confirmation message.
    */
   public L_Data_ind()
   {
      super(EmiFrameType.L_DATA_IND);
   }
}