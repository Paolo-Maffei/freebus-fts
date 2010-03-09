package org.freebus.knxcomm.netip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freebus.knxcomm.KNXConnection;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.internal.ListenableConnection;
import org.freebus.knxcomm.netip.frames.DescriptionRequest;
import org.freebus.knxcomm.netip.frames.Frame;
import org.freebus.knxcomm.netip.frames.FrameFactory;
import org.freebus.knxcomm.netip.frames.SearchRequest;
import org.freebus.knxcomm.netip.types.ProtocolType;
import org.freebus.knxcomm.netip.types.ServiceType;

/**
 * A KNXnet/IP connection.
 */
public final class KNXnetConnection extends ListenableConnection implements KNXConnection
{
   private final Logger logger = Logger.getLogger(getClass());
   private final InetSocketAddress addr;

   private final Semaphore receiveSemaphore = new Semaphore(0);
   private Frame receivedFrame;

   private final DatagramSocket socket;
   private final Thread listenerThread;

   private final int sendBufferSize = 8192;
   private final int recvBufferSize = 8192;
   private final byte[] sendBufferByte = new byte[sendBufferSize];
   private final int[] sendBuffer = new int[sendBufferSize];
   private final int[] recvBuffer = new int[recvBufferSize];

   /**
    * Create a new connection to a KNXnet/IP server listening on a custom port.
    * 
    * @param host - the name or IP address of the host that is running the
    *           KNXnet/IP server.
    * @param port - the UDP port of the KNXnet/IP server on the host. Usually
    *           3671.
    */
   public KNXnetConnection(String host, int port)
   {
      addr = new InetSocketAddress(host, port);

      try
      {
         socket = new DatagramSocket();
         socket.setSendBufferSize(sendBufferSize);
         socket.setReceiveBufferSize(recvBufferSize);
         logger.info("Opening UDP socket " + socket.getLocalAddress() + " port " + socket.getLocalPort());
      }
      catch (SocketException e)
      {
         throw new RuntimeException("Cannot create a datagram socket", e);
      }

      listenerThread = createListenerThread();
      listenerThread.start();
   }

   /**
    * Create a new connection to a KNXnet/IP server listening on the default
    * port (6720).
    * 
    * @param host - the name or IP address of the host that is running the
    *           KNXnet/IP server.
    */
   public KNXnetConnection(String host)
   {
      this(host, 6720);
   }

   /**
    * @return the host where the KNXnet/IP server is running.
    */
   public String getHost()
   {
      return addr.getHostName();
   }

   /**
    * @return the port on which the KNXnet/IP server is listening.
    */
   public int getPort()
   {
      return addr.getPort();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isConnected()
   {
      return socket != null && socket.isConnected();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void open() throws IOException
   {
      receiveSemaphore.drainPermits();

      send(new SearchRequest(ProtocolType.IPv4_UDP, socket.getLocalAddress(), socket.getLocalPort()));
      try
      {
         Thread.sleep(500);
      }
      catch (InterruptedException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      send(new DescriptionRequest(ProtocolType.IPv4_UDP, socket.getLocalAddress(), socket.getLocalPort()));
   }

   /**
    * Receive a frame. Up to <code>timeout</code> milliseconds is waited for
    * a frame to arrive.
    * 
    * @param timeout - wait up to timeout milliseconds, -1 waits infinitely.
    * @return the received KNXnet/IP frame, or null of no frame was received
    *         within the timeout.
    */
   public synchronized Frame receive(int timeout)
   {
      receiveSemaphore.drainPermits();
      receivedFrame = null;

      try
      {
         if (!receiveSemaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS))
            return null;
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
         return null;
      }

      // TODO
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void send(EmiFrame message) throws IOException
   {
   }

   /**
    * Send a KNXnet/IP frame.
    */
   public synchronized void send(Frame frame) throws IOException
   {
      final ServiceType serviceType = frame.getServiceType();
      logger.debug("Send: " + serviceType);

      final int frameSize = frame.toData(sendBuffer);
      for (int i = 0; i < frameSize; ++i)
         sendBufferByte[i] = (byte) (sendBuffer[i] & 0xff);

      final DatagramPacket dp = new DatagramPacket(sendBufferByte, frameSize);
      dp.setSocketAddress(addr);
      socket.send(dp);
   }

   /**
    * Process the received data
    * 
    * @throws IOException
    */
   public synchronized void processData(final byte[] data, int len) throws IOException
   {
      if (len < 6)
         return;

      for (int i = 0; i < len; ++i)
         recvBuffer[i] = ((int) data[i]) & 0xff;

      final Frame frame = FrameFactory.createFrame(recvBuffer);
      if (frame == null)
      {
         final ServiceType serviceType = ServiceType.valueOf((recvBuffer[2] << 8) | recvBuffer[3]);
         logger.debug("Recv: " + serviceType);
         return;
      }

      logger.debug("Recv: " + frame.getServiceType());

      if (receiveSemaphore.hasQueuedThreads())
      {
         receivedFrame = frame;
         receiveSemaphore.release();
      }

      // TODO process the frame
   }

   /**
    * Create the listener thread.
    */
   protected Thread createListenerThread()
   {
      return new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            logger.debug("starting listener thread");

            final byte[] data = new byte[recvBufferSize];
            final DatagramPacket p = new DatagramPacket(data, data.length);

            while (true)
            {
               try
               {
                  socket.receive(p);
                  processData(p.getData(), p.getLength());
               }
               catch (IOException e)
               {
                  e.printStackTrace();
               }
               catch (IllegalArgumentException e)
               {
                  e.printStackTrace();
               }
            }
         }
      });
   }
}
