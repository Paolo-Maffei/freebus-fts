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
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.KNXConnection;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameListener;
import org.freebus.knxcomm.emi.EmiTelegramFrame;
import org.freebus.knxcomm.emi.L_Data_req;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramListener;
import org.freebus.knxcomm.types.LinkMode;

/**
 * {@link BusInterface} implementation. Use {@link BusInterfaceFactory} to get a
 * bus interface object.
 */
public class BusInterfaceImpl implements BusInterface, EmiFrameListener
{
   protected final CopyOnWriteArraySet<TelegramListener> listeners = new CopyOnWriteArraySet<TelegramListener>();
   protected final Map<PhysicalAddress, DataConnection> connections = new ConcurrentHashMap<PhysicalAddress, DataConnection>();
   private final KNXConnection con;
   private final Semaphore replySemaphore = new Semaphore(0);
   private Telegram waitConTelegram;

   private final Queue<EmiFrame> receiveQueue = new ConcurrentLinkedQueue<EmiFrame>();
   private final Semaphore received = new Semaphore(0);
   private boolean active = true;

   /**
    * Create a bus-interface object that uses the given connection for the bus
    * communication.
    */
   public BusInterfaceImpl(KNXConnection con)
   {
      this.con = con;

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
      con.open(mode);
      con.addListener(this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      active = false;
      received.release(1);

      con.removeListener(this);
      con.close();
   }

   /**
    * {@inheritDoc}
    */
   public void setLinkMode(LinkMode mode) throws IOException
   {
      Logger.getLogger(getClass()).debug("Switching to " + mode + " link mode");
      con.setLinkMode(mode);
   }

   /**
    * {@inheritDoc}
    */
   public LinkMode getLinkMode()
   {
      return con.getLinkMode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DataConnection connect(PhysicalAddress addr) throws IOException
   {
      if (con == null)
         throw new IOException("Not open");

      if (getLinkMode() == LinkMode.BusMonitor)
         throw new IllegalAccessError("bus monitor link mode is read only");

      final DataConnection dataCon = new DataConnectionImpl(addr, this);
      dataCon.open();

      connections.put(addr, dataCon);
      return dataCon;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public KNXConnection getConnection()
   {
      return con;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PhysicalAddress getPhysicalAddress()
   {
      return con.getPhysicalAddress();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isConnected()
   {
      return con != null && con.isConnected();
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
      if (con == null)
         throw new IOException("Not open");

      if (getLinkMode() == LinkMode.BusMonitor)
         throw new IOException("bus monitor link mode is read only");

      final PhysicalAddress from = telegram.getFrom();
      if (from == null || PhysicalAddress.NULL.equals(from))
         telegram.setFrom(getPhysicalAddress());

      replySemaphore.drainPermits();
      waitConTelegram = telegram;

      try
      {
         con.send(new L_Data_req(telegram));

         if (replySemaphore.tryAcquire(1000, TimeUnit.MILLISECONDS))
            return;
      }
      catch (InterruptedException e)
      {
         Logger.getLogger(getClass()).warn("Interrupted while waiting for send confirmation", e);
      }
      finally
      {
         waitConTelegram = null;
      }

      throw new IOException("Sent telegram was not confirmed: " + telegram);
   }

   /**
    * An {@link EmiFrame EMI frame} was received. This method is called from the
    * {@link KNXConnection connection's} receiver thread.
    */
   @Override
   public void frameReceived(EmiFrame frame)
   {
      if (replySemaphore.hasQueuedThreads() && frame.getType().isConfirmation() && frame instanceof EmiTelegramFrame)
      {
         final Telegram telegram = ((EmiTelegramFrame) frame).getTelegram();
//         if (telegram.getTransport() == Transport.Disconnect)
//         {
//            // Debug catchpoint
//            Logger.getLogger(getClass()).debug("close confirmed");
//         }
//         Logger.getLogger(getClass()).debug("possible confirmation, isSimilar=" + telegram.isSimilar(waitConTelegram) + " telegram: " + telegram + " waitConTelegram: " + waitConTelegram);
         if (telegram.isSimilar(waitConTelegram))
            replySemaphore.release();
      }

      receiveQueue.add(frame);
      received.release();
   }

   /**
    * A runnable that handles received frames.
    */
   private final Runnable receiver = new Runnable()
   {
      private final Logger logger = Logger.getLogger(getClass());

      @Override
      public void run()
      {
         while (active)
         {
            try
            {
               received.acquire();

               if (!active)
                  break;

               final EmiFrame frame = receiveQueue.poll();
               if (frame instanceof EmiTelegramFrame)
               {
                  final Telegram telegram = ((EmiTelegramFrame) frame).getTelegram();

                  if (frame.getType().isConfirmation())
                     notifyListenersSent(telegram);
                  else notifyListenersReceived(telegram);
               }
            }
            catch (InterruptedException e)
            {
               logger.error("EMI frame receiver thread was interrupted", e);
            }
         }
      }
   };
}
