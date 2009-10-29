package org.freebus.fts.comm;

import java.util.EventListener;

import org.freebus.fts.eib.Telegram;
import org.freebus.fts.emi.EmiMessage;

/**
 * Interface for classes that want to be notified when an EMI message is received.
 */
public interface BusListener extends EventListener
{
   /**
    * A message was received.
    * The called object must not change the message.
    */
   public void messageReceived(EmiMessage message);

   /**
    * A telegram was received.
    * The called object must not change the telegram.
    */
   public void telegramReceived(Telegram telegram);
}
