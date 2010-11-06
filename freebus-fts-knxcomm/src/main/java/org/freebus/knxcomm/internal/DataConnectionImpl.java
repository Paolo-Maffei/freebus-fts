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
   private static final Logger LOGGER = Logger.getLogger(DataConnectionImpl.class);

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
   private final Semaphore recvSemaphore = new Semaphore(0);
   private int sequence = -1;
   private DeviceDescriptor0 deviceDescriptor0;
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
   @Override
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
      if (state == State.CLOSED)
         return;

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
            LOGGER.error(e);
         }
         finally
         {
            dispose();
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void dispose()
   {
      if (state != State.CLOSED)
      {
         state = State.CLOSED;
         busInterface.removeListener(this);
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
      //      logger.debug("::: receive: " + telegram);

      if (telegram != null)
         return telegram.getApplication();
      return null;
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
         LOGGER.error(e);
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
   @Override
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
    *
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
    *
    * @throws TimeoutException
    */
   @Override
   public Application query(Application application) throws IOException, TimeoutException
   {
      // To install the mapper here is only required if the log output below
      // is enabled. #send(Application) installs the mapper anyways before
      // sending.
      if (application instanceof Memory && memoryAddressMapper != null)
         ((Memory) application).setAddressMapper(memoryAddressMapper);
      LOGGER.debug("query - sending: " + application);

      //      final long start = System.currentTimeMillis();
      send(application);

      final int replyTimeout = 6000;
      final long end = System.currentTimeMillis() + replyTimeout;
      int remaining = replyTimeout;
      Application reply = null;

      while (reply == null && remaining > 0)
      {
         reply = receive(remaining);
         remaining = (int) (end - System.currentTimeMillis());
      }

      LOGGER.debug("query - reply: " + reply);
      return reply;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
      LOGGER.debug("++ Recv: " + telegram);

      if (!telegram.getFrom().equals(addr))
         return;

      final Application app = telegram.getApplication();
      if (app instanceof Memory && memoryAddressMapper != null)
      {
         ((Memory) app).setAddressMapper(memoryAddressMapper);
      }
      else if (deviceDescriptor0 == null && app instanceof DeviceDescriptorResponse)
      {
         final DeviceDescriptorResponse ddapp = (DeviceDescriptorResponse) app;
         if (ddapp.getDescriptorType() == 0)
            deviceDescriptor0 = (DeviceDescriptor0) ddapp.getDescriptor();
      }

      LOGGER.debug("++ Telegram received: " + telegram);

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
               LOGGER.debug("++ Send: " + ackTelegram.getTransport());
               busInterface.send(ackTelegram);
            }
            catch (IOException e)
            {
               LOGGER.warn("Failed to send acknowledge", e);
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
      LOGGER.debug("++ Sent: " + telegram);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void installMemoryAddressMapper()
   {
      if (memoryAddressMapper != null)
         return;

      if (deviceDescriptor0 == null)
         readDeviceDescriptor0();

      memoryAddressMapper = MemoryAddressMapperFactory.getMemoryAddressMapper(deviceDescriptor0.getMaskVersion());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DeviceDescriptor0 getDeviceDescriptor0()
   {
      if (deviceDescriptor0 == null)
         readDeviceDescriptor0();

      return deviceDescriptor0;
   }

   /**
    * Read the device descriptor #0 from the device and store the mask version in
    * {@link #deviceDescriptorMaskVersion}
    */
   private void readDeviceDescriptor0()
   {
      try
      {
         final DeviceDescriptorResponse reply = (DeviceDescriptorResponse) query(new DeviceDescriptorRead(0));
         if (reply == null)
            throw new RuntimeException("failed to read device descriptor from " + addr);

         deviceDescriptor0 = (DeviceDescriptor0) reply.getDescriptor();
      }
      catch (TimeoutException e)
      {
         throw new RuntimeException("failed to read device descriptor from " + addr, e);
      }
      catch (IOException e)
      {
         throw new RuntimeException("failed to read device descriptor from " + addr, e);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MemoryAddressMapper getMemoryAddressMapper()
   {
      if (memoryAddressMapper == null)
         installMemoryAddressMapper();

      return memoryAddressMapper;
   }
}
