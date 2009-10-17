package org.freebus.fts.comm;

/**
 * Interface for classes that want to be notified when the bus receives
 * telegrams.
 */
public interface BusListener
{
   /**
    * A telegram was received.
    * The called object must not change the telegram.
    */
   public void telegramReceived(Telegram telegram);
}
