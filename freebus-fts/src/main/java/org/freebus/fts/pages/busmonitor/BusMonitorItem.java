package org.freebus.fts.pages.busmonitor;

import org.freebus.fts.pages.BusMonitor;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * An item of the {@link BusMonitor} bus-monitor.
 */
public final class BusMonitorItem
{
   private final Telegram telegram;
   private final boolean received;

   /**
    * Create a bus-monitor item.
    *
    * @param telegram - The telegram that the item represents.
    * @param received - True if the telegram was received, false if it was sent.
    */
   public BusMonitorItem(Telegram telegram, boolean received)
   {
      this.telegram = telegram;
      this.received = received;
   }

   /**
    * @return True the telegram was received, false if it was sent.
    */
   public boolean isReceived()
   {
      return received;
   }

   /**
    * @return The telegram.
    */
   public Telegram getTelegram()
   {
      return telegram;
   }
}
