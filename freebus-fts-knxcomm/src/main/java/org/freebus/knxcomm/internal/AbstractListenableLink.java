package org.freebus.knxcomm.internal;

import java.util.concurrent.CopyOnWriteArrayList;

import org.freebus.knxcomm.event.CloseEvent;
import org.freebus.knxcomm.event.FrameEvent;
import org.freebus.knxcomm.link.Link;
import org.freebus.knxcomm.link.LinkListener;
import org.slf4j.LoggerFactory;

/**
 * An abstract {@link Link} class that has methods for handling listeners.
 */
public abstract class AbstractListenableLink implements Link
{
   private final CopyOnWriteArrayList<LinkListener> listeners = new CopyOnWriteArrayList<LinkListener>();

   /**
    * {@inheritDoc}
    */
   @Override
   public final void addListener(LinkListener listener)
   {
      listeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final void removeListener(LinkListener listener)
   {
      listeners.remove(listener);
   }

   /**
    * Notify all listeners that a frame was received.
    *
    * @param e - the corresponding frame event.
    */
   public final void fireFrameReceived(FrameEvent e)
   {
      for (LinkListener listener : listeners)
      {
         try
         {
            listener.frameReceived(e);
         }
         catch (final RuntimeException ex)
         {
            removeListener(listener);
            LoggerFactory.getLogger(AbstractListenableLink.class).error("removed event listener", ex);
         }
      }
   }

   /**
    * Notify all listeners that the link was closed.
    *
    * @param e - the corresponding frame event.
    */
   public final void fireLinkClosed(CloseEvent e)
   {
      for (LinkListener listener : listeners)
      {
         try
         {
            listener.linkClosed(e);
         }
         catch (final RuntimeException ex)
         {
            removeListener(listener);
            LoggerFactory.getLogger(AbstractListenableLink.class).error("removed event listener", ex);
         }
      }
   }
}
