package org.freebus.fts.client.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.freebus.fts.client.MainWindow;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectFactory;

/**
 * Save the current project.
 */
public final class SaveProjectAction extends BasicAction
{
   private static final long serialVersionUID = -2759367485810122303L;

   /**
    * Create an action object.
    */
   SaveProjectAction()
   {
      super(I18n.getMessage("SaveProjectAction.Name"), I18n.getMessage("SaveProjectAction.ToolTip"), ImageCache
            .getIcon("icons/filesave"));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent event)
   {
      final ProjectFactory projectFactory = ProjectManager.getProjectFactory();
      final Project project = ProjectManager.getProject();
      final MainWindow mainWin = MainWindow.getInstance();

      try
      {
         mainWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

         projectFactory.getTransaction().begin();

         if (project.getId() == 0)
            projectFactory.getProjectService().persist(project);
         else projectFactory.getProjectService().merge(project);

         projectFactory.getTransaction().commit();

         Logger.getLogger(getClass()).info("Project saved");
      }
      catch (Exception e)
      {
         try
         {
            projectFactory.getTransaction().rollback();
         }
         catch (Exception e1)
         {
         }

         Dialogs.showExceptionDialog(e, I18n.getMessage("SaveProjectAction.ErrSaving"));
      }
      finally
      {
         mainWin.setCursor(Cursor.getDefaultCursor());
      }
   }
}
