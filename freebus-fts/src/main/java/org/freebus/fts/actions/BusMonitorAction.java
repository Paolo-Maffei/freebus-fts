package org.freebus.fts.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.MainWindow;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.pages.BusMonitor;

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
      MainWindow.getInstance().showUniquePage(BusMonitor.class, null);
   }
}
