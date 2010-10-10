package org.freebus.fts.actions;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.freebus.fts.I18n;
import org.freebus.fts.MainWindow;
import org.freebus.fts.dialogs.ProjectProperties;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;

/**
 * Create a new project
 */
public final class NewProjectAction extends BasicAction
{
   private static final long serialVersionUID = 3873238375955915904L;

   /**
    * Create an action object.
    */
   NewProjectAction()
   {
      super(I18n.getMessage("NewProjectAction.Name"), I18n.getMessage("NewProjectAction.ToolTip"), ImageCache
            .getIcon("icons/filenew"));
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      final Project newProject = new Project();

      final ProjectProperties dlg = new ProjectProperties(MainWindow.getInstance());
      dlg.setProject(newProject);
      dlg.setTitle(I18n.getMessage("ProjectProperties.TitleNew"));

      dlg.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosed(WindowEvent e)
         {
            if (dlg.isAccepted())
               ProjectManager.setProject(newProject);
         }
      });

      dlg.setVisible(true);
   }
}
