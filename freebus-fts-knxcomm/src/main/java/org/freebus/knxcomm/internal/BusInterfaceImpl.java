package org.freebus.knxcomm.internal;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freebus.fts.common.HexString;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameFactory;
import org.freebus.knxcomm.emi.EmiTelegramFrame;
import org.freebus.knxcomm.emi.L_Data_req;
import org.freebus.knxcomm.event.CloseEvent;
import org.freebus.knxcomm.event.FrameEvent;
import org.freebus.knxcomm.link.Link;
import org.freebus.knxcomm.link.LinkListener;
import org.freebus.knxcomm.link.serial.Ft12SerialLink;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramListener;
import org.freebus.knxcomm.types.LinkMode;

/**
 * {@link BusInterface} implementation. Use {@link BusInterfaceFactory} to get a
 * bus interface object.
 */
public class BusInterfaceImpl implements BusInterface
{
   protected final CopyOnWriteArraySet<TelegramListener> listeners = new CopyOnWriteArraySet<TelegramListener>();
   protected final Map<PhysicalAddress, DataConnection> connections = new ConcurrentHashMap<PhysicalAddress, DataConnection>();
   private final Semaphore replySemaphore = new Semaphore(0);
   private final Logger logger = Logger.getLogger(getClass());
   private Telegram waitConTelegram;
   private final Link link;
   private Receiver receiver;

   /**
    * Create a bus-interface object that uses the given connection for the bus
    * communication.
    */
   public BusInterfaceImpl(Link con)
   {
      this.link = con;

      (new Thread(receiver)).start();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addListener(TelegramListener listener)
   {
      listeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void open(LinkMode mode) throws IOException
   {
      receiver = new Receiver();

      link.open(mode);

      link.addListener(receiver);
      receiver.start();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      close(true, "normal close");
   }

   /**
    * Close the connection.
    * 
    * @param normal
    * @param reason
    */
   private void close(boolean normal, String reason)
   {
      logger.info("closing bus interface - " + reason);

      link.removeListener(receiver);
      receiver.quit();
      link.close();
   }

   /**
    * {@inheritDoc}
    */
   public void setLinkMode(LinkMode mode) throws IOException
   {
      logger.debug("Switching to " + mode + " link mode");
      link.setLinkMode(mode);
   }

   /**
    * {@inheritDoc}
    */
   public LinkMode getLinkMode()
   {
      return link.getLinkMode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DataConnection connect(PhysicalAddress addr, Priority priority) throws IOException
   {
      if (link == null)
         throw new IOException("Not open");

      if (getLinkMode() == LinkMode.BusMonitor)
         throw new IllegalAccessError("bus monitor link mode is read only");

      final DataConnection dataCon = new DataConnectionImpl(addr, priority, this);
      dataCon.open();

      connections.put(addr, dataCon);
      return dataCon;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Link getConnection()
   {
      return link;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PhysicalAddress getPhysicalAddress()
   {
      return link.getPhysicalAddress();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isConnected()
   {
      return link != null && link.isConnected();
   }

   /**
    * Notify all listeners that the given telegram was received.
    */
   protected void notifyListenersReceived(final Telegram telegram)
   {
      for (TelegramListener listener : listeners)
         listener.telegramReceived(telegram);
   }

   /**
    * Notify all listeners that the given telegram was sent.
    */
   protected void notifyListenersSent(final Telegram telegram)
   {
      for (TelegramListener listener : listeners)
         listener.telegramSent(telegram);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeListener(TelegramListener listener)
   {
      listeners.remove(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void send(Telegram telegram) throws IOException
   {
      if (link == null)
         throw new IOException("Not open");

      if (getLinkMode() == LinkMode.BusMonitor)
         throw new IOException("bus monitor link mode is read only");

      final PhysicalAddress from = telegram.getFrom();
      if (from == null || PhysicalAddress.NULL.equals(from))
         telegram.setFrom(getPhysicalAddress());

      replySemaphore.drainPermits();
      waitConTelegram = telegram;

      final int waitTimeMS = 1500;
      try
      {
         link.send(new L_Data_req(telegram), true);

         if (replySemaphore.tryAcquire(waitTimeMS, TimeUnit.MILLISECONDS))
            return;
      }
      catch (InterruptedException e)
      {
         logger.warn("Interrupted while waiting for send confirmation", e);
      }
      finally
      {
         waitConTelegram = null;
      }

      logger.error("Sent telegram was not confirmed within " + waitTimeMS + "ms");
      throw new IOException("Sent telegram was not confirmed: " + telegram);
   }

   /**
    * FT1.2 receiver thread for the {@link Ft12SerialLink FT1.2 serial
    * communication link}.
    */
   private final class Receiver extends Thread implements LinkListener
   {
      private final Logger logger = Logger.getLogger(getClass());
      private final Queue<FrameEvent> receiveQueue = new ConcurrentLinkedQueue<FrameEvent>();
      private final Semaphore received = new Semaphore(0);
      private volatile boolean active;

      Receiver()
      {
         super("bus interface receiver");
         setDaemon(true);
      }

      /**
       * The receiver's main loop
       */
      @Override
      public void run()
      {
         active = true;

         try
         {
            while (active)
            {
               try
               {
                  received.acquire();
               }
               catch (InterruptedException e)
               {
                  if (active)
                     logger.warn("interrupted", e);
               }

               if (active)
                  processFrameEvent(receiveQueue.poll());
            }
         }
         finally
         {
            if (active)
               close(false, "receiver communication failure");
         }
      }

      /**
       * Process a frame event.
       * 
       * @param e - the frame event to process.
       * @return true if the frame was valid, false if not.
       */
      boolean processFrameEvent(FrameEvent e)
      {
         EmiFrame frame = e.getFrame();
         if (frame == null)
         {
            try
            {
               frame = EmiFrameFactory.createFrame(e.getData());
            }
            catch (IOException ex)
            {
               final byte[] data = e.getData();
               logger.warn("invalid EMI frame, discarding " + data.length + " bytes:" + HexString.toString(data));
               return false;
            }
         }

         if (frame instanceof EmiTelegramFrame)
         {
            final Telegram telegram = ((EmiTelegramFrame) frame).getTelegram();

            if (frame.getType().isConfirmation())
            {
               if (replySemaphore.hasQueuedThreads() && telegram.isSimilar(waitConTelegram))
                  replySemaphore.release();

               notifyListenersSent(telegram);
            }
            else
            {
               notifyListenersReceived(telegram);
            }
         }

         return true;
      }

      /**
       * Terminate the receiver thread.
       */
      void quit()
      {
         active = false;
         received.release(1);

         interrupt();

         if (currentThread() == this)
            return;

         try
         {
            join(100);
         }
         catch (final InterruptedException e)
         {
         }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void frameReceived(FrameEvent e)
      {
         receiveQueue.add(e);
         received.release();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void linkClosed(CloseEvent e)
      {
         quit();
      }
   };
}
