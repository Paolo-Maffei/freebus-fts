package org.freebus.knxcomm;

import java.io.IOException;

import org.freebus.fts.common.address.Address;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

/**
 * A direct connection to a device on the KNX/EIB bus. Use
 * {@link BusInterface#connect(PhysicalAddress)} to open a data connection.
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

   /**
    * Send a telegram to the device.
    * <p>
    * The telegram is corrected to match the requirements of the connected data
    * transfer: the {@link Telegram#setFrom(Address) sender}, the
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
    * @return the received telegram
    *
    * @throws IOException
    */
   public Telegram receive() throws IOException;
}
