package org.freebus.knxcomm;

import java.io.IOException;

import org.freebus.knxcomm.telegram.PhysicalAddress;

/**
 * A direct connection to a device on the KNX/EIB bus.
 * Use {@link BusInterface#connect(PhysicalAddress)} to open a data connection.
 *
 * Always close the connection with {@link #close} when done.
 * 
 * TODO not yet implemented!
 */
public interface DataConnection
{
   /**
    * Open the connection.
    * 
    * @throws IOException if the connection is not closed
    */
   public void open() throws IOException;

   /**
    * @return true if the connection is opened.
    */
   public boolean isOpened();

   /**
    * Close the connection.
    */
   public void close();
}
