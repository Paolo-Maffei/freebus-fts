package org.freebus.fts.comm;

import java.util.EventListener;

import org.freebus.fts.eib.Telegram;

/**
 * Interface for classes that want to be notified when the bus receives
 * telegrams.
 */
public interface BusListener extends EventListener
{
   /**
    * A telegram was received.
    * The called object must not change the telegram.
    */
   public void telegramReceived(Telegram telegram);
}
