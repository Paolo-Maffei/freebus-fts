package org.freebus.fts.comm;

import java.io.IOException;

import org.freebus.fts.emi.EmiMessage;

/**
 * Interface for connections to the EIB/KNX bus. The interfaces speak
 * the connection protocol, the {@link BusConnection} connection classes
 * are for the data transport.
 */
public interface BusInterface
{
   /**
    * Open the connection to the communication device.
    * @throws IOException
    */
   public void open() throws IOException;

   /**
    * Close the connection to the communication device.
    */
   public void close();

   /**
    * @return true if the connection to the communication device is established.
    */
   public boolean isOpen();

   /**
    * Send a message to the communication device.
    * @throws IOException 
    */
   public void write(EmiMessage message) throws IOException;

   /**
    * Add a bus listener. Listeners get called when messages arrive.
    */
   public void addListener(BusListener listener);

   /**
    * Remove a bus listener.
    */
   public void removeListener(BusListener listener);
}
