package org.freebus.fts.pages;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.freebus.fts.client.core.I18n;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectListener;

/**
 * An editor page for a {@link Line line}. 
 */
public final class LineDetails extends AbstractPage
{
   private static final long serialVersionUID = 4496164130912865790L;

   private final JLabel areaAddrLabel = new JLabel();
   private final JComboBox addrCombo = new JComboBox();
   private final JTextField nameEdit = new JTextField();
   private final JTextArea notesEdit = new JTextArea();

   private boolean updating;
   private Line line;

   /**
    * Create a line page.
    */
   public LineDetails()
   {
      setLayout(new GridBagLayout());
      setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 32));

      final Insets blkInsets = new Insets(16, 0, 0, 0);
      final Insets stdInsets = new Insets(0, 0, 0, 0);
      final int nw = GridBagConstraints.NORTHWEST;
      final int e = GridBagConstraints.EAST;
      final int w = GridBagConstraints.WEST;
      final int gridWidth = 4;
      int gridy = -1;
      JLabel lbl;

      //
      // Line name
      //
      lbl = new JLabel(I18n.getMessage("LineDetails.Name") + ": ");
      add(lbl, new GridBagConstraints(0, ++gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      add(nameEdit, new GridBagConstraints(1, gridy, 3, 1, 1, 1, w, GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));
      nameEdit.addFocusListener(new FocusListener()
      {
         private String prevValue;

         @Override
         public void focusLost(FocusEvent e)
         {
            if (nameEdit.getText().equals(prevValue))
               return;

            line.setName(nameEdit.getText());
            fireModified(line);
         }

         @Override
         public void focusGained(FocusEvent e)
         {
            prevValue = nameEdit.getText();
         }
      });

      //
      // Line address
      //
      lbl = new JLabel(I18n.getMessage("LineDetails.Address") + ": ");
      add(lbl, new GridBagConstraints(0, ++gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      add(areaAddrLabel, new GridBagConstraints(1, gridy, 1, 1, 1, 1, e, GridBagConstraints.NONE, stdInsets, 0, 0));

      add(addrCombo, new GridBagConstraints(2, gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      addrCombo.setMaximumRowCount(20);
      addrCombo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            final Object sel = addrCombo.getSelectedItem();
            if (sel == null)
               return;

            final int newAddr = Integer.parseInt(sel.toString().trim());
            if (newAddr == line.getAddress())
               return;

            line.setAddress(newAddr);
            fireModified(line);
         }
      });

      add(new Label(), new GridBagConstraints(3, gridy, 1, 1, 100, 1, w, GridBagConstraints.BOTH, stdInsets, 0, 0));

      //
      // Notes
      //
      lbl = new JLabel(I18n.getMessage("DeviceEditor.GeneralPanel.Notes"));
      add(lbl, new GridBagConstraints(0, ++gridy, 3, 1, 1, 1, w, GridBagConstraints.NONE, blkInsets, 0, 0));

      final JScrollPane scpNotes = new JScrollPane(notesEdit);
      add(scpNotes, new GridBagConstraints(0, ++gridy, gridWidth, 1, 1, 1, nw, GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));
      scpNotes.setPreferredSize(new Dimension(100, 100));
      notesEdit.addFocusListener(new FocusListener()
      {
         private String prevValue;

         @Override
         public void focusLost(FocusEvent e)
         {
            if (notesEdit.getText().equals(prevValue))
               return;

            line.setDescription(notesEdit.getText());
         }

         @Override
         public void focusGained(FocusEvent e)
         {
            prevValue = notesEdit.getText();
         }
      });

      //
      // Page filler
      //
      add(new Label(), new GridBagConstraints(0, ++gridy, 1, 1, 1, 100, w, GridBagConstraints.BOTH, stdInsets, 0, 0));

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
         if (obj == line)
            close();
      }
      
      @Override
      public void projectComponentModified(Object obj)
      {
         if (!updating)
         {
            if (obj == line || line.getArea() == obj)
               updateContents();
            else updateLineAddresses();
         }
      }

      @Override
      public void projectComponentAdded(Object obj)
      {
         if (!updating)
            updateLineAddresses();
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
   public void setObject(Object o)
   {
      this.line = (Line) o;
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

      final String title = Integer.toString(line.getArea().getAddress()) + '.' + Integer.toString(line.getAddress());
      setName(title);

      areaAddrLabel.setText(Integer.toString(line.getArea().getAddress()) + '.');
      nameEdit.setText(line.getName());
      notesEdit.setText(line.getDescription());

      updateLineAddresses();

      updating = false;
   }

   /**
    * Update the combobox with the available line addresses.
    */
   private void updateLineAddresses()
   {
      updating = true;

      addrCombo.removeAllItems();

      final Area area = line.getArea();
      if (area == null)
         return;

      final Set<Integer> usedAddrs = new HashSet<Integer>(21);
      for (final Line l: area.getLines())
      {
         if (l != line)
            usedAddrs.add(l.getAddress());
      }

      final int lineAddr = line.getAddress();
      for (int addr = 0; addr <= 15; ++addr)
      {
         if (usedAddrs.contains(addr))
            continue;

         addrCombo.addItem(" " + Integer.toString(addr) + " ");
         if (addr == lineAddr)
            addrCombo.setSelectedIndex(addrCombo.getItemCount() - 1);
      }
      
      if (addrCombo.getSelectedIndex() == -1)
         addrCombo.setSelectedIndex(0);

      updating = false;
   }
}
