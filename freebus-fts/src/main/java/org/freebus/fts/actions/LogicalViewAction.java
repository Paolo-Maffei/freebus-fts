package org.freebus.fts.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.I18n;
import org.freebus.fts.MainWindow;
import org.freebus.fts.view.LogicalView;

/**
 * Show the logical view
 */
public final class LogicalViewAction extends BasicAction
{
   private static final long serialVersionUID = -2351157727237882875L;

   /**
    * Create an action object.
    */
   LogicalViewAction()
   {
      super(I18n.getMessage("LogicalViewAction.Name"), I18n.getMessage("LogicalViewAction.ToolTip"), null);
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      MainWindow.getInstance().showPage(LogicalView.class, null);
   }
}
