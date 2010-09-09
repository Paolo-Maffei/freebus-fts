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
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.core.I18n;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.Room;

/**
 * Show / edit the general settings of a device.
 */
public class GeneralPanel extends JPanel implements DeviceEditorComponent
{
   private static final long serialVersionUID = -7668596031416201110L;

   private boolean updating = false;
   private Device device;

   private final JLabel lblLineAddr = new JLabel();
   private final JComboBox cboAddr = new JComboBox();
   private final JComboBox cboRoom = new JComboBox();
   private final JTextField edtName = new JTextField();
   private final JTextArea edtNotes = new JTextArea();

   /*
    * Internal class for the rooms combobox
    */
   static private class RoomItem
   {
      public final Room room;

      public RoomItem(Room room)
      {
         this.room = room;
      }

      @Override
      public String toString()
      {
         if (room == null)
            return I18n.getMessage("AddDeviceDialog.NoRoom");
         return room.getBuilding().getName() + " - " + room.getName();
      }
   }

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
            fireModified(device);
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
            fireModified(device);
         }
      });

      add(new Label(), new GridBagConstraints(3, gridy, 1, 1, 100, 1, w, GridBagConstraints.BOTH, stdInsets, 0, 0));

      //
      // Room
      //
      lbl = new JLabel(I18n.getMessage("DeviceEditor.GeneralPanel.Room") + ": ");
      add(lbl, new GridBagConstraints(0, ++gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));

      add(cboRoom, new GridBagConstraints(1, gridy, 3, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      cboRoom.setMaximumRowCount(20);
      cboRoom.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            final Object sel = cboRoom.getSelectedItem();
            if (!(sel instanceof RoomItem))
               return;

            Room room = ((RoomItem) sel).room;
            if (room == null)
            {
               room = device.getRoom();
               if (room != null)
                  room.remove(device);
            }
            else if (device.getRoom() != room)
            {
               room.add(device);
            }

            fireModified(room);
         }
      });

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
    * Set the edited device.
    */
   public void setDevice(Device device)
   {
      this.device = device;

      if (device == null)
         return;

      updating = true;
      edtName.setText(device.getName());
      
      final PhysicalAddress addr = device.getPhysicalAddress();
      lblLineAddr.setText(Integer.toString(addr.getZone()) + '.' + Integer.toString(addr.getLine()) + '.');

      updateDeviceAddresses();
      updateRooms();

      updating = false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void componentChanged(Object obj)
   {
      updating = true;

      if (obj instanceof Device)
         updateDeviceAddresses();
      else updateRooms();

      updating = false;
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

   /**
    * Update the combobox with the available rooms.
    */
   private void updateRooms()
   {
      Object selected = cboRoom.getSelectedItem();
      if (selected != null)
         selected = ((RoomItem) selected).room;
      else if (device != null)
         selected = device.getRoom();

      cboRoom.removeAllItems();
      cboRoom.addItem(new RoomItem(null));

      final Set<Room> rooms = new TreeSet<Room>();
      for (final Building building : ProjectManager.getProject().getBuildings())
         rooms.addAll(building.getRooms());

      for (final Room room : rooms)
      {
         cboRoom.addItem(new RoomItem(room));

         if (room == selected)
            cboRoom.setSelectedIndex(cboRoom.getItemCount() - 1);
      }
   }
}
