package org.freebus.fts.client.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.client.application.MainWindow;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.dialogs.About;

/**
 * Show the "about" dialog.
 */
public final class AboutAction extends BasicAction
{
   private static final long serialVersionUID = 8511540400150309373L;

   /**
    * Create an action object.
    */
   AboutAction()
   {
      super(I18n.getMessage("AboutAction.Name"), null, null);
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      final About dlg = new About(MainWindow.getInstance());
      dlg.center();
      dlg.setVisible(true);
   }
}
