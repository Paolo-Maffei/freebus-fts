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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.workbench.WorkBenchEditor;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectListener;

/**
 * An editor page for an {@link MainGroup mainGroup}.
 */
public final class MainGroupDetails extends WorkBenchEditor
{
   private static final long serialVersionUID = -3099122201675912206L;

   private final JComboBox addrCombo = new JComboBox();
   private final JTextField nameEdit = new JTextField();

   private boolean updating;
   private MainGroup mainGroup;

   /**
    * Create an mainGroup details page.
    */
   public MainGroupDetails()
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
      // MainGroup name
      //
      lbl = new JLabel(I18n.getMessage("MainGroupDetails.Name") + ": ");
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

            mainGroup.setName(nameEdit.getText());
            fireModified(mainGroup);
         }

         @Override
         public void focusGained(FocusEvent e)
         {
            prevValue = nameEdit.getText();
         }
      });

      //
      // MainGroup address
      //
      lbl = new JLabel(I18n.getMessage("MainGroupDetails.Address") + ": ");
      add(lbl, new GridBagConstraints(0, ++gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));

      add(addrCombo, new GridBagConstraints(1, gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
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
            if (newAddr == mainGroup.getAddress())
               return;

            mainGroup.setAddress(newAddr);
            fireModified(mainGroup);
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
         if (obj == mainGroup)
            close();
      }
 
      @Override
      public void projectComponentModified(Object obj)
      {
         if (!updating)
         {
            if (obj == mainGroup)
               updateContents();
            else updateMainGroupAddresses();
         }
      }

      @Override
      public void projectComponentAdded(Object obj)
      {
         if (!updating)
            updateMainGroupAddresses();
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
      this.mainGroup = (MainGroup) o;
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

      final String title = Integer.toString(mainGroup.getAddress());
      setName(title);

      nameEdit.setText(mainGroup.getName());

      updateMainGroupAddresses();

      updating = false;
   }

   /**
    * Update the combo-box with the available line addresses.
    */
   private void updateMainGroupAddresses()
   {
      updating = true;

      addrCombo.removeAllItems();

      final Set<Integer> usedAddrs = new HashSet<Integer>(21);
      for (final MainGroup a: mainGroup.getProject().getMainGroups())
      {
         if (a != mainGroup)
            usedAddrs.add(a.getAddress());
      }

      final int lineAddr = mainGroup.getAddress();
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
