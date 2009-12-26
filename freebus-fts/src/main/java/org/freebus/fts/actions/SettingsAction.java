package org.freebus.fts.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.MainWindow;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.settings.Settings;

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
      final Settings dlg = new Settings(MainWindow.getInstance());
      dlg.setVisible(true);
   }
}
