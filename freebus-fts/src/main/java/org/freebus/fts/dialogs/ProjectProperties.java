package org.freebus.fts.dialogs;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.freebus.fts.I18n;
import org.freebus.fts.elements.components.Dialog;
import org.freebus.fts.project.Project;

/**
 * Show/edit the properties of a project.
 */
public class ProjectProperties extends Dialog
{
   private static final long serialVersionUID = -2181288305995816556L;

   private final JTextField edtName = new JTextField();
   private final JTextArea edtDescription = new JTextArea();
   private final JButton btnOk = new JButton(I18n.getMessage("Button.Ok"));
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

      edtName.setName("edtName");
      body.add(edtName, new GridBagConstraints(0, ++row, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
//      edtName.addKeyListener(new KeyAdapter()
//      {
//         @Override
//         public void keyReleased(KeyEvent e)
//         {
//            btnOk.setEnabled(!edtName.getText().isEmpty());
//         }
//      });
      edtName.getDocument().addDocumentListener(new DocumentListener()
      {
         @Override
         public void removeUpdate(DocumentEvent e)
         {
            btnOk.setEnabled(e.getDocument().getLength() > 0);
         }
         
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            btnOk.setEnabled(e.getDocument().getLength() > 0);
         }
         
         @Override
         public void changedUpdate(DocumentEvent e)
         {
            btnOk.setEnabled(e.getDocument().getLength() > 0);
         }
      });

      lbl = new JLabel(I18n.getMessage("ProjectProperties.Description"));
      lbl.setFont(captionFont);
      body.add(lbl, new GridBagConstraints(0, ++row, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));

      edtDescription.setName("edtDescription");
      edtDescription.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
      body.add(edtDescription, new GridBagConstraints(0, ++row, 3, 1, 1, 100, GridBagConstraints.WEST, GridBagConstraints.BOTH, defaultInsets, 0, 0));

      btnOk.setName("btnOk");
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
   public void setProject(Project proj)
   {
      project = proj;

      edtName.setText(project.getName());
      edtDescription.setText(project.getDescription());
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
