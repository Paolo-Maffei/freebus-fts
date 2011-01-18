package org.freebus.fts.client.actions;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.freebus.fts.client.MainWindow;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.dialogs.ProjectProperties;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;

/**
 * Edit/show the properties of the current project.
 */
public final class ProjectPropertiesAction extends BasicAction
{
   private static final long serialVersionUID = -1749206712566069496L;

   /**
    * Create an action object.
    */
   ProjectPropertiesAction()
   {
      super(I18n.getMessage("ProjectPropertiesAction.Name"), I18n.getMessage("ProjectPropertiesAction.ToolTip"), null);
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      final Project project = ProjectManager.getProject();
      final String oldProjectName = project.getName();

      final ProjectProperties dlg = new ProjectProperties(MainWindow.getInstance());
      dlg.setProject(project);
      dlg.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosed(WindowEvent e)
         {
            if (dlg.isAccepted() && !oldProjectName.equals(project.getName()))
               ProjectManager.fireProjectChanged();
         }
      });

      dlg.setVisible(true);
   }
}
