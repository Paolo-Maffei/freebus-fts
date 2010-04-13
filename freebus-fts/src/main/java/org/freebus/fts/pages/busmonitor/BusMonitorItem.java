package org.freebus.fts.pages.busmonitor;

import java.util.Calendar;
import java.util.Date;

import org.freebus.fts.pages.BusMonitor;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * An item of the {@link BusMonitor} bus-monitor.
 */
public final class BusMonitorItem
{
   private final Telegram telegram;
   private final boolean received;
   private final Date when;

   /**
    * Create a bus-monitor item.
    *
    * @param when - The time when the telegram was received.
    * @param telegram - The telegram that the item represents.
    * @param received - True if the telegram was received, false if it was sent.
    *
    * @see Calendar#getTimeInMillis()
    */
   public BusMonitorItem(Date when, Telegram telegram, boolean received)
   {
      this.when = when;
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
    * @return the date when the telegram was received.
    */
   public Date getWhen()
   {
      return when;
   }

   /**
    * @return The telegram.
    */
   public Telegram getTelegram()
   {
      return telegram;
   }
}
