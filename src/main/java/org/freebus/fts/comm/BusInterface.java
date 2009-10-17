package org.freebus.fts.comm;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Display;
import org.freebus.fts.Config;

/**
 * Base class for bus interfaces
 */
public class BusInterface
{
   static private BusInterface instance = null;
   protected final Set<BusListener> listeners = new HashSet<BusListener>();
   
   /**
    * Returns the bus interface as selected in the configuration.
    */
   static public BusInterface getInstance() throws BusConnectException
   {
      if (instance==null)
      {
         final ConnectType conType = Config.getConfig().getCommType();
         try
         {
            instance = conType.busInterfaceClass.newInstance();
         }
         catch (Exception e)
         {
            throw new RuntimeException("Cannot create bus interface: "+conType.name, e);
         }
      }
      return instance;
   }

   /**
    * Remove the bus-interface.
    */
   static public void disposeInstance()
   {
      if (instance==null) return;
      instance.close();
      instance = null;
   }

   /**
    * Close all connections of the interface.
    */
   protected void close()
   {
   }

   /**
    * Create a bus-interface and sets the global instance.
    */
   protected BusInterface() throws BusConnectException
   {
      instance = this;
   }

   /**
    * Add a listener.
    */
   public void addListener(BusListener listener)
   {
      listeners.add(listener);
   }

   /**
    * Remove a listener.
    */
   public void removeListener(BusListener listener)
   {
      listeners.remove(listener);
   }
   
   /**
    * An EIB telegram was received.
    * 
    * @param data is the data buffer
    * @param start is the offset within the buffer at which the telegram starts
    * @param length is the length of the telegram data in bytes
    */
   protected void telegramReceived(int[] data, int start, int length)
   {
      final Telegram telegram = new Telegram(TelegramType.UNKNOWN, 0);

      telegram.ctrl = data[start+1];
      telegram.from = (data[start+2]<<8) + data[start+3];
      telegram.recv = (data[start+4]<<8) + data[start+5];
      telegram.drl = data[start+6];
      telegram.recvIsGroup = (telegram.drl & (1<<7)) != 0;

//      System.out.printf("from %d.%d.%d ", data[start+2]>>4, data[start+2]&15, data[start+3]);

      int dataLen = (telegram.drl & 15) + 1;
      telegram.data = new int[dataLen];
      for (int i=start+7, j=0; dataLen>0; ++i, --dataLen, ++j)
         telegram.data[j] = data[i];

      Display.getDefault().syncExec(new Runnable()
      {
         @Override
         public void run()
         {
            for (BusListener listener: listeners)
               listener.telegramReceived(telegram);
         }
      });
   }
}
