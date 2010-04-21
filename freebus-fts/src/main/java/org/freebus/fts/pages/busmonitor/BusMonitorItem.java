package org.freebus.fts.pages.busmonitor;

import java.util.Calendar;
import java.util.Date;

import org.freebus.fts.pages.BusMonitor;
import org.freebus.knxcomm.emi.EmiFrame;

/**
 * An item of the {@link SerialBusMonitor} bus-monitor.
 */
public final class BusMonitorItem
{
   private final int id;
   private final EmiFrame frame;
   private final Date when;

   /**
    * Create a bus-monitor item.
    *
    * @param id - the numerical id of the frame.
    * @param when - the time when the frame was received.
    * @param frame - the EMI frame that the item represents.
    *
    * @see Calendar#getTimeInMillis()
    */
   public BusMonitorItem(int id, Date when, EmiFrame frame)
   {
      this.id = id;
      this.when = when;
      this.frame = frame;
   }

   /**
    * @return the numerical id of the frame.
    */
   public int getId()
   {
      return id;
   }

   /**
    * @return the date when the frame was received.
    */
   public Date getWhen()
   {
      return when;
   }

   /**
    * @return The EMI frame.
    */
   public EmiFrame getFrame()
   {
      return frame;
   }
}
