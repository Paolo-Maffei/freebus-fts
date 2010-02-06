package org.freebus.fts.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowEvent;

import javax.swing.KeyStroke;

import org.freebus.fts.MainWindow;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.persistence.db.DatabaseResources;

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
      Config.getInstance().save();

      final MainWindow win = MainWindow.getInstance();
      win.dispatchEvent(new WindowEvent(win, WindowEvent.WINDOW_CLOSING));

      DatabaseResources.close();
   }

}
