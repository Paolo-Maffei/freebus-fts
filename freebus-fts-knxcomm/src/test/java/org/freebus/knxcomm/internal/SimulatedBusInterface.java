package org.freebus.knxcomm.internal;

import java.io.IOException;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.KNXConnection;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * A bus interface that is used for (unit) tests only.
 */
public class SimulatedBusInterface implements BusInterface
{
   @Override
   public void addListener(TelegramListener listener)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void close()
   {
      // TODO Auto-generated method stub

   }

   @Override
   public DataConnection connect(PhysicalAddress addr) throws IOException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public KNXConnection getConnection()
   {
      // TODO Auto-generated method stub
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
   public void open() throws IOException
   {
   }

   @Override
   public void removeListener(TelegramListener listener)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void send(Telegram telegram) throws IOException
   {
      // TODO Auto-generated method stub

   }

}
