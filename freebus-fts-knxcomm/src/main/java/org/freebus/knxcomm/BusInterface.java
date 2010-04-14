package org.freebus.knxcomm;

import java.io.IOException;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.emi.types.EmiFrameType;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * Interface for classes that do the communication with the KNX/EIB bus.
 */
public interface BusInterface
{
   /**
    * Open the connection to the bus, using the preferred connection type and
    * settings, taken from the application's configuration.
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
    * Send a telegram to the bus.
    *
    * @throws IOException
    */
   public void send(Telegram telegram) throws IOException;

   /**
    * Open a data connection to the device with the given physical address. Also
    * calls {@link DataConnection#open()}.
    * <p>
    * Always close the connection with {@link DataConnection#close()} after
    * using it.
    *
    * @param addr - the physical address of the target device.
    * @return The connection.
    */
   public DataConnection connect(PhysicalAddress addr) throws IOException;

   /**
    * @return the internal KNX/EIB bus connection.
    */
   public KNXConnection getConnection();

   /**
    * Add a listener that gets informed when telegrams are sent or received.
    * Listeners are not informed about confirmations
    * {@link EmiFrameType#isConfirmation()}.
    *
    * @param listener - the listener object.
    */
   public void addListener(TelegramListener listener);

   /**
    * Remove a listener.
    *
    * @param listener - the listener object.
    */
   public void removeListener(TelegramListener listener);

   /**
    * Returns the physical address of the BCU that is used to access the KNX/EIB
    * bus. Valid after a successful {@link #open()}.
    *
    * @return the physical address of the BCU.
    */
   public PhysicalAddress getPhysicalAddress();
}
