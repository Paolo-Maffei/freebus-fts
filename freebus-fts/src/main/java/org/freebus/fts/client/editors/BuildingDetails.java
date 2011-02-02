package org.freebus.fts.client.editors;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.workbench.WorkBenchEditor;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectListener;

/**
 * An editor page for a {@link Building building}. 
 */
public final class BuildingDetails extends WorkBenchEditor
{
   private static final long serialVersionUID = -2709220571933275335L;

   private final JTextField nameEdit = new JTextField();
   private final JTextArea notesEdit = new JTextArea();

   private boolean updating;
   private Building building;

   /**
    * Create an building details page.
    */
   public BuildingDetails()
   {
      setLayout(new GridBagLayout());
      setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 32));

      final Insets blkInsets = new Insets(16, 0, 0, 0);
      final Insets stdInsets = new Insets(0, 0, 0, 0);
      final int nw = GridBagConstraints.NORTHWEST;
//      final int e = GridBagConstraints.EAST;
      final int w = GridBagConstraints.WEST;
      final int gridWidth = 4;
      int gridy = -1;
      JLabel lbl;

      //
      // Building name
      //
      lbl = new JLabel(I18n.getMessage("BuildingDetails.Name") + ": ");
      add(lbl, new GridBagConstraints(0, ++gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      add(nameEdit, new GridBagConstraints(1, gridy, 1, 1, 10, 1, w, GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));
      nameEdit.addFocusListener(new FocusListener()
      {
         private String prevValue;

         @Override
         public void focusLost(FocusEvent e)
         {
            if (nameEdit.getText().equals(prevValue))
               return;

            building.setName(nameEdit.getText());
            fireModified(building);
         }

         @Override
         public void focusGained(FocusEvent e)
         {
            prevValue = nameEdit.getText();
         }
      });

      add(new JPanel(), new GridBagConstraints(2, gridy, 1, 1, 10, 1, w, GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));

      //
      // Notes
      //
      lbl = new JLabel(I18n.getMessage("BuildingDetails.Notes"));
      add(lbl, new GridBagConstraints(0, ++gridy, gridWidth, 1, 1, 1, w, GridBagConstraints.NONE, blkInsets, 0, 0));

      final JScrollPane scpNotes = new JScrollPane(notesEdit);
      add(scpNotes, new GridBagConstraints(0, ++gridy, gridWidth, 1, 1, 1, nw, GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));
      scpNotes.setPreferredSize(new Dimension(100, 100));

      //
      // Page filler
      //
      add(new Label(), new GridBagConstraints(0, ++gridy, gridWidth, 1, 1, 100, w, GridBagConstraints.BOTH, stdInsets, 0, 0));

      ProjectManager.addListener(projectListener);
   }

   /*
    * Listener for project changes
    */
   private final ProjectListener projectListener = new ProjectListener()
   {
      @Override
      public void projectComponentRemoved(Object obj)
      {
         if (obj == building)
            close();
      }
      
      @Override
      public void projectComponentModified(Object obj)
      {
         if (!updating)
         {
            if (obj == building)
               updateContents();
         }
      }

      @Override
      public void projectComponentAdded(Object obj)
      {
      }
      
      @Override
      public void projectChanged(Project project)
      {
         close();
      }
   };

   /**
    * {@inheritDoc}
    */
   @Override
   protected void closeEvent()
   {
      ProjectManager.removeListener(projectListener);
   }

   /**
    * Set the line to show / edit.
    */
   @Override
   public void objectChanged(Object o)
   {
      this.building = (Building) o;
      updateContents();
   }

   /**
    * An object was modified.
    * 
    * obj - The object that was modified.
    */
   protected void fireModified(final Object obj)
   {
      if (!updating && obj != null)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            @Override
            public void run()
            {
               ProjectManager.fireComponentModified(obj);
            }
         });
      }
   }

   /**
    * Update the page's contents.
    */
   public void updateContents()
   {
      updating = true;

      final String title = building.getName();
      setName(title);

      nameEdit.setText(building.getName());
      // TODO notesEdit.setText(line.getNotes());

      updating = false;
   }
}
