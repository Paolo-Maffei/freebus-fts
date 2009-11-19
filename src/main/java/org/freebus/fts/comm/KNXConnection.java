package org.freebus.fts.comm;

import java.io.IOException;

import org.freebus.fts.comm.emi.EmiFrame;

/**
 * Interface for a KNX/EIB bus connection.
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
