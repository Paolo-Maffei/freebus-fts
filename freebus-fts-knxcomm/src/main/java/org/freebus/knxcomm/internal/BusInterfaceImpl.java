package org.freebus.knxcomm.internal;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.KNXConnection;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameListener;
import org.freebus.knxcomm.emi.EmiFrameType;
import org.freebus.knxcomm.emi.L_Data;
import org.freebus.knxcomm.emi.PEI_Switch;
import org.freebus.knxcomm.telegram.PhysicalAddress;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * {@link BusInterface} implementation.
 * Use {@link BusInterfaceFactory} to get a bus interface object.
 */
public class BusInterfaceImpl implements BusInterface, EmiFrameListener
{
   protected final CopyOnWriteArrayList<TelegramListener> listeners = new CopyOnWriteArrayList<TelegramListener>();
   protected final Map<PhysicalAddress, DataConnection> connections = new ConcurrentHashMap<PhysicalAddress, DataConnection>();
   private final KNXConnection con;

   /**
    * Create a bus-interface object that uses the given connection for the bus communication.
    */
   public BusInterfaceImpl(KNXConnection con)
   {
      this.con = con;
//      final Config cfg = Config.getInstance();
//      con = new SerialFt12Connection(cfg.getCommPort());
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
   public void close()
   {
      con.removeListener(this);
      con.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DataConnection connect(PhysicalAddress addr) throws IOException
   {
      if (con == null) throw new IOException("Not open");

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
      final EmiFrameType type = frame.getType();
      if (type == EmiFrameType.L_DATA_IND)
      {
         final Telegram telegram = ((L_Data.ind) frame).getTelegram();
         notifyListenersReceived(telegram);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void frameSent(EmiFrame frame)
   {
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
    * {@inheritDoc}
    */
   @Override
   public void open() throws IOException
   {
      con.open();
      con.addListener(this);

      // A pei_switch that EIBD sends on startup, so we do it here too
      con.send(new PEI_Switch.req(PEI_Switch.Mode.INIT));

      // Switch to link mode
      con.send(new PEI_Switch.req(PEI_Switch.Mode.LINK));
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
      if (con == null) throw new IOException("Not open");

      con.send(new L_Data.req(telegram));
   }
}
