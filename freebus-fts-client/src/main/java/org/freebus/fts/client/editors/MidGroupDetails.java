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
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectListener;

/**
 * An editor page for a {@link MidGroup midGroup}.
 */
public final class MidGroupDetails extends WorkBenchEditor
{
   private static final long serialVersionUID = 4496164130912865790L;

   private final JLabel mainGroupAddrLabel = new JLabel();
   private final JComboBox addrCombo = new JComboBox();
   private final JTextField nameEdit = new JTextField();

   private boolean updating;
   private MidGroup midGroup;

   /**
    * Create a midGroup page.
    */
   public MidGroupDetails()
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
      // MidGroup name
      //
      lbl = new JLabel(I18n.getMessage("MidGroupDetails.Name") + ": ");
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

            midGroup.setName(nameEdit.getText());
            fireModified(midGroup);
         }

         @Override
         public void focusGained(FocusEvent e)
         {
            prevValue = nameEdit.getText();
         }
      });

      //
      // MidGroup address
      //
      lbl = new JLabel(I18n.getMessage("MidGroupDetails.Address") + ": ");
      add(lbl, new GridBagConstraints(0, ++gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      add(mainGroupAddrLabel, new GridBagConstraints(1, gridy, 1, 1, 1, 1, e, GridBagConstraints.NONE, stdInsets, 0, 0));

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
            if (newAddr == midGroup.getAddress())
               return;

            midGroup.setAddress(newAddr);
            fireModified(midGroup);
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
         if (obj == midGroup)
            close();
      }
 
      @Override
      public void projectComponentModified(Object obj)
      {
         if (!updating)
         {
            if (obj == midGroup || midGroup.getMainGroup() == obj)
               updateContents();
            else updateMidGroupAddresses();
         }
      }

      @Override
      public void projectComponentAdded(Object obj)
      {
         if (!updating)
            updateMidGroupAddresses();
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
    * Set the midGroup to show / edit.
    */
   @Override
   public void objectChanged(Object o)
   {
      this.midGroup = (MidGroup) o;
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

      final String title = Integer.toString(midGroup.getMainGroup().getAddress()) + '.' + Integer.toString(midGroup.getAddress());
      setName(title);

      mainGroupAddrLabel.setText(Integer.toString(midGroup.getMainGroup().getAddress()) + '.');
      nameEdit.setText(midGroup.getName());

      updateMidGroupAddresses();

      updating = false;
   }

   /**
    * Update the combobox with the available midGroup addresses.
    */
   private void updateMidGroupAddresses()
   {
      updating = true;

      addrCombo.removeAllItems();

      final MainGroup mainGroup = midGroup.getMainGroup();
      if (mainGroup == null)
         return;

      final Set<Integer> usedAddrs = new HashSet<Integer>(21);
      for (final MidGroup l: mainGroup.getMidGroups())
      {
         if (l != midGroup)
            usedAddrs.add(l.getAddress());
      }

      final int midGroupAddr = midGroup.getAddress();
      for (int addr = 0; addr <= 7; ++addr)
      {
         if (usedAddrs.contains(addr))
            continue;

         addrCombo.addItem(" " + Integer.toString(addr) + " ");
         if (addr == midGroupAddr)
            addrCombo.setSelectedIndex(addrCombo.getItemCount() - 1);
      }
      
      if (addrCombo.getSelectedIndex() == -1)
         addrCombo.setSelectedIndex(0);

      updating = false;
   }
}
