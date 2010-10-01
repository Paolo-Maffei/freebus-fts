package org.freebus.knxcomm.link;

import java.util.EventListener;

import org.freebus.knxcomm.event.CloseEvent;
import org.freebus.knxcomm.event.FrameEvent;

/**
 * Listener for getting events from a KNX {@link Link}.
 */
public interface LinkListener extends EventListener
{
   /**
    * A frame was received.
    * 
    * @param e - the corresponding event object.
    */
   void frameReceived(FrameEvent e);

   /**
    * Called when the link was closed.
    * 
    * @param e - the corresponding event object.
    */
   void linkClosed(CloseEvent e);
}
