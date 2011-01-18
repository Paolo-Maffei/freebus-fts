package org.freebus.fts.client.pages.devicedetails;

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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.freebus.fts.client.components.CatalogEntryDetails;
import org.freebus.fts.client.components.DeviceProgrammingPanel;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.Room;
import org.freebus.fts.service.devicecontroller.DeviceController;

/**
 * Show / edit the general settings of a device.
 */
public class GeneralPanel extends JPanel implements DeviceEditorComponent
{
   private static final long serialVersionUID = -7668596031416201110L;

   private boolean updating = false;
   private Device device;

   private final JLabel lineAddrLabel = new JLabel();
   private final JComboBox addrCombo = new JComboBox();
   private final JComboBox roomCombo = new JComboBox();
   private final JTextField nameEdit = new JTextField();
   private final JTextArea notesEdit = new JTextArea();
   private final DeviceProgrammingPanel programmingPanel = new DeviceProgrammingPanel();
   private final CatalogEntryDetails catEntryDetails = new CatalogEntryDetails();

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
         return room.getPathName();
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
      final int ne = GridBagConstraints.NORTHEAST;
      final int nw = GridBagConstraints.NORTHWEST;
      final int e = GridBagConstraints.EAST;
      final int w = GridBagConstraints.WEST;
      final int gridWidth = 5;
      int gridy = -1;
      JLabel lbl;

      //
      // Device name
      //
      lbl = new JLabel(I18n.getMessage("DeviceEditor.GeneralPanel.Name") + ": ");
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

            device.setName(nameEdit.getText());
            fireModified(device);
         }

         @Override
         public void focusGained(FocusEvent e)
         {
            prevValue = nameEdit.getText();
         }
      });

      //
      // Physical address
      //
      lbl = new JLabel(I18n.getMessage("DeviceEditor.GeneralPanel.PhysicalAddress") + ": ");
      add(lbl, new GridBagConstraints(0, ++gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      add(lineAddrLabel, new GridBagConstraints(1, gridy, 1, 1, 1, 1, e, GridBagConstraints.NONE, stdInsets, 0, 0));

      add(addrCombo, new GridBagConstraints(2, gridy, 1, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      addrCombo.setMaximumRowCount(20);
      addrCombo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            final Object sel = addrCombo.getSelectedItem();
            if (updating || sel == null)
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

      add(roomCombo, new GridBagConstraints(1, gridy, 3, 1, 1, 1, w, GridBagConstraints.NONE, stdInsets, 0, 0));
      roomCombo.setMaximumRowCount(20);
      roomCombo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            final Object sel = roomCombo.getSelectedItem();
            if (updating || !(sel instanceof RoomItem))
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

      final JScrollPane notesScrollPane = new JScrollPane(notesEdit);
      notesScrollPane.setPreferredSize(new Dimension(100, 100));
      add(notesScrollPane, new GridBagConstraints(0, ++gridy, gridWidth - 1, 1, 1, 1, nw, GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));

      notesEdit.addFocusListener(new FocusListener()
      {
         private String prevValue;

         @Override
         public void focusLost(FocusEvent e)
         {
            if (notesEdit.getText().equals(prevValue))
               return;

            device.setDescription(notesEdit.getText());
         }

         @Override
         public void focusGained(FocusEvent e)
         {
            prevValue = notesEdit.getText();
         }
      });

      //
      // Programming details
      //
      programmingPanel.setBorder(BorderFactory.createTitledBorder(I18n.getMessage("DeviceEditor.GeneralPanel.ProgrammingDetails")));
      add(programmingPanel, new GridBagConstraints(4, 0, 1, gridy + 1, 1, 1, ne, GridBagConstraints.VERTICAL, new Insets(0, 8, 0, 0), 0, 0));

      //
      // Catalog entry details
      //
      add(catEntryDetails, new GridBagConstraints(0, ++gridy, gridWidth, 1, 1, 1, w, GridBagConstraints.BOTH, blkInsets, 0, 0));

      //
      // Page filler
      //
      add(new Label(), new GridBagConstraints(0, ++gridy, gridWidth, 1, 1, 100, w, GridBagConstraints.BOTH, stdInsets, 0, 0));
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
   @Override
   public void setDevice(Device device, DeviceController adapter)
   {
      this.device = device;

      if (device == null)
         return;

      updating = true;
      nameEdit.setText(device.getName());
      notesEdit.setText(device.getDescription());
      catEntryDetails.setCatalogEntry(device.getCatalogEntry());
      programmingPanel.setDevice(device);

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

      if (obj instanceof Device || obj instanceof Line || obj instanceof Area)
      {
         updateDeviceAddresses();
         programmingPanel.updateContents();
      }
      else updateRooms();

      updating = false;
   }

   /**
    * Update the combo-box with the available device addresses.
    */
   private void updateDeviceAddresses()
   {
      addrCombo.removeAllItems();

      final Line line = device.getLine();
      if (line == null)
         return;

      final PhysicalAddress physicalAddr = device.getPhysicalAddress();
      lineAddrLabel.setText(Integer.toString(physicalAddr.getZone()) + '.' + Integer.toString(physicalAddr.getLine()) + '.');

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

         addrCombo.addItem(" " + Integer.toString(addr) + " ");
         if (addr == deviceAddr)
            addrCombo.setSelectedIndex(addrCombo.getItemCount() - 1);
      }

      if (addrCombo.getSelectedIndex() == -1)
         addrCombo.setSelectedIndex(0);
   }

   /**
    * Update the combo-box with the available rooms.
    */
   private void updateRooms()
   {
      Object selected = device == null ? null : device.getRoom();

      roomCombo.removeAllItems();
      roomCombo.addItem(new RoomItem(null));

      final Map<String,Room> rooms = new TreeMap<String,Room>();
      for (final Building building : ProjectManager.getProject().getBuildings())
      {
         for (final Room room : building.getRooms())
            rooms.put(room.getPathName(), room);
      }

      for (final Room room : rooms.values())
      {
         roomCombo.addItem(new RoomItem(room));

         if (room == selected)
            roomCombo.setSelectedIndex(roomCombo.getItemCount() - 1);
      }
   }
}
