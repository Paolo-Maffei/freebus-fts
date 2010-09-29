package org.freebus.knxcomm.link;

import java.io.IOException;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameListener;
import org.freebus.knxcomm.types.LinkMode;

/**
 * A KNX/EIB (low level) bus connection link. For communication with the bus,
 * the {@link BusInterface} is usually used. The bus interface gets a
 * {@link Link link} which it uses to do the actual communication with the
 * bus. But you most definitely want to use {@link BusInterface} and not this
 * class for doing the bus communication.
 * 
 * @see BusInterface An interface for talking with the KNX/EIB bus.
 */
public interface Link
{
   /**
    * Open the connection to the bus.
    * 
    * @param mode - the link mode to set for the connection.
    */
   public void open(LinkMode mode) throws IOException;

   /**
    * Close the bus connection.
    */
   public void close();

   /**
    * @return true if the bus connection is opened.
    */
   public boolean isConnected();

   /**
    * Switch the link mode.
    * 
    * @param mode - the link mode to switch to
    */
   public void setLinkMode(LinkMode mode) throws IOException;

   /**
    * @return the currently active link mode.
    */
   public LinkMode getLinkMode();

   /**
    * Send an EMI frame to the bus.
    * 
    * @throws IOException
    */
   public void send(EmiFrame frame) throws IOException;

   /**
    * Add a frame listener. Frame listeners get called when {@link EmiFrame}s
    * arrive.
    */
   public void addListener(EmiFrameListener listener);

   /**
    * Remove a frame listener.
    */
   public void removeListener(EmiFrameListener listener);

   /**
    * @return the physical address of the bus interface.
    */
   public PhysicalAddress getPhysicalAddress();
}
