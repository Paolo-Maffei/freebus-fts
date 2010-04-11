package org.freebus.knxcomm.netip.frames;

import java.net.InetAddress;

import org.freebus.knxcomm.netip.blocks.ConnReqInfo;
import org.freebus.knxcomm.netip.blocks.HostProtAddrInfo;
import org.freebus.knxcomm.netip.types.ConnectionType;
import org.freebus.knxcomm.netip.types.ProtocolType;
import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Connect request to the KNXnet/IP server.
 *
 * The {@link #getHostProtAddrInfo()} describes the control endpoint,
 * ths {@link #getDataHostProtAddrInfo()} describes the data endpoint.
 */
public class ConnectRequest extends AbstractRequest
{
   private final HostProtAddrInfo dataHostProtAddrInfo;
   private final ConnReqInfo connReqInfo;

   /**
    * Create a connect request object.
    *
    * @param controlProtocol - the control protocol-type.
    * @param controlAddr - the address of the sender's control endpoint.
    * @param controlPort - the port of the sender's control endpoint.
    *
    * @param dataProtocol - the data protocol-type.
    * @param dataAddr - the address of the sender's data endpoint.
    * @param dataPort - the port of the sender's data endpoint.
    */
   public ConnectRequest(ProtocolType controlProtocol, InetAddress controlAddr, int controlPort,
         ProtocolType dataProtocol, InetAddress dataAddr, int dataPort)
   {
      super(controlProtocol, controlAddr, controlPort);
      dataHostProtAddrInfo = new HostProtAddrInfo(dataProtocol, dataAddr, dataPort);

      connReqInfo = new ConnReqInfo(ConnectionType.TUNNEL, 2);
   }

   /**
    * Create a description request object.
    */
   public ConnectRequest()
   {
      super();
      dataHostProtAddrInfo = new HostProtAddrInfo();
      connReqInfo = new ConnReqInfo(ConnectionType.TUNNEL, 2);
   }

   /**
    * @return {@link ServiceType#CONNECT_REQUEST}.
    */
   @Override
   public ServiceType getServiceType()
   {
      return ServiceType.CONNECT_REQUEST;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int bodyFromData(int[] data, int start) throws InvalidDataException
   {
      int pos = start;
      pos += super.bodyFromData(data, pos);
      pos += dataHostProtAddrInfo.fromData(data, pos);
      pos += connReqInfo.fromData(data, pos);
      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int bodyToData(int[] data, int start)
   {
      int pos = start;
      pos += super.bodyToData(data, pos);
      pos += dataHostProtAddrInfo.toData(data, pos);
      pos += connReqInfo.toData(data, pos);
      return pos - start;
   }
}
