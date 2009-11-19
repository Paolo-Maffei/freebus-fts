package org.freebus.fts.gui.actions;

import org.eclipse.swt.events.SelectionEvent;
import org.freebus.fts.gui.MainWindow;
import org.freebus.fts.utils.I18n;

/**
 * Exit the application.
 */
public final class ActionExit extends GenericAction
{
   ActionExit()
   {
      super(I18n.getMessage("ActionExit_Label"), I18n.getMessage("ActionExit_ToolTip"), "icons/exit");
   }

   @Override
   public void triggered(SelectionEvent event)
   {
      final MainWindow mainWin = MainWindow.getInstance();
      mainWin.getShell().close();
      mainWin.getDisplay().dispose();
   }

}
