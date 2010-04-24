package org.freebus.knxcomm.netip;

import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freebus.fts.common.HexString;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.KNXConnection;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.internal.ListenableConnection;
import org.freebus.knxcomm.netip.frames.ConnectRequest;
import org.freebus.knxcomm.netip.frames.ConnectResponse;
import org.freebus.knxcomm.netip.frames.DescriptionRequest;
import org.freebus.knxcomm.netip.frames.DescriptionResponse;
import org.freebus.knxcomm.netip.frames.DisconnectRequest;
import org.freebus.knxcomm.netip.frames.DisconnectResponse;
import org.freebus.knxcomm.netip.frames.Frame;
import org.freebus.knxcomm.netip.frames.FrameFactory;
import org.freebus.knxcomm.netip.frames.SearchRequest;
import org.freebus.knxcomm.netip.frames.SearchResponse;
import org.freebus.knxcomm.netip.frames.TunnelingAck;
import org.freebus.knxcomm.netip.frames.TunnelingRequest;
import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.netip.types.StatusCode;
import org.freebus.knxcomm.netip.types.TransportType;
import org.freebus.knxcomm.types.LinkMode;

/**
 * A KNXnet/IP connection.
 */
public final class KNXnetConnection extends ListenableConnection implements KNXConnection
{
   /**
    * The default KNXnet/IP UDP port
    */
   public static final int defaultPortUDP = 3671;

   /**
    * The default KNXnet/IP TCP port
    */
   public static final int defaultPortTCP = 6720;

   private final Logger logger = Logger.getLogger(getClass());
   private final InetSocketAddress addr;
   private InetSocketAddress dataAddr;
   private PhysicalAddress busAddr = PhysicalAddress.NULL;

   private final Semaphore receiveSemaphore = new Semaphore(0);
   private Frame receivedFrame;

   private int channelId = -1;
   private int sequence = -1;
   private boolean busMonitorModeSupported = true;
   private LinkMode mode;

   private final DatagramSocket socket;
   private final Thread listenerThread;

   private final int sendBufferSize = 8192;
   private final int recvBufferSize = 8192;

   private final byte[] recvBuffer = new byte[recvBufferSize];

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
    * Create a new connection to a KNXnet/IP server listening on the default UDP
    * port (3671).
    *
    * @param host - the name or IP address of the host that is running the
    *           KNXnet/IP server.
    */
   public KNXnetConnection(String host)
   {
      this(host, defaultPortUDP);
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
   public void open(LinkMode mode) throws IOException
   {
      receiveSemaphore.drainPermits();

      Frame frame;

      send(addr, new SearchRequest(TransportType.UDP, socket.getLocalAddress(), socket.getLocalPort()));
      frame = receive(1000);
      if (!(frame instanceof SearchResponse))
         throw new ConnectException("no response to KNXnet/IP search request");
      final SearchResponse searchResponse = (SearchResponse) frame;
      logger.info("Found KNXnet/IP server: " + searchResponse.getHardwareInfo().getName());

      StatusCode status = connectRequest(mode);
      if (status != StatusCode.OK)
      {
         if (status == StatusCode.E_CONNECTION_TYPE && mode == LinkMode.BusMonitor)
         {
            // Fall back to LinkLayer mode if BusMonitor mode is not supported
            mode = LinkMode.LinkLayer;
            status = connectRequest(mode);
            if (status == StatusCode.OK)
            {
               logger.warn("KNXnet/IP server does not support bus monitor mode, falling back to link layer mode");
               busMonitorModeSupported = false;
            }
         }

         if (status != StatusCode.OK)
            throw new ConnectException("KNXnet/IP connect to " + addr + " failed: " + status);
      }

      send(addr, new DescriptionRequest(TransportType.UDP, socket.getLocalAddress(), socket.getLocalPort()));
      frame = receive(3500);

      if (!(frame instanceof DescriptionResponse))
         throw new ConnectException("no response to KNXnet/IP describe request");

      final DescriptionResponse descResp = (DescriptionResponse) frame;

      this.mode = mode;
      busAddr = descResp.getHardwareInfo().getBusAddress();
      logger.info("Connection to KNXnet/IP server " + busAddr + " established");

      if (PhysicalAddress.NULL.equals(busAddr))
         busAddr = new PhysicalAddress(0, 0, 254);
   }

   /**
    * Send a connection request and wait for the answer. On success, the channel
    * ID and the data endpoint information is stored and {@link StatusCode#OK}
    * is returned. On failure, the {@link StatusCode status code} of the
    * response is returned.
    *
    * @param mode - the link mode to request
    *
    * @return the {@link StatusCode status} of the the connection response.
    *
    * @throws IOException
    */
   private StatusCode connectRequest(LinkMode mode) throws IOException
   {

      final ConnectRequest conReq = new ConnectRequest(TransportType.UDP, socket.getLocalAddress(), socket
            .getLocalPort(), TransportType.UDP, socket.getLocalAddress(), socket.getLocalPort());
      conReq.setLayer(mode);

      Frame frame = null;
      for (int tries = 3; tries > 0 && !(frame instanceof ConnectResponse); --tries)
      {
         send(addr, conReq);
         frame = receive(3500);
      }

      if (!(frame instanceof ConnectResponse))
         throw new ConnectException("no response to KNXnet/IP connect request");

      final ConnectResponse resp = (ConnectResponse) frame;
      final StatusCode status = resp.getStatus();

      if (status == StatusCode.OK)
      {
         channelId = resp.getChannelId();
         dataAddr = new InetSocketAddress(resp.getDataEndPoint().getAddress(), resp.getDataEndPoint().getPort());
      }

      return status;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      try
      {
         send(addr,
               new DisconnectRequest(TransportType.UDP, socket.getLocalAddress(), socket.getLocalPort(), channelId));

         final Frame frame = receive(500);
         if (!(frame instanceof DisconnectResponse))
            throw new ConnectException("no response to KNXnet/IP disconnect request");

         final DisconnectResponse resp = (DisconnectResponse) frame;
         if (resp.getStatus() != StatusCode.OK)
            throw new ConnectException("Cannot disconnect: " + resp.getStatus());
      }
      catch (IOException e)
      {
         logger.error("Failed to disconnect from the KNXnet/IP server", e);
      }
      finally
      {
         channelId = -1;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isConnected()
   {
      return socket != null && channelId >= 0;
   }

   /**
    * {@inheritDoc}
    */
   public void setLinkMode(LinkMode mode) throws IOException
   {
      if (!busMonitorModeSupported)
         mode = LinkMode.LinkLayer;

      if (this.mode == mode)
         return;

      close();
      open(mode);
   }

   /**
    * {@inheritDoc}
    */
   public LinkMode getLinkMode()
   {
      return mode;
   }

   /**
    * Receive a frame. Up to <code>timeout</code> milliseconds is waited for a
    * frame to arrive.
    *
    * @param timeout - wait up to timeout milliseconds, -1 waits infinitely.
    *
    * @return the received KNXnet/IP frame, or null of no frame was received
    *         within the timeout.
    */
   public Frame receive(int timeout)
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

      return receivedFrame;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send(EmiFrame emiFrame) throws IOException
   {
      final TunnelingRequest frame = new TunnelingRequest(channelId, ++sequence);
      frame.setFrame(emiFrame);

      send(dataAddr, frame);
      notifyListenersSent(emiFrame);
   }

   /**
    * Send a KNXnet/IP frame to an address.
    *
    * @param address - the address (IP number + port) to send to
    * @param frame - the frame to send
    */
   public void send(InetSocketAddress address, Frame frame) throws IOException
   {
      final ServiceType serviceType = frame.getServiceType();
      logger.debug("Send: " + serviceType + " (seq " + sequence + ")");

      final byte[] data = frame.toByteArray();
      if (logger.isDebugEnabled())
         logger.debug("SEND: " + HexString.toString(data));

      final DatagramPacket dp = new DatagramPacket(data, data.length);
      dp.setSocketAddress(address);
      socket.send(dp);
   }

   /**
    * Process the received data
    *
    * @param data - the received data.
    * @param len - number of bytes in data that are valid.
    *
    * @throws IOException
    */
   public void processData(final byte[] data, int len) throws IOException
   {
      if (len < 6)
         return;

      if (logger.isDebugEnabled())
         logger.debug("RECV: " + HexString.toString(data, 0, len));

      final Frame frame = FrameFactory.createFrame(data);
      if (frame == null)
      {
         int serviceTypeCode = ((data[2] << 8) | data[3]) & 0xffff;
         final ServiceType serviceType = ServiceType.valueOf(serviceTypeCode);
         logger.debug("Recv: " + serviceType + " (ignored)");
         return;
      }

      logger.debug("Recv: " + frame.getServiceType());

      if (receiveSemaphore.hasQueuedThreads())
      {
         receivedFrame = frame;
         receiveSemaphore.release();
      }
      else if (frame.getServiceType() == ServiceType.TUNNELING_REQUEST)
      {
         final TunnelingRequest tunnelFrame = (TunnelingRequest) frame;
         final int recvSequence = tunnelFrame.getSequence();

         logger.debug("Recv: " + tunnelFrame.getFrame() + " (seq " + recvSequence + ")");

         send(dataAddr, new TunnelingAck(channelId, recvSequence, StatusCode.OK));

         notifyListenersReceived(tunnelFrame.getFrame());
      }
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
            logger.debug("Starting listener thread");
            final DatagramPacket p = new DatagramPacket(recvBuffer, recvBuffer.length);

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

   /**
    * {@inheritDoc}
    */
   @Override
   public PhysicalAddress getPhysicalAddress()
   {
      return busAddr;
   }
}
