package org.freebus.fts.gui.actions;

import org.eclipse.swt.events.SelectionEvent;
import org.freebus.fts.gui.BusMonitor;
import org.freebus.fts.gui.MainWindow;
import org.freebus.fts.utils.I18n;

/**
 * Open the bus-monitor widget.
 */
public final class ActionBusMonitor extends GenericAction
{
   ActionBusMonitor()
   {
      super(I18n.getMessage("ActionBusMonitor_Label"), I18n.getMessage("ActionBusMonitor_ToolTip"), "icons/bus-monitor");
   }

   @Override
   public void triggered(SelectionEvent event)
   {
      final MainWindow mainWin = MainWindow.getInstance();
      mainWin.showTabPage(BusMonitor.class, null);
   }

}
