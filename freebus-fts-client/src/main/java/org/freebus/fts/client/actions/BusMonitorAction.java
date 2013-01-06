package org.freebus.fts.client.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import org.freebus.fts.client.application.MainWindow;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.editors.busmonitor.BusMonitor;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.services.ImageCache;
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
      BusInterface bus = null;

      try
      {
         mainWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         bus = BusInterfaceFactory.getBusInterface();
      }
      catch (Exception ex)
      {
         Dialogs.showExceptionDialog(ex, I18n.getMessage("BusMonitor.ErrOpenBus"));
      }
      finally
      {
         mainWin.setCursor(Cursor.getDefaultCursor());
      }

      if (bus != null)
         mainWin.showUniqueEditor(BusMonitor.class, bus);
   }
}
