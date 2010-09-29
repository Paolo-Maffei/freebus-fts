package org.freebus.knxcomm.internal;

import java.io.IOException;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.link.Link;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramListener;
import org.freebus.knxcomm.types.LinkMode;

/**
 * A bus interface that is used for (unit) tests only.
 */
public class SimulatedBusInterface implements BusInterface
{
   @Override
   public void addListener(TelegramListener listener)
   {
   }

   @Override
   public void open(LinkMode mode) throws IOException
   {
   }

   @Override
   public void close()
   {
   }

   @Override
   public DataConnection connect(PhysicalAddress addr, Priority priority) throws IOException
   {
      return null;
   }

   @Override
   public Link getConnection()
   {
      return null;
   }

   @Override
   public PhysicalAddress getPhysicalAddress()
   {
      return PhysicalAddress.ONE;
   }

   @Override
   public boolean isConnected()
   {
      return true;
   }

   @Override
   public void removeListener(TelegramListener listener)
   {
   }

   @Override
   public void send(Telegram telegram) throws IOException
   {
   }

   @Override
   public void setLinkMode(LinkMode mode) throws IOException
   {
   }

   @Override
   public LinkMode getLinkMode()
   {
      return null;
   }

}
