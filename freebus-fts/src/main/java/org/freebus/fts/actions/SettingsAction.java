package org.freebus.fts.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import org.freebus.fts.I18n;
import org.freebus.fts.MainWindow;
import org.freebus.fts.dialogs.Settings;
import org.freebus.fts.elements.services.ImageCache;

/**
 * Open the settings dialog.
 */
public final class SettingsAction extends BasicAction
{
   private static final long serialVersionUID = 5649188834706980695L;

   /**
    * Create an action object.
    */
   SettingsAction()
   {
      super(I18n.getMessage("SettingsAction.Name"), I18n.getMessage("SettingsAction.ToolTip"), ImageCache.getIcon("icons/configure"));
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      final MainWindow mainWin = MainWindow.getInstance();
      try
      {
         mainWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         final Settings dlg = new Settings(mainWin);
         dlg.setVisible(true);
      }
      finally
      {
         mainWin.setCursor(Cursor.getDefaultCursor());
      }
   }
}
