package org.freebus.knxcomm;

import java.io.IOException;
import java.util.List;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.Application;

/**
 * A direct connection to a device on the KNX/EIB bus. Use
 * {@link BusInterface#connect(PhysicalAddress)} to open a data connection.
 *
 * The data connection is {@link Application application} oriented. Proper
 * telegrams for transporting the applications and its data are created as
 * needed internally.
 *
 * Always close the connection with {@link #close} when done.
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

   /**
    * Send a telegram to the device. The given application is wrapped into a
    * proper telegram and sent to the device.
    *
    * @param application - the application to send
    */
   public void send(Application application) throws IOException;

   /**
    * Receive an {@link Application application} from the device. Waits until a
    * telegram is received and extracts the application from it.
    *
    * @param timeout - how long to wait, in milliseconds, -1 waits infinitely.
    *
    * @return the received application
    *
    * @throws IOException
    */
   public Application receive(int timeout) throws IOException;

   /**
    * Receive multiple {@link Application application}s from the device. Waits
    * until the timeout is over and returns all applications that arrived within
    * the time, and that were in the receive queue.
    *
    * @param timeout - how long to wait, in milliseconds, 0 to not wait at all.
    *
    * @return a list with the received applications.
    *
    * @throws IOException
    */
   public List<Application> receiveMultiple(int timeout) throws IOException;

   /**
    * Select if confirmation telegrams shall be received by the connection.
    * Default is that confirmations are discarded.
    *
    * @param enable - to enable receiving of confirmations
    */
   public void setReceiveConfirmations(boolean enable);
}
