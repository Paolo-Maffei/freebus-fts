package org.freebus.fts.comm;

import org.freebus.fts.utils.I18n;

/**
 * The types of EIB bus telegrams.
 */
public enum TelegramType
{
   /**
    * A telegram of unknown type.
    */
   UNKNOWN(-1);

   /**
    * The id of the telegram.
    */
   public final int id;

   /**
    * @return the translated name of the telegram.
    */
   public String getLocalizedName()
   {
      return I18n.getMessage("Telegram_" + this.toString());
   }

   //
   // Internal constructor
   //
   private TelegramType(int id)
   {
      this.id = id;
   }
}
