package org.freebus.fts.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import org.freebus.fts.I18n;
import org.freebus.fts.MainWindow;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.pages.BusMonitor;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;

/**
 * Open the bus monitor.
 */
public final class BusMonitorAction extends BasicAction
{
   private static final long serialVersionUID = 6085721650959966808L;

   /**
    * Create an action object.
    */
   BusMonitorAction()
   {
      super(I18n.getMessage("BusMonitorAction.Name"), I18n.getMessage("BusMonitorAction.ToolTip"), ImageCache.getIcon("icons/bus-monitor"));
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      final MainWindow mainWin = MainWindow.getInstance();
      BusInterface bus;

      try
      {
         mainWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         bus = BusInterfaceFactory.getBusInterface();
      }
      finally
      {
         mainWin.setCursor(Cursor.getDefaultCursor());
      }

      if (bus != null)
         mainWin.showPage(BusMonitor.class, bus);
   }
}
