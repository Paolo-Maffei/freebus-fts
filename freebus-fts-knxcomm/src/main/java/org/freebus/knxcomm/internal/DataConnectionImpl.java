package org.freebus.knxcomm.internal;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

/**
 * A direct connection to a device on the KNX/EIB bus.
 */
public class DataConnectionImpl implements DataConnection, TelegramListener
{
   /**
    * The state of the connection.
    */
   enum State
   {
      /**
       * The connection is closed.
       */
      CLOSED,

      /**
       * The connection is open.
       */
      OPEN_IDLE,

      /**
       * The connection is open, a T_ACK from the remote device is awaited.
       */
      OPEN_WAIT,

      /**
       * The connection is being established, a connection request was sent, an
       * IACK is awaited.
       */
      CONNECTING;
   }

   private State state = State.CLOSED;
   private final PhysicalAddress addr;
   private final BusInterface busInterface;
   private final Telegram sendTelegram = new Telegram();
   private final LinkedList<Telegram> recvQueue = new LinkedList<Telegram>();
   private Semaphore recvSemaphore = new Semaphore(0);
   private int sequence = -1;
   private boolean recvConfirmations = false;

   /**
    * Create a connection to the device with the given physical address. Use
    * {@link BusInterface#connect} to get a connection.
    *
    * @param addr - the physical address to which the connection will happen.
    * @param busInterface - the bus interface to use.
    */
   public DataConnectionImpl(PhysicalAddress addr, BusInterface busInterface)
   {
      this.addr = addr;
      this.busInterface = busInterface;

      sendTelegram.setDest(addr);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void close()
   {
      busInterface.removeListener(this);

      synchronized (sendTelegram)
      {
         sendTelegram.setTransport(Transport.Disconnect);
         sendTelegram.setSequence(++sequence);

         state = State.CLOSED;
         try
         {
            busInterface.send(sendTelegram);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * @return the physical address of the target device.
    */
   public PhysicalAddress getAddress()
   {
      return addr;
   }

   /**
    * @return the bus-interface.
    */
   public BusInterface getBusInterface()
   {
      return busInterface;
   }

   /**
    * {@inheritDoc}
    */
   public void setReceiveConfirmations(boolean enable)
   {
      recvConfirmations = enable;
   }

   /**
    * @return the internal state of the connection.
    */
   public State getState()
   {
      return state;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isOpened()
   {
      return state != State.CLOSED;
   }

   /**
    * {@inheritDoc}
    */
   public synchronized void open() throws IOException
   {
      if (isOpened())
         throw new IOException("Connection is open");

      busInterface.addListener(this);

      sequence = -1;
      recvSemaphore.drainPermits();

      synchronized (sendTelegram)
      {
         sendTelegram.setTransport(Transport.Connect);
         sendTelegram.setSequence(++sequence);

         state = State.CONNECTING;

         try
         {
            busInterface.send(sendTelegram);
         }
         catch (Exception e)
         {
            state = State.CLOSED;
            throw new IOException(e);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Telegram receive(int timeout) throws IOException
   {
      try
      {
         if (timeout < 0)
            recvSemaphore.acquire();
         else if (!recvSemaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS))
            return null;
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
         return null;
      }

      synchronized (recvQueue)
      {
         return recvQueue.poll();
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<Telegram> receiveMultiple(int timeout) throws IOException
   {
      if (timeout > 0)
      {
         try
         {
            Thread.sleep(timeout);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
      }

      final List<Telegram> result = new LinkedList<Telegram>();

      synchronized (recvQueue)
      {
         if (!recvSemaphore.tryAcquire(recvQueue.size()))
            throw new RuntimeException("internal error");

         result.addAll(recvQueue);
         recvQueue.clear();
      }

      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send(Application application) throws IOException
   {
      synchronized (sendTelegram)
      {
         sendTelegram.setApplication(application);
         send(sendTelegram);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send(Telegram telegram) throws IOException
   {
      telegram.setDest(addr);
      telegram.setTransport(Transport.Connected);
      telegram.setSequence(++sequence);

      busInterface.send(telegram);
   }

   /**
    * Process a received telegram
    */
   public void processTelegram(Telegram telegram, boolean isConfirmation)
   {
      if (!telegram.getFrom().equals(addr))
         return;

      if (isConfirmation)
         Logger.getLogger(getClass()).debug("Telegram confirmed: " + telegram);
      else Logger.getLogger(getClass()).debug("Telegram received: " + telegram);

      synchronized (recvQueue)
      {
         recvQueue.push(telegram);
      }
      recvSemaphore.release();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
      processTelegram(telegram, false);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSendConfirmed(Telegram telegram)
   {
      if (recvConfirmations)
         processTelegram(telegram, true);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSent(Telegram telegram)
   {
   }
}
