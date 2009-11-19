package org.freebus.fts.comm;

import java.util.EventListener;

import org.freebus.fts.comm.emi.EmiFrame;

/**
 * Interface for classes that want to be notified when an EMI frame is received.
 */
public interface EmiFrameListener extends EventListener
{
   /**
    * An EMI frame was received.
    * The called object must not change the frame.
    */
   public void frameReceived(EmiFrame frame);

   /**
    * An EMI frame was sent.
    * The called object must not change the frame.
    */
   public void frameSent(EmiFrame frame);
}
