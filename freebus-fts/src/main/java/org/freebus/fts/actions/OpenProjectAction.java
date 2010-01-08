package org.freebus.fts.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;

/**
 * Open a project.
 */
public final class OpenProjectAction extends BasicAction
{
   private static final long serialVersionUID = -3511750343333514078L;

   /**
    * Create an action object.
    */
   OpenProjectAction()
   {
      super(I18n.getMessage("OpenProjectAction.Name"), I18n.getMessage("OpenProjectAction.ToolTip"), ImageCache
            .getIcon("icons/fileopen"));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
   }
}
