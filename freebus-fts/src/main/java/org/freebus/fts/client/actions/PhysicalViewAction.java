package org.freebus.fts.client.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.client.application.MainWindow;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.views.PhysicalView;

/**
 * Show the physical view
 */
public final class PhysicalViewAction extends BasicAction
{
   private static final long serialVersionUID = -1978117400718878973L;

   /**
    * Create an action object.
    */
   PhysicalViewAction()
   {
      super(I18n.getMessage("PhysicalViewAction.Name"), I18n.getMessage("PhysicalViewAction.ToolTip"), null);
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      MainWindow.getInstance().showUniquePanel(PhysicalView.class);
   }
}
