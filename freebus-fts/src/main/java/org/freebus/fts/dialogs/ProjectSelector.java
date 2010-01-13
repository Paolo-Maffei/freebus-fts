package org.freebus.fts.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.freebus.fts.components.Dialog;
import org.freebus.fts.core.I18n;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;

/**
 * Dialog for selecting a project from the database's list of projects.
 */
public final class ProjectSelector extends Dialog
{
   private static final long serialVersionUID = 7580878090288641271L;

   private final ProjectsModel projectsModel;
   private final JTable projectsTable;
   private final JScrollPane projectsView;
   private final JButton btnOpen;

   /**
    * Create a project-selector window.
    */
   public ProjectSelector(Window owner)
   {
      super(owner);
      setTitle(I18n.getMessage("ProjectSelector.Title"));
      setSize(600, 600);

      final Container body = getBodyPane();
      body.setLayout(new BorderLayout());

      projectsModel = new ProjectsModel();

      projectsTable = new JTable(projectsModel);
      projectsTable.setFillsViewportHeight(true);
      projectsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      projectsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent e)
         {
            final int idx = e.getLastIndex();
            btnOpen.setEnabled(idx >= 0);
         }
      });

      projectsView = new JScrollPane(projectsTable);
      projectsView.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      body.add(projectsView, BorderLayout.CENTER);

      btnOpen = new JButton(I18n.getMessage("Button.Open"));
      btnOpen.setEnabled(false);
      addButton(btnOpen, Dialog.ACCEPT);

      addButton(new JButton(I18n.getMessage("Button.Cancel")), Dialog.REJECT);
   }

   /**
    * @return The selected project, or null if no project is selected.
    */
   public Project getSelectedProject()
   {
      final int idx = projectsTable.getSelectionModel().getMaxSelectionIndex();
      if (idx < 0) return null;
      return projectsModel.getProjectAt(idx);
   }

   /**
    * Internal table-model for the {@link ProjectSelector} project-selection
    * dialog.
    */
   private class ProjectsModel extends AbstractTableModel
   {
      private static final long serialVersionUID = 4747236528702209278L;
      private List<Project> projects;

      /** Column-id of the project name column. */
      private static final int PROJECT_NAME_COLUMN = 0;

      /** Column-id of the project's last-modified time. */
      private static final int PROJECT_LAST_MODIFIED = 1;

      /**
       * Create a project-model that displays all projects of the database.
       */
      public ProjectsModel()
      {
         super();
         update();
      }

      /**
       * @return the project of the row rowIndex.
       */
      public Project getProjectAt(int rowIndex)
      {
         if (projects == null) return null;
         return projects.get(rowIndex);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int getColumnCount()
      {
         return 2;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getColumnName(int columnIndex)
      {
         switch (columnIndex)
         {
            case PROJECT_NAME_COLUMN:
               return I18n.getMessage("ProjectSelector.ColName");
            case PROJECT_LAST_MODIFIED:
               return I18n.getMessage("ProjectSelector.ColLastModified");
            default:
               return super.getColumnName(columnIndex);
         }
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public int getRowCount()
      {
         if (projects == null) return 0;
         return projects.size();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Object getValueAt(int rowIndex, int columnIndex)
      {
         if (projects == null) return null;
         final Project project = projects.get(rowIndex);

         switch (columnIndex)
         {
            case PROJECT_NAME_COLUMN:
               return project.getName();
            case PROJECT_LAST_MODIFIED:
               return project.getLastModified();
            default:
               return null;
         }
      }

      /**
       * Update the contents of the model.
       */
      public void update()
      {
         projects = ProjectManager.getDAOFactory().getProjectService().getProjects();
         fireTableDataChanged();
      }
   }
}
