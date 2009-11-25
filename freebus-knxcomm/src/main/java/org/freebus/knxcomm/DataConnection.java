package org.freebus.knxcomm;

import java.io.IOException;

/**
 * A direct connection to a device on the KNX/EIB bus.
 * Use {@link BusInterface#connect} to open a data connection.
 *
 * Always close the connection with {@link #close} when done.
 * 
 * TODO not yet implemented!
 */
public interface DataConnection
{
   /**
    * Close the connection.
    */
   public void close();

   /**
    * Open the connection.
    */
   public void open() throws IOException;
}
