package org.freebus.knxcomm;

import java.io.IOException;
import java.util.List;

import org.freebus.fts.common.address.Address;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

/**
 * A direct connection to a device on the KNX/EIB bus. Use
 * {@link BusInterface#connect(PhysicalAddress)} to open a data connection.
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
    * Send a telegram to the device. The given application is wrapped
    * into a proper telegram and sent to the device.
    *
    * @param application - the application to send
    */
   public void send(Application application) throws IOException;

   /**
    * Send a telegram to the device.
    * <p>
    * The telegram is corrected to match the requirements of the connected data
    * transfer: the {@link Telegram#setFrom(PhysicalAddress) sender}, the
    * {@link Telegram#setDest(Address) receiver}, the
    * {@link Telegram#setTransport(Transport) transport type}, and the
    * {@link Telegram#setSequence(int) sequence number} are set.
    *
    * @param telegram - the telegram to send
    *
    * @throws IOException
    */
   public void send(Telegram telegram) throws IOException;

   /**
    * Receive a telegram from the device. Waits until a telegram is received.
    *
    * @param timeout - how long to wait, in milliseconds, -1 waits infinitely.
    *
    * @return the received telegram
    *
    * @throws IOException
    */
   public Telegram receive(int timeout) throws IOException;

   /**
    * Receive multiple telegrams from the device. Waits until the timeout
    * is over and returns all telegrams that arrived within the time,
    * and that were in the receive queue.
    *
    * @param timeout - how long to wait, in milliseconds, 0 to not wait at all.
    *
    * @return a list with the received telegrams.
    *
    * @throws IOException
    */
   public List<Telegram> receiveMultiple(int timeout) throws IOException;

   /**
    * Select if confirmation telegrams shall be received by the connection.
    * Default is that confirmations are discarded.
    *
    * @param enable - to enable receiving of confirmations
    */
   public void setReceiveConfirmations(boolean enable);
}
