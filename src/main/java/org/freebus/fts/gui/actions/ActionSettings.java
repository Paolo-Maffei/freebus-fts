package org.freebus.fts.gui.actions;

import org.eclipse.swt.events.SelectionEvent;
import org.freebus.fts.settings.Settings;
import org.freebus.fts.utils.I18n;

/**
 * Open the settings dialog.
 */
public final class ActionSettings extends GenericAction
{
   ActionSettings()
   {
      super(I18n.getMessage("ActionSettings_Label"), I18n.getMessage("ActionSettings_ToolTip"), "icons/configure");
   }

   @Override
   public void triggered(SelectionEvent event)
   {
      Settings.openDialog();
   }

}
