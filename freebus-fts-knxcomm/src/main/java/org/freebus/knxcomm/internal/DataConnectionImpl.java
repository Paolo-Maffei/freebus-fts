package org.freebus.knxcomm.internal;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.application.ApplicationType;
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
       * The connection is being established, a connection request was sent,
       * an IACK is awaited.
       */
      CONNECTING;
   }

   private State state = State.CLOSED;
   private final PhysicalAddress addr;
   private final BusInterface busInterface;
   private final Telegram telegram;
   private final Semaphore waitDataSemaphore = new Semaphore(0);
   private int sendSequence, recvSequence;

   /**
    * Create a connection to the device with the given physical address.
    * Use {@link BusInterface#connect} to get a connection.
    *
    * @param addr - the physical address to which the connection will happen.
    * @param busInterface - the bus interface to use.
    */
   public DataConnectionImpl(PhysicalAddress addr, BusInterface busInterface)
   {
      this.addr = addr;
      this.busInterface = busInterface;

      telegram = new Telegram();
      telegram.setDest(addr);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void close()
   {
      busInterface.removeListener(this);

      incSequence();
      telegram.setTransport(Transport.Disconnect);

      state = State.CLOSED;
      try
      {
         busInterface.send(telegram);
      }
      catch (IOException e)
      {
         e.printStackTrace();
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
    * @return the internal state of the connection.
    */
   public State getState()
   {
      return state;
   }

   /**
    * Increase the sequence number of the internal telegram.
    */
   private void incSequence()
   {
      telegram.setSequence(telegram.getSequence() + 1);
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

      sendSequence = 0;
      recvSequence = 0;

      telegram.setApplication(ApplicationType.GroupValue_Read);
      telegram.setTransport(Transport.Connect);
      telegram.setSequence(++sendSequence);

      state = State.CONNECTING;

      try
      {
         busInterface.send(telegram);
      }
      catch (Exception e)
      {
         state = State.CLOSED;
         throw new IOException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Telegram receive() throws IOException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send(Telegram telegram) throws IOException
   {
      telegram.setDest(addr);
      telegram.setTransport(Transport.Connected);
      telegram.setSequence(++sendSequence);

      busInterface.send(telegram);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
      if (!telegram.getFrom().equals(addr))
         return;

      Logger.getLogger(getClass()).debug("Telegram received: " + telegram);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSent(Telegram telegram)
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSendConfirmed(Telegram telegram)
   {
   }
}
