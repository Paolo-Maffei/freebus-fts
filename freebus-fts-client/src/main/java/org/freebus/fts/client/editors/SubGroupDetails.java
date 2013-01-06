package org.freebus.fts.client.editors;

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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.workbench.WorkBenchEditor;
import org.freebus.fts.project.MidGroup;
import org.freebus.fts.project.SubGroup;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectListener;

/**
 * An editor page for a {@link SubGroup subGroup}.
 */
public final class SubGroupDetails extends WorkBenchEditor
{
   private static final long serialVersionUID = 4496164130912865790L;

   private final JLabel midGroupAddrLabel = new JLabel();
   private final JComboBox addrCombo = new JComboBox();
   private final JTextField nameEdit = new JTextField();

   private boolean updating;
   private SubGroup subGroup;

   /**
    * Create a subGroup page.
    */
   public SubGroupDetails()
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
      // SubGroup name
      //
      lbl = new JLabel(I18n.getMessage("SubGroupDetails.Name") + ": ");
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

            subGroup.setName(nameEdit.getText());
            fireModified(subGroup);
         }

         @Override
         public void focusGained(FocusEvent e)
         {
            prevValue = nameEdit.getText();
         }
      });

      //
      // SubGroup address
      //
      lbl = new JLabel(I18n.getMessage("SubGroupDetails.Address") + ": ");
      add(lbl, new GridBagConstraints(0, ++gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      add(midGroupAddrLabel, new GridBagConstraints(1, gridy, 1, 1, 1, 1, e, GridBagConstraints.NONE, stdInsets, 0, 0));

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
            if (newAddr == subGroup.getAddress())
               return;

            subGroup.setAddress(newAddr);
            fireModified(subGroup);
         }
      });

      add(new Label(), new GridBagConstraints(3, gridy, 1, 1, 100, 1, w, GridBagConstraints.BOTH, stdInsets, 0, 0));

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
         if (obj == subGroup)
            close();
      }
 
      @Override
      public void projectComponentModified(Object obj)
      {
         if (!updating)
         {
            if (obj == subGroup || subGroup.getMidGroup() == obj)
               updateContents();
            else updateSubGroupAddresses();
         }
      }

      @Override
      public void projectComponentAdded(Object obj)
      {
         if (!updating)
            updateSubGroupAddresses();
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
    * Set the subGroup to show / edit.
    */
   @Override
   public void objectChanged(Object o)
   {
      this.subGroup = (SubGroup) o;
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

      final String title = Integer.toString(subGroup.getMidGroup().getAddress()) + '.' + Integer.toString(subGroup.getAddress());
      setName(title);

      midGroupAddrLabel.setText(Integer.toString(subGroup.getMidGroup().getAddress()) + '.');
      nameEdit.setText(subGroup.getName());

      updateSubGroupAddresses();

      updating = false;
   }

   /**
    * Update the combobox with the available subGroup addresses.
    */
   private void updateSubGroupAddresses()
   {
      updating = true;

      addrCombo.removeAllItems();

      final MidGroup midGroup = subGroup.getMidGroup();
      if (midGroup == null)
         return;

      final Set<Integer> usedAddrs = new HashSet<Integer>(21);
      for (final SubGroup l: midGroup.getSubGroups())
      {
         if (l != subGroup)
            usedAddrs.add(l.getAddress());
      }

      final int subGroupAddr = subGroup.getAddress();
      for (int addr = 0; addr <= 255; ++addr)
      {
         if (usedAddrs.contains(addr))
            continue;

         addrCombo.addItem(" " + Integer.toString(addr) + " ");
         if (addr == subGroupAddr)
            addrCombo.setSelectedIndex(addrCombo.getItemCount() - 1);
      }
      
      if (addrCombo.getSelectedIndex() == -1)
         addrCombo.setSelectedIndex(0);

      updating = false;
   }
}
