package org.freebus.fts.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

import org.freebus.fts.client.FTS;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.elements.services.ImageCache;

/**
 * Exit the application.
 */
public final class ExitAction extends BasicAction
{
   private static final long serialVersionUID = 5649188834706980695L;

   /**
    * Create an action object.
    */
   ExitAction()
   {
      super(I18n.getMessage("ExitAction.Name"), I18n.getMessage("ExitAction.ToolTip"), ImageCache.getIcon("icons/exit"));

      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('Q', InputEvent.CTRL_DOWN_MASK));
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      FTS.getInstance().exit();
   }
}
