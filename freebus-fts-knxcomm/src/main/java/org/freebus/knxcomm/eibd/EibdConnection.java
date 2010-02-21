package org.freebus.knxcomm.eibd;

import java.io.IOException;

import org.freebus.knxcomm.KNXConnection;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameListener;

/**
 * A connection to an eibd.
 */
public final class EibdConnection implements KNXConnection
{
   private final String host;
   private final int port;

   /**
    * Create a new connection to an eibd listening on a custom port.
    * 
    * @param host - the name or IP address of the host that is running eibd.
    * @param port - the TCP port of the eibd on the host. Default: 6720.
    */
   public EibdConnection(String host, int port)
   {
      this.host = host;
      this.port = port;
   }

   /**
    * Create a new connection to an eibd listening on the default port (6720).
    * 
    * @param host - the name or IP address of the host that is running eibd.
    */
   public EibdConnection(String host)
   {
      this.host = host;
      this.port = 6720;
   }

   /**
    * @return the host where the eibd is running.
    */
   public String getHost()
   {
      return host;
   }

   /**
    * @return the port on which the eibd is listening.
    */
   public int getPort()
   {
      return port;
   }

   @Override
   public void addListener(EmiFrameListener listener)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void close()
   {
      // TODO Auto-generated method stub

   }

   @Override
   public boolean isConnected()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void open() throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void removeListener(EmiFrameListener listener)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void send(EmiFrame message) throws IOException
   {
      // TODO Auto-generated method stub

   }

}
