package org.freebus.knxcomm.telegram;

import java.util.EventListener;

import org.freebus.knxcomm.BusInterface;

/**
 * Interface for classes that want to be notified when a telegram is sent or received.
 *
 * @see BusInterface - this is where telegram listeners can be registered.
 */
public interface TelegramListener extends EventListener
{
   /**
    * A telegram was received.
    * The called object must not change the frame.
    *
    * @param telegram - the received telegram
    */
   public void telegramReceived(Telegram telegram);

   /**
    * A telegram was sent.
    * The called object must not change the frame.
    *
    * @param telegram - the sent telegram
    */
   public void telegramSent(Telegram telegram);

   /**
    * The sending of a telegram was confirmed by the bus coupling unit.
    *
    * @param telegram - the confirmation telegram
    */
   public void telegramSendConfirmed(Telegram telegram);
}
