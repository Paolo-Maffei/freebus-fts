package org.freebus.fts.pages.deviceeditor;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.core.I18n;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;

/**
 * Show / edit the general settings of a device.
 */
public class GeneralPanel extends JPanel implements DeviceEditorPart
{
   private static final long serialVersionUID = -7668596031416201110L;
   private Device device;

   private final JLabel lblLineAddr = new JLabel();
   private final JComboBox cboAddr = new JComboBox();
   private final JTextField edtName = new JTextField();
   private final JTextArea edtNotes = new JTextArea();

   /**
    * Create a general device settings panel.
    */
   public GeneralPanel()
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
      // Device name
      //
      lbl = new JLabel(I18n.getMessage("DeviceEditor.GeneralPanel.Name") + ": ");
      add(lbl, new GridBagConstraints(0, ++gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      add(edtName, new GridBagConstraints(1, gridy, 3, 1, 1, 1, w, GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));
      edtName.addFocusListener(new FocusListener()
      {
         private String prevValue;

         @Override
         public void focusLost(FocusEvent e)
         {
            if (edtName.getText().equals(prevValue))
               return;

            device.setName(edtName.getText());
            fireModified();
         }

         @Override
         public void focusGained(FocusEvent e)
         {
            prevValue = edtName.getText();
         }
      });

      //
      // Physical address
      //
      lbl = new JLabel(I18n.getMessage("DeviceEditor.GeneralPanel.PhysicalAddress") + ": ");
      add(lbl, new GridBagConstraints(0, ++gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      add(lblLineAddr, new GridBagConstraints(1, gridy, 1, 1, 1, 1, e, GridBagConstraints.NONE, stdInsets, 0, 0));

      add(cboAddr, new GridBagConstraints(2, gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      cboAddr.setMaximumRowCount(20);
      cboAddr.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            final Object sel = cboAddr.getSelectedItem();
            if (sel == null)
               return;

            final int newNodeAddr = Integer.parseInt(sel.toString().trim());
            if (newNodeAddr == device.getAddress())
               return;

            device.setAddress(newNodeAddr);
            fireModified();
         }
      });

      add(new Label(), new GridBagConstraints(3, gridy, 1, 1, 100, 1, w, GridBagConstraints.BOTH, stdInsets, 0, 0));

      //
      // Notes
      //
      lbl = new JLabel(I18n.getMessage("DeviceEditor.GeneralPanel.Notes"));
      add(lbl, new GridBagConstraints(0, ++gridy, 3, 1, 1, 1, w, GridBagConstraints.NONE, blkInsets, 0, 0));

      final JScrollPane scpNotes = new JScrollPane(edtNotes);
      add(scpNotes, new GridBagConstraints(0, ++gridy, gridWidth, 1, 1, 1, nw, GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));
      scpNotes.setPreferredSize(new Dimension(100, 100));

      //
      // Page filler
      //
      add(new Label(), new GridBagConstraints(0, ++gridy, 1, 1, 1, 100, w, GridBagConstraints.BOTH, stdInsets, 0, 0));
   }

   /**
    * The device was modified
    */
   protected void fireModified()
   {
   }

   /**
    * Set the edited device.
    */
   public void setDevice(Device device)
   {
      this.device = device;

      if (device == null)
         return;

      edtName.setText(device.getName());
      
      final PhysicalAddress addr = device.getPhysicalAddress();
      lblLineAddr.setText(Integer.toString(addr.getZone()) + '.' + Integer.toString(addr.getLine()) + '.');
      updateDeviceAddresses();
   }

   /**
    * Update the combobox with the available device addresses.
    */
   private void updateDeviceAddresses()
   {
      cboAddr.removeAllItems();

      final Line line = device.getLine();
      if (line == null)
         return;

      final Set<Integer> usedAddrs = new HashSet<Integer>(257);
      for (final Device dev: line.getDevices())
      {
         if (dev != device)
            usedAddrs.add(dev.getPhysicalAddress().getNode());
      }

      final int deviceAddr = device.getAddress();
      for (int addr = 0; addr <= 255; ++addr)
      {
         if (usedAddrs.contains(addr))
            continue;

         cboAddr.addItem(" " + Integer.toString(addr) + " ");
         if (addr == deviceAddr)
            cboAddr.setSelectedIndex(cboAddr.getItemCount() - 1);
      }
      
      if (cboAddr.getSelectedIndex() == -1)
         cboAddr.setSelectedIndex(0);
   }
}
