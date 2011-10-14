package org.freebus.knxcomm.link.dummy;

import java.io.IOException;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.event.CloseEvent;
import org.freebus.knxcomm.internal.AbstractListenableLink;
import org.freebus.knxcomm.link.Link;
import org.freebus.knxcomm.types.LinkMode;

/**
 * A dummy KNX connection that does nothing.
 */
public class DummyLink extends AbstractListenableLink implements Link
{
   private PhysicalAddress busAddr = PhysicalAddress.NULL;
   private boolean connected = false;
   private LinkMode mode;

   /**
    * {@inheritDoc}
    */
   @Override
   public void open(LinkMode mode) throws IOException
   {
      this.mode = mode;
      connected = true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      this.mode = null;
      connected = false;
      fireLinkClosed(new CloseEvent(this));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isConnected()
   {
      return connected;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setLinkMode(LinkMode mode) throws IOException
   {
      this.mode = mode;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LinkMode getLinkMode()
   {
      return mode;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send(EmiFrame frame, boolean blocking) throws IOException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PhysicalAddress getPhysicalAddress()
   {
      return busAddr;
   }

}
