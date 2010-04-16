package org.freebus.knxcomm;

import org.freebus.knxcomm.telegram.Telegram;

/**
 * Abstract implementation of a {@link TelegramListener telegram listener}.
 * The methods are empty.
 */
public abstract class TelegramAdapter implements TelegramListener
{
   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSendConfirmed(Telegram telegram)
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSent(Telegram telegram)
   {
   }
}
