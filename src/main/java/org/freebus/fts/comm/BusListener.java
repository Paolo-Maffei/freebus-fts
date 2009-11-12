package org.freebus.fts.comm;

import java.util.EventListener;

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
    * A message was sent.
    * The called object must not change the message.
    */
   public void messageSent(EmiMessage message);
}
