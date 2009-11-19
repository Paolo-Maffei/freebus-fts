package org.freebus.fts.gui.actions;

import org.eclipse.swt.events.SelectionEvent;
import org.freebus.fts.utils.I18n;

/**
 * Create a new project.
 */
public final class ActionProjectNew extends GenericAction
{
   ActionProjectNew()
   {
      super(I18n.getMessage("ActionProjectNew_Label"), I18n.getMessage("ActionProjectNew_ToolTip"), "icons/filenew");
   }

   @Override
   public void triggered(SelectionEvent event)
   {
   }
}
