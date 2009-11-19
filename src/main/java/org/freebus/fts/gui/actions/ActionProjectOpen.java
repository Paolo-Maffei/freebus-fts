package org.freebus.fts.gui.actions;

import org.eclipse.swt.events.SelectionEvent;
import org.freebus.fts.utils.I18n;

/**
 * Open an existing project.
 */
public final class ActionProjectOpen extends GenericAction
{
   ActionProjectOpen()
   {
      super(I18n.getMessage("ActionProjectOpen_Label"), I18n.getMessage("ActionProjectOpen_ToolTip"), "icons/fileopen");
   }

   @Override
   public void triggered(SelectionEvent event)
   {
   }
}
