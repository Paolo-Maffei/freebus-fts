package org.freebus.fts.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.I18n;
import org.freebus.fts.MainWindow;
import org.freebus.fts.pages.PhysicalView;

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
      MainWindow.getInstance().showPage(PhysicalView.class, null);
   }
}
