package org.freebus.fts.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.MainWindow;
import org.freebus.fts.core.I18n;
import org.freebus.fts.pages.TopologyView;

/**
 * Open the bus monitor.
 */
public final class TopologyViewAction extends BasicAction
{
   private static final long serialVersionUID = 6085721650959966808L;

   /**
    * Create an action object.
    */
   TopologyViewAction()
   {
      super(I18n.getMessage("TopologyViewAction.Name"), I18n.getMessage("TopologyViewAction.ToolTip"), null);
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      MainWindow.getInstance().showUniquePage(TopologyView.class, null);
   }
}
