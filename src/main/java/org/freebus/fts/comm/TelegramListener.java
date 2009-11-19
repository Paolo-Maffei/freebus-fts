package org.freebus.fts.comm;

import java.util.EventListener;

import org.freebus.fts.eib.Telegram;

/**
 * Interface for classes that want to be notified when a telegram is sent or received.
 */
public interface TelegramListener extends EventListener
{
   /**
    * A telegram was received.
    * The called object must not change the frame.
    */
   public void telegramReceived(Telegram telegram);

   /**
    * A telegram was sent.
    * The called object must not change the frame.
    */
   public void telegramSent(Telegram telegram);
}
