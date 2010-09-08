package org.freebus.fts.dialogs;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.freebus.fts.MainWindow;
import org.freebus.fts.components.Dialog;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.pages.PhysicalView;
import org.freebus.fts.pages.TopologyView;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.Room;

/**
 * Dialog for adding new devices to the project.
 * 
 * When the "Ok" button is clicked, the dialog adds the device to the project.
 */
public class AddDeviceDialog extends Dialog
{
   private static final long serialVersionUID = -3229208553550933238L;

   private final VirtualDevice virtualDevice;
   private final JButton btnOk = new JButton(I18n.getMessage("Button.Ok"));
   private final JTextField edtName = new JTextField();
   private final JComboBox cboLine = new JComboBox();
   private final JComboBox cboRoom = new JComboBox();

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
    * Create a dialog for adding new devices to the project.
    * 
    * @param virtualDevice - the virtual device to add.
    * @param owner - the owning window.
    */
   public AddDeviceDialog(VirtualDevice virtualDevice, Window owner)
   {
      super(owner, ModalityType.APPLICATION_MODAL);
      this.virtualDevice = virtualDevice;

      setTitle(I18n.getMessage("AddDeviceDialog.Title"));

      final Container body = getBodyPane();
      body.setLayout(new GridBagLayout());

      final Insets stdInsets = new Insets(2, 2, 2, 2);
      int gridy = -1;
      JLabel lbl;

      lbl = new JLabel(ImageCache.getIcon("icons-large/device"));
      body.add(lbl, new GridBagConstraints(0, ++gridy, 1, 2, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.VERTICAL, stdInsets, 0, 0));

      lbl = new JLabel(I18n.getMessage("AddDeviceDialog.Caption"));
      lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, lbl.getFont().getSize2D() * 1.2f));
      body.add(lbl, new GridBagConstraints(1, gridy, 3, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));

      lbl = new JLabel(virtualDevice.getName());
      body.add(lbl, new GridBagConstraints(1, ++gridy, 3, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));

      lbl = new JLabel(I18n.getMessage("AddDeviceDialog.Name"));
      body.add(lbl, new GridBagConstraints(0, ++gridy, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            stdInsets, 0, 0));
      body.add(edtName, new GridBagConstraints(2, gridy, 2, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));
      edtName.setText(virtualDevice.getCatalogEntry().getName());

      lbl = new JLabel(I18n.getMessage("AddDeviceDialog.AddToLine"));
      body.add(lbl, new GridBagConstraints(0, ++gridy, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            stdInsets, 0, 0));
      body.add(cboLine, new GridBagConstraints(2, gridy, 2, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));
      updateLines();

      lbl = new JLabel(I18n.getMessage("AddDeviceDialog.AddToRoom"));
      body.add(lbl, new GridBagConstraints(0, ++gridy, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            stdInsets, 0, 0));
      body.add(cboRoom, new GridBagConstraints(2, gridy, 2, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, stdInsets, 0, 0));
      updateRooms();

      addButton(btnOk, Dialog.ACCEPT);
      addButton(new JButton(I18n.getMessage("Button.Cancel")), Dialog.REJECT);

      setMinimumSize(getPreferredSize());
      setSize((int) (getPreferredSize().width * 1.25), (int) (getPreferredSize().height * 1.25));
   }

   /**
    * Try to find a suitable default line by looking at what is selected in the
    * topology view of the main window.
    * 
    * @return The selected line, or null if none.
    */
   private Line getDefaultLine()
   {
      final TopologyView topologyView = (TopologyView) MainWindow.getInstance().getPage(TopologyView.class, null);
      if (topologyView == null)
         return null;

      final Object obj = topologyView.getSelectedObject();
      if (obj instanceof Area)
      {
         final List<Line> lines = ((Area) obj).getLines();
         if (lines != null && !lines.isEmpty())
            return lines.get(0);
      }
      else if (obj instanceof Line)
      {
         return (Line) obj;
      }
      else if (obj instanceof Device)
      {
         return ((Device) obj).getLine();
      }

      return null;
   }

   /**
    * Try to find a suitable default room by looking at what is selected in the
    * physical view of the main window.
    * 
    * @return The selected line, or null if none.
    */
   private Room getDefaultRoom()
   {
      final PhysicalView physicalView = (PhysicalView) MainWindow.getInstance().getPage(PhysicalView.class, null);
      if (physicalView == null)
         return null;

      final Object obj = physicalView.getSelectedObject();
      if (obj instanceof Building)
      {
         final List<Room> rooms = ((Building) obj).getRooms();
         if (rooms != null && !rooms.isEmpty())
            return rooms.get(0);
      }
      else if (obj instanceof Room)
      {
         return (Room) obj;
      }
      else if (obj instanceof Device)
      {
         return ((Device) obj).getRoom();
      }

      return null;
   }

   /**
    * Add all {@link Line lines} of the project to the line-address combobox
    */
   private void updateLines()
   {
      Object selected = cboLine.getSelectedItem();
      if (selected == null)
         selected = getDefaultLine();

      cboLine.removeAllItems();

      final Set<Line> lines = new TreeSet<Line>();
      for (final Area area : ProjectManager.getProject().getAreas())
         lines.addAll(area.getLines());

      for (final Line line : lines)
      {
         cboLine.addItem(line);

         if (line == selected)
            cboLine.setSelectedIndex(cboLine.getItemCount() - 1);
      }
   }

   /**
    * Add all {@link Room rooms} of the project to the rooms combobox
    */
   private void updateRooms()
   {
      Object selected = cboRoom.getSelectedItem();
      if (selected == null)
         selected = getDefaultRoom();

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

   /**
    * @return The virtual device that the dialog displays.
    */
   public VirtualDevice getVirtualDevice()
   {
      return virtualDevice;
   }

   /**
    * Accept the dialog: add a new device with the chosen configuration.
    */
   public void accept()
   {
      final Device device = new Device(virtualDevice);

      final String name = edtName.getText().trim();
      if (!name.isEmpty())
         device.setName(name);

      final RoomItem roomItem = (RoomItem) cboRoom.getSelectedItem();
      if (roomItem.room != null)
         roomItem.room.add(device);

      final Line line = (Line) cboLine.getSelectedItem();
      device.setAddress(line.getFreeAddress());
      line.add(device);

      ProjectManager.fireComponentAdded(device);
   }
}
