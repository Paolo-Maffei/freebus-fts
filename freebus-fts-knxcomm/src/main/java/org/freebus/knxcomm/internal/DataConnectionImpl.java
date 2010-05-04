package org.freebus.knxcomm.internal;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freebus.fts.common.address.Address;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.DeviceDescriptorRead;
import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptorProperties;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptorPropertiesFactory;
import org.freebus.knxcomm.applicationData.ApplicationDataException;
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
   
   class RecvQueue extends LinkedList<Telegram>
   {

      /**
       * 
       */
      private static final long serialVersionUID = 8362220814452756284L;
     
      
      /**
       * search in the list the first telegram with the parameter
       * @param from - the from Address at  the telegram
       * @param dest - the destination address at the telegram
       * @param seq - the sequence number of the telegram
       * @param app - the Application of the telegram
       * @return The found telegram. If no Telegram is found null is returned.
       */
      public Telegram getItem(Address from, Address dest, int seq, ApplicationType appt)
      {
         for (Telegram t : this)
         {
            if (t.getFrom().equals(from) && t.getDest().equals(dest) && t.getSequence() == seq
                  && t.getApplicationType().equals(appt))
            {
               return t;
            }
         }
         return null;
      }
      
      
      /**
       * search in the list the first telegram with the parameter
       * @param from - the from Address at  the telegram
       * @param dest - the destination address at the telegram
       * @param seq - the sequence number of the telegram
       * @return The found telegram. If no Telegram is found null is returned.
       */
      public Telegram getConnectedAck(Address from, Address dest, int seq){
         for (Telegram t : this)
         {
            if (t.getFrom().equals(from) && t.getDest().equals(dest) && t.getSequence() == seq
                 && t.getTransport().equals(Transport.ConnectedAck))
            {
               return t;
            }
         }
         return null;
      }
   }

   private State state = State.CLOSED;
   private final PhysicalAddress addr;
   private final BusInterface busInterface;
   private final Telegram sendTelegram = new Telegram();
   private final RecvQueue recvQueue = new RecvQueue();
   private DeviceDescriptor deviceDescriptor = null;
   private Semaphore recvSemaphore = new Semaphore(0);
   private int sequence = -2;
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
    * @return the deviceDescriptor
    */
   public DeviceDescriptor getDeviceDescriptor()
   {
      return deviceDescriptor;
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
         sendTelegram.setPriority(Priority.SYSTEM);
         sendTelegram.setRepeated(true);
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
      sequence = -1;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Application receive(int timeout) throws IOException
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
         return recvQueue.poll().getApplication();
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
    * Send a telegram with the application to a device.
    * 
    * @param application - the application to send.
    * @return the responded application.
    * @throws ApplicationDataException 
    */
   public Application qeury(Application application) throws ApplicationDataException 
   {
      Telegram con,indack,indapp;
      if (deviceDescriptor != null){
         DeviceDescriptorPropertiesFactory ddpf = new DeviceDescriptorPropertiesFactory();
         DeviceDescriptorProperties deviceDescriptorProperties = ddpf.getDeviceDescriptor(deviceDescriptor);
      application.setDeviceDescriptorProperties(deviceDescriptorProperties);
      }
      sendTelegram.setApplication(application);
      try
      {
         send(sendTelegram);
         Thread.sleep(500);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      System.out.println("count resieved recoreds " +recvQueue.size());
      con = recvQueue.getItem(busInterface.getPhysicalAddress(), addr, sendTelegram.getSequence(), application.getType());
      indack = recvQueue.getConnectedAck(addr,busInterface.getPhysicalAddress(),  sendTelegram.getSequence());
      indapp = recvQueue.getItem(addr,busInterface.getPhysicalAddress(), sendTelegram.getSequence(), application.getApplicationResponses());
      if (con != null && indack != null && indapp != null){
         Application app = indapp.getApplication();
         if (app instanceof DeviceDescriptorResponse){
            DeviceDescriptorResponse appDevRes = (DeviceDescriptorResponse)app;
            this.deviceDescriptor = appDevRes.getDescriptor();
         }
         return indapp.getApplication();
      }
      
      return null;

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send(Application application) throws IOException
   {
      synchronized (sendTelegram)
      {
         if (application.isDeviceDescriptorRequired() && deviceDescriptor == null)
         {
            readDeviceDescriptor();

         }
         sendTelegram.setApplication(application);

         send(sendTelegram);
      }
   }

   /**
    * Read the Device Descriptor
    * 
    * @throws IOException
    */
   private void readDeviceDescriptor() throws IOException
   {
      synchronized (sendTelegram)
      {
         sendTelegram.setApplication(new DeviceDescriptorRead());
         send(sendTelegram);
         List<Application> apps = receiveMultiple(100);
         for (Application app : apps)
         {
            if (app instanceof DeviceDescriptorResponse)
            {
               deviceDescriptor = ((DeviceDescriptorResponse) app).getDescriptor();
            }
         }

      }
   }

   /**
    * {@inheritDoc}
    */
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
      System.out.println("*******Daten Empfangen********");



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
      Logger.getLogger(getClass()).debug("recv: " + telegram);
      processTelegram(telegram, false);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSendConfirmed(Telegram telegram)
   {
      Logger.getLogger(getClass()).debug("con: " + telegram);

      
         processTelegram(telegram, true);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSent(Telegram telegram)
   {
      Logger.getLogger(getClass()).debug("send: " + telegram);
   }
}
