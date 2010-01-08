package org.freebus.fts.dialogs;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.freebus.fts.components.Dialog;
import org.freebus.fts.core.I18n;
import org.freebus.fts.project.Project;

/**
 * Show/edit the properties of a project.
 */
public class ProjectProperties extends Dialog
{
   private static final long serialVersionUID = -2181288305995816556L;

   private final JTextField edtName;
   private final JTextArea edtDescription;
   private final JButton btnOk;
   private Project project;

   /**
    * Create a project-properties window.
    */
   public ProjectProperties(Window owner)
   {
      super(owner);
      setTitle(I18n.getMessage("ProjectProperties.Title"));

      final Container body = getBodyPane();

      body.setLayout(new GridBagLayout());
      setSize(400, 500);

      final Font captionFont = getFont().deriveFont(Font.BOLD);
      final Insets defaultInsets = new Insets(2, 4, 0, 4);
      int row = -1;
      JLabel lbl;

      lbl = new JLabel(I18n.getMessage("ProjectProperties.Name"));
      lbl.setFont(captionFont);
      body.add(lbl, new GridBagConstraints(0, ++row, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 0, 4), 0, 0));

      edtName = new JTextField();
      body.add(edtName, new GridBagConstraints(0, ++row, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
      edtName.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyReleased(KeyEvent e)
         {
            btnOk.setEnabled(!edtName.getText().isEmpty());
         }
      });

      lbl = new JLabel(I18n.getMessage("ProjectProperties.Description"));
      lbl.setFont(captionFont);
      body.add(lbl, new GridBagConstraints(0, ++row, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));

      edtDescription = new JTextArea();
      edtDescription.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
      body.add(edtDescription, new GridBagConstraints(0, ++row, 3, 1, 1, 100, GridBagConstraints.WEST, GridBagConstraints.BOTH, defaultInsets, 0, 0));

      btnOk = new JButton(I18n.getMessage("Button.Ok"));
      btnOk.setEnabled(false);
      addButton(btnOk, Dialog.ACCEPT);

      addButton(new JButton(I18n.getMessage("Button.Cancel")), Dialog.REJECT);
   }

   /**
    * @return The project that is displayed/edited.
    */
   public Project getProject()
   {
      return project;
   }

   /**
    * Set the project that is displayed/edited.
    */
   public void setProject(Project project)
   {
      this.project = project;

      edtName.setText(project.getName());
      edtDescription.setText(project.getDescription());

      btnOk.setEnabled(!edtName.getText().isEmpty());
   }

   /**
    * Apply the changes to the project.
    */
   @Override
   public void accept()
   {
      if (project == null) return;

      project.setName(edtName.getText());
      project.setDescription(edtDescription.getText());
   }
}
