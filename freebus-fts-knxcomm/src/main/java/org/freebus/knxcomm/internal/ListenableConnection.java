package org.freebus.knxcomm.internal;

import java.util.concurrent.CopyOnWriteArrayList;

import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameListener;
import org.freebus.knxcomm.link.Link;

/**
 * An abstract KNX connection class that has methods for handling listeners.
 */
public abstract class ListenableConnection implements Link
{
   private final CopyOnWriteArrayList<EmiFrameListener> listeners = new CopyOnWriteArrayList<EmiFrameListener>();

   /**
    * {@inheritDoc}
    */
   @Override
   public void addListener(EmiFrameListener listener)
   {
      listeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeListener(EmiFrameListener listener)
   {
      listeners.remove(listener);
   }

   /**
    * Notify all listeners that a frame was received.
    *
    * @param frame - the frame that was received.
    */
   public void notifyListenersReceived(final EmiFrame frame)
   {
      for (EmiFrameListener listener : listeners)
         listener.frameReceived(frame);
   }
}
