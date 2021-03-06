package org.freebus.fts.client.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.client.FTS;
import org.freebus.fts.client.core.I18n;

/**
 * Restart the application.
 */
public final class RestartAction extends BasicAction
{
   private static final long serialVersionUID = 3076971380544988695L;

   /**
    * Create an action object.
    */
   RestartAction()
   {
      super(I18n.getMessage("RestartAction.Name"), I18n.getMessage("RestartAction.ToolTip"), null);
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      FTS.getInstance().restart();
   }
}
