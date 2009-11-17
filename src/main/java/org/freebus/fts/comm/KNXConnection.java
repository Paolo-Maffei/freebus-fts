package org.freebus.fts.comm;

import java.io.IOException;

import org.freebus.fts.emi.EmiMessage;

/**
 * Interface for a KNX/EIB bus connection.
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
   public boolean isOpen();

   /**
    * Send a message to the bus.
    * 
    * @throws IOException
    */
   public void write(EmiMessage message) throws IOException;

   /**
    * Add a frame listener. Frame listeners get called when {@link EmiMessage}s
    * arrive. This is basically the whole bus communication.
    */
   public void addListener(EmiFrameListener listener);

   /**
    * Remove a frame listener.
    */
   public void removeListener(EmiFrameListener listener);
}
