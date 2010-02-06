package org.freebus.fts.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.freebus.fts.MainWindow;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.dialogs.ProjectSelector;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;

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
      final MainWindow mainWin = MainWindow.getInstance();

      try
      {
         mainWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         final ProjectSelector dlg = new ProjectSelector(mainWin);

         dlg.addWindowListener(new WindowAdapter()
         {
            @Override
            public void windowClosed(WindowEvent e)
            {
               if (!dlg.isAccepted()) return;
               final Project project = dlg.getSelectedProject();
               if (project == null) return;
               ProjectManager.setProject(project);
               Logger.getLogger(getClass()).info("Project loaded: " + project.getName());
            }
         });

         dlg.setVisible(true);
      }
      finally
      {
         mainWin.setCursor(Cursor.getDefaultCursor());
      }
   }
}
