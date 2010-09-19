package org.freebus.knxcomm.internal;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.application.DeviceDescriptorRead;
import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.application.Memory;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor0;
import org.freebus.knxcomm.application.memory.MemoryAddressMapper;
import org.freebus.knxcomm.application.memory.MemoryAddressMapperFactory;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramListener;
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
   private final Priority priority;
   private final BusInterface busInterface;
   private final Telegram sendTelegram = new Telegram();
   private final Telegram ackTelegram = new Telegram();
   private final LinkedList<Telegram> recvQueue = new LinkedList<Telegram>();
   private Semaphore recvSemaphore = new Semaphore(0);
   private int sequence = -1;
   private int deviceDescriptorMaskVersion;
   private MemoryAddressMapper memoryAddressMapper;

   /**
    * Create a connection to the device with the given physical address. Use
    * {@link BusInterface#connect} to get a connection.
    *
    * @param addr - the physical address to which the connection will happen.
    * @param priority - the priority of the telegrams.
    * @param busInterface - the bus interface to use.
    */
   public DataConnectionImpl(PhysicalAddress addr, Priority priority, BusInterface busInterface)
   {
      this.addr = addr;
      this.priority = priority;
      this.busInterface = busInterface;

      sendTelegram.setDest(addr);
      sendTelegram.setPriority(priority);

      ackTelegram.setDest(addr);
      ackTelegram.setPriority(priority);
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
   public synchronized void close()
   {
      synchronized (sendTelegram)
      {
         sendTelegram.setTransport(Transport.Disconnect);
         sendTelegram.setApplicationType(null);
         sendTelegram.setSequence(0);

         try
         {
            busInterface.send(sendTelegram);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         finally
         {
            state = State.CLOSED;
            busInterface.removeListener(this);
         }
      }
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
    * @return the priority that was set for telegrams that are sent.
    */
   public Priority getPriority()
   {
      return priority;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Application receive(int timeout) throws IOException
   {
      final Telegram telegram = receiveTelegram(timeout);
      return telegram == null ? null : telegram.getApplication();
   }

   /**
    * Receive an {@link Transport#ConnectedAck acknowledge} from the remote
    * device. Waits 3 seconds for the acknowledge to be received.
    *
    * @throws IOException if a NACK (not-acknowledged) was received.
    * @throws TimeoutException if no acknowledge was received within the timeout
    */
   private void receiveAcknowledge() throws IOException, TimeoutException
   {
      final Telegram telegram = receiveTelegram(3000);
      if (telegram != null)
      {
         final Transport transport = telegram.getTransport();
         if (transport == Transport.ConnectedAck)
            return;
         if (transport == Transport.ConnectedNak)
            throw new IOException("NACK received");
      }

      throw new TimeoutException("timeout while waiting for an acknowledge from the remote device");
   }

   /**
    * Receive a telegram from the remote device.
    *
    * @param timeout - how long to wait, in milliseconds, -1 waits infinitely.
    *
    * @return the received telegram
    *
    * @throws IOException
    */
   private Telegram receiveTelegram(int timeout) throws IOException
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
   public List<Application> receiveMultiple(int timeout) throws IOException
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

      final List<Application> result = new LinkedList<Application>();

      synchronized (recvQueue)
      {
         if (!recvSemaphore.tryAcquire(recvQueue.size()))
            throw new RuntimeException("internal error");

         while (!recvQueue.isEmpty())
            result.add(recvQueue.poll().getApplication());

         recvQueue.clear();
      }

      return result;
   }

   /**
    * {@inheritDoc}
    * @throws TimeoutException
    */
   @Override
   public void send(Application application) throws IOException, TimeoutException
   {
      if (application instanceof Memory && memoryAddressMapper != null)
         ((Memory) application).setAddressMapper(memoryAddressMapper);

      synchronized (sendTelegram)
      {
         sendTelegram.setApplication(application);
         sendTelegram.setDest(addr);
         sendTelegram.setTransport(Transport.Connected);
         sendTelegram.setSequence(++sequence);

         busInterface.send(sendTelegram);

         receiveAcknowledge();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void sendUnconfirmed(Application application) throws IOException
   {
      if (application instanceof Memory && memoryAddressMapper != null)
         ((Memory) application).setAddressMapper(memoryAddressMapper);

      synchronized (sendTelegram)
      {
         sendTelegram.setApplication(application);
         sendTelegram.setDest(addr);
         sendTelegram.setTransport(Transport.Connected);
         sendTelegram.setSequence(++sequence);

         busInterface.send(sendTelegram);
      }
   }

   /**
    * {@inheritDoc}
    * @throws TimeoutException
    */
   @Override
   public Application query(Application application) throws IOException, TimeoutException
   {
//      Logger.getLogger(getClass()).debug("query - sending: " + application);

      final long start = System.currentTimeMillis();
      send(application);

      final int timeout = (int) (System.currentTimeMillis() - start + 6000);
      final Application reply = receive(timeout);

//      Logger.getLogger(getClass()).debug("query - reply: " + reply);
      return reply;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
      Logger.getLogger(getClass()).debug("++ Recv: " + telegram);

      if (!telegram.getFrom().equals(addr))
         return;

      final Application app = telegram.getApplication();
      if (app instanceof Memory && memoryAddressMapper != null)
      {
         ((Memory) app).setAddressMapper(memoryAddressMapper);
      }
      else if (deviceDescriptorMaskVersion == 0 && app instanceof DeviceDescriptorResponse)
      {
         final DeviceDescriptorResponse ddapp = (DeviceDescriptorResponse) app;
         if (ddapp.getDescriptorType() == 0)
            deviceDescriptorMaskVersion = ((DeviceDescriptor0) ddapp.getDescriptor()).getMaskVersion();
      }

      Logger.getLogger(getClass()).debug("++ Telegram received: " + telegram);

      synchronized (recvQueue)
      {
         recvQueue.push(telegram);
      }

      if (telegram.getTransport() == Transport.Connected)
      {
         synchronized (ackTelegram)
         {
            ackTelegram.setTransport(Transport.ConnectedAck);
            ackTelegram.setSequence(telegram.getSequence());

            try
            {
               Logger.getLogger(getClass()).debug("++ Send: " + ackTelegram.getTransport());
               busInterface.send(ackTelegram);
            }
            catch (IOException e)
            {
               Logger.getLogger(getClass()).warn("Failed to send acknowledge", e);
            }
         }
      }

      recvSemaphore.release();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSent(Telegram telegram)
   {
      Logger.getLogger(getClass()).debug("++ Sent: " + telegram);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void installMemoryAddressMapper()
   {
      if (memoryAddressMapper != null)
         return;

      if (deviceDescriptorMaskVersion == 0)
      {
         try
         {
            final DeviceDescriptorResponse reply = (DeviceDescriptorResponse) query(new DeviceDescriptorRead(0));
            deviceDescriptorMaskVersion = ((DeviceDescriptor0) reply.getDescriptor()).getMaskVersion();
         }
         catch (TimeoutException e)
         {
            throw new RuntimeException("failed to get device descriptor from " + addr, e);
         }
         catch (IOException e)
         {
            throw new RuntimeException("failed to get device descriptor from " + addr, e);
         }
      }

      memoryAddressMapper = MemoryAddressMapperFactory.getMemoryAddressMapper(deviceDescriptorMaskVersion);
   }
}
