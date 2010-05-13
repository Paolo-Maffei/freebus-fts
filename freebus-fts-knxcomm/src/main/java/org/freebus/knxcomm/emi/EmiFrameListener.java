package org.freebus.knxcomm.emi;

import java.util.EventListener;


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
}
