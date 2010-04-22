package org.freebus.knxcomm.internal;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.KNXConnection;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameListener;
import org.freebus.knxcomm.emi.EmiTelegramFrame;
import org.freebus.knxcomm.emi.L_Data_req;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.types.LinkMode;

/**
 * {@link BusInterface} implementation. Use {@link BusInterfaceFactory} to get a
 * bus interface object.
 */
public class BusInterfaceImpl implements BusInterface, EmiFrameListener
{
   protected final CopyOnWriteArraySet<TelegramListener> listeners = new CopyOnWriteArraySet<TelegramListener>();
   protected final Map<PhysicalAddress, DataConnection> connections = new ConcurrentHashMap<PhysicalAddress, DataConnection>();
   private final KNXConnection con;

   /**
    * Create a bus-interface object that uses the given connection for the bus
    * communication.
    */
   public BusInterfaceImpl(KNXConnection con)
   {
      this.con = con;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addListener(TelegramListener listener)
   {
      listeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void open(LinkMode mode) throws IOException
   {
      con.open(mode);
      con.addListener(this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      con.removeListener(this);
      con.close();
   }

   /**
    * {@inheritDoc}
    */
   public void setLinkMode(LinkMode mode) throws IOException
   {
      Logger.getLogger(getClass()).debug("Switching to " + mode + " link mode");
      con.setLinkMode(mode);
   }

   /**
    * {@inheritDoc}
    */
   public LinkMode getLinkMode()
   {
      return con.getLinkMode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DataConnection connect(PhysicalAddress addr) throws IOException
   {
      if (con == null)
         throw new IOException("Not open");

      if (getLinkMode() == LinkMode.BusMonitor)
         throw new IllegalAccessError("bus monitor link mode is read only");

      final DataConnection dataCon = new DataConnectionImpl(addr, this);
      dataCon.open();

      connections.put(addr, dataCon);
      return dataCon;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void frameReceived(EmiFrame frame)
   {
      if (frame instanceof EmiTelegramFrame)
      {
         final Telegram telegram = ((EmiTelegramFrame) frame).getTelegram();

         if (frame.getType().isConfirmation())
            notifyListenersSendConfirmed(telegram);
         else notifyListenersReceived(telegram);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void frameSent(EmiFrame frame)
   {
      if (frame instanceof EmiTelegramFrame)
      {
         final Telegram telegram = ((EmiTelegramFrame) frame).getTelegram();
         notifyListenersSent(telegram);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public KNXConnection getConnection()
   {
      return con;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PhysicalAddress getPhysicalAddress()
   {
      return con.getPhysicalAddress();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isConnected()
   {
      return con != null && con.isConnected();
   }

   /**
    * Notify all listeners that the given telegram was received.
    */
   protected void notifyListenersReceived(final Telegram telegram)
   {
      for (TelegramListener listener : listeners)
         listener.telegramReceived(telegram);
   }

   /**
    * Notify all listeners that the given telegram was sent.
    */
   protected void notifyListenersSent(final Telegram telegram)
   {
      for (TelegramListener listener : listeners)
         listener.telegramSent(telegram);
   }

   /**
    * Notify all listeners that the given telegram was received as a
    * confirmation to a sent telegram. This is a telegram that was received with
    * {@link EmiFrame#getType()}.isConfirmation()
    */
   protected void notifyListenersSendConfirmed(final Telegram telegram)
   {
      for (TelegramListener listener : listeners)
         listener.telegramSendConfirmed(telegram);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeListener(TelegramListener listener)
   {
      listeners.remove(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send(Telegram telegram) throws IOException
   {
      if (con == null)
         throw new IOException("Not open");

      if (getLinkMode() == LinkMode.BusMonitor)
         throw new IllegalAccessError("bus monitor link mode is read only");

      final PhysicalAddress from = telegram.getFrom();
      if (from == null || PhysicalAddress.NULL.equals(from))
         telegram.setFrom(getPhysicalAddress());

      con.send(new L_Data_req(telegram));
   }
}
