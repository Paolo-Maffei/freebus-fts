package org.freebus.knxcomm;

import java.io.IOException;

import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameListener;

/**
 * A KNX/EIB (low level) bus connection. For communication with the bus, the
 * {@link BusInterface} is usually used. The bus interface gets a
 * {@link KNXConnection} which it uses to do the actual communication with the
 * bus. But you most definitely want to use {@link BusInterface} and this
 * class for doing the bus communication.
 * 
 * @see {@link BusInterface} - An interface for talking with the KNX/EIB bus.
 */
public interface KNXConnection
{
   /**
    * Open the connection to the bus.
    */
   public void open() throws IOException;

   /**
    * Close the bus connection.
    */
   public void close();

   /**
    * @return true if the bus connection is opened.
    */
   public boolean isConnected();

   /**
    * Send an EMI frame to the bus.
    * 
    * @throws IOException
    */
   public void send(EmiFrame message) throws IOException;

   /**
    * Add a frame listener. Frame listeners get called when {@link EmiFrame}s
    * arrive.
    */
   public void addListener(EmiFrameListener listener);

   /**
    * Remove a frame listener.
    */
   public void removeListener(EmiFrameListener listener);
}