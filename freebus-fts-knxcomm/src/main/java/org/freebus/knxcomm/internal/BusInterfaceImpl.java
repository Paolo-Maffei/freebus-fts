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
import org.freebus.knxcomm.emi.PEISwitchMode;
import org.freebus.knxcomm.emi.PEI_Identify_con;
import org.freebus.knxcomm.emi.PEI_Identify_req;
import org.freebus.knxcomm.emi.PEI_Switch_req;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * {@link BusInterface} implementation.
 * Use {@link BusInterfaceFactory} to get a bus interface object.
 */
public class BusInterfaceImpl implements BusInterface, EmiFrameListener
{
   protected final CopyOnWriteArraySet<TelegramListener> listeners = new CopyOnWriteArraySet<TelegramListener>();
   protected final Map<PhysicalAddress, DataConnection> connections = new ConcurrentHashMap<PhysicalAddress, DataConnection>();
   private PhysicalAddress physicalAddr;
   private final KNXConnection con;

   /**
    * Create a bus-interface object that uses the given connection for the bus communication.
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
      if (frame instanceof PEI_Identify_con)
      {
         Logger.getLogger(getClass()).info(frame);
         physicalAddr = new PhysicalAddress(((PEI_Identify_con) frame).getAddr());
      }
// TODO check Second condition
      //if (frame instanceof EmiTelegramFrame && !frame.getType().isConfirmation())
      if (frame instanceof EmiTelegramFrame )
      {
         final Telegram telegram = ((EmiTelegramFrame) frame).getTelegram();
         notifyListenersReceived(telegram);
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
      return physicalAddr;
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
      physicalAddr = null;

      con.open();
      con.addListener(this);

      // Identify the BCU
      con.send(new PEI_Identify_req());

      // A pei_switch that EIBD sends on startup, so we do it here too
      con.send(new PEI_Switch_req(PEISwitchMode.INIT));

      // Switch to bus monitor mode
      con.send(new PEI_Switch_req(PEISwitchMode.BUSMON));
      //con.send(new PEI_Switch_req(PEISwitchMode.LINK));
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
//	TODO check if null address is not allowed
//      if (PhysicalAddress.NULL.equals(telegram.getFrom()))
//         telegram.setFrom(physicalAddr);

      con.send(new L_Data_req(telegram));
   }
}
