package org.freebus.knxcomm.netip.frames;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetAddress;

import org.freebus.knxcomm.netip.blocks.EndPoint;
import org.freebus.knxcomm.netip.types.ConnectionType;
import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.netip.types.TransportType;

/**
 * Connect request to the KNXnet/IP server.
 *
 * @see #getEndPoint to access the control endpoint,
 * @see #getDataEndPoint to access the data endpoint.
 */
public class ConnectRequest extends AbstractEndPointFrame
{
   private final EndPoint dataEndPoint;
   private ConnectionType type = ConnectionType.TUNNEL;
   private int layer = 2; // 2=link layer, see TunnelType for TUNNEL connections

   /**
    * Create a connect request object.
    *
    * @param ctrlType - the transport type of the client's control endpoint.
    * @param ctrlAddr - the address of the client's control endpoint.
    * @param ctrlPort - the port of the client's control endpoint.
    *
    * @param dataType - the transport type of the client's data endpoint.
    * @param dataAddr - the address of the client's data endpoint.
    * @param dataPort - the port of the client's data endpoint.
    */
   public ConnectRequest(TransportType ctrlType, InetAddress ctrlAddr, int ctrlPort,
         TransportType dataType, InetAddress dataAddr, int dataPort)
   {
      super(ctrlType, ctrlAddr, ctrlPort);
      dataEndPoint = new EndPoint(dataType, dataAddr, dataPort);
   }

   /**
    * Create a connect request object.
    */
   public ConnectRequest()
   {
      super();
      dataEndPoint = new EndPoint();
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
    * @return the connection type.
    */
   public ConnectionType getType()
   {
      return type;
   }

   /**
    * Set the connection type.
    *
    * @param type - the type to set
    */
   public void setType(ConnectionType type)
   {
      this.type = type;
   }

   /**
    * Returns the data endpoint information.
    *
    * @return the data endpoint
    */
   public EndPoint getDataEndPoint()
   {
      return dataEndPoint;
   }

   /**
    * @return the KNX layer
    */
   public int getLayer()
   {
      return layer;
   }

   /**
    * Set the KNX layer. E.g. 2 is the link layer.
    *
    * @param layer - the layer to set
    */
   public void setLayer(int layer)
   {
      this.layer = layer;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void readData(final DataInput in) throws IOException
   {
      super.readData(in);
      dataEndPoint.readData(in);

      in.skipBytes(1); // data length
      type = ConnectionType.valueOf(in.readUnsignedByte());
      layer = in.readUnsignedByte();
      in.skipBytes(1); // reserved
   }

   /**
    * Write the object to a {@link DataOutput data output stream}.
    *
    * @param out - the output stream to write to
    *
    * @throws IOException
    */
   @Override
   public void writeData(DataOutput out) throws IOException
   {
      super.writeData(out);
      dataEndPoint.writeData(out);

      out.write(4); // data length
      out.write(type.code);
      out.write(layer);
      out.write(0); // reserved
   }
}
