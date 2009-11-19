package org.freebus.fts.gui.actions;

import org.eclipse.swt.events.SelectionEvent;
import org.freebus.fts.utils.I18n;

/**
 * Open an existing project.
 */
public final class ActionProjectSave extends GenericAction
{
   ActionProjectSave()
   {
      super(I18n.getMessage("ActionProjectSave_Label"), I18n.getMessage("ActionProjectSave_ToolTip"), "icons/filesave");
   }

   @Override
   public void triggered(SelectionEvent event)
   {
   }
}
