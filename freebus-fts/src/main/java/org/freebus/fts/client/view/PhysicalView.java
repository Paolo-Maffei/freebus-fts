package org.freebus.fts.client.view;

import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

import org.freebus.fts.client.actions.Actions;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.dragdrop.ObjectTransferHandler;
import org.freebus.fts.elements.components.ToolBar;
import org.freebus.fts.elements.components.ToolBarButton;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.elements.tree.MutableIconTreeNode;
import org.freebus.fts.elements.utils.TreeUtils;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.Room;
import org.freebus.fts.project.RoomType;

/**
 * A page that shows the physical components of the project (buildings, floors,
 * rooms).
 */
public class PhysicalView extends AbstractTreeView
{
   private static final long serialVersionUID = 8538623742260660512L;

   private final Icon buildingIcon = ImageCache.getIcon("icons/building");
   private final Icon buildingPartIcon = ImageCache.getIcon("icons/floor");
   private final Icon roomIcon = ImageCache.getIcon("icons/room");
   private final Icon cabinetIcon = ImageCache.getIcon("icons/cabinet");
   private final Icon deviceIcon = ImageCache.getIcon("icons/device");
   private final Icon unassignedDevicesStoreIcon = ImageCache.getIcon("icons/idea");

   private JButton btnAddBuilding, btnAddBuildingPart, btnAddCabinet, btnAddRoom, btnAddDevice;
   private JButton btnEdit, btnDelete;

   /**
    * Internal class for marking the tree node that contains devices that are
    * not assigned to a room.
    */
   static class UnassignedDevicesStore
   {
      @Override
      public String toString()
      {
         return I18n.getMessage("PhysicalView.UnassignedDevices");
      }
   }

   /**
    * Create a page that shows the topological structure of the project.
    */
   public PhysicalView()
   {
      setName(I18n.getMessage("PhysicalView.Title"));
      initToolBar();

      enableTreeDragDrop(true, DnDConstants.ACTION_MOVE);
      getTree().setTransferHandler(new ObjectTransferHandler(TransferHandler.MOVE)
      {
         private static final long serialVersionUID = -7656708845168119823L;

         @Override
         public boolean isDragable(Object obj)
         {
            if (obj instanceof Building)
               return ((Building) obj).getParent() != null;

            return true;
         }
      });
   }

   /**
    * Enable/disable all toolbar buttons.
    * 
    * @param - true to enable the buttons, false to disable them.
    */
   private void setButtonsEnabled(boolean b)
   {
      btnAddBuildingPart.setEnabled(b);
      btnAddRoom.setEnabled(b);
      btnAddCabinet.setEnabled(b);
      btnAddDevice.setEnabled(b);
      btnEdit.setEnabled(b);
      btnDelete.setEnabled(b);
   }

   /**
    * Initialize the tool-bar.
    */
   private void initToolBar()
   {
      final JToolBar toolBar = new ToolBar();
      add(toolBar, BorderLayout.NORTH);

      btnAddBuilding = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            addBuilding();
         }
      });
      btnAddBuilding.setIcon(ImageCache.getIcon("icons/building", "icons/new-overlay"));
      btnAddBuilding.setToolTipText(I18n.getMessage("PhysicalView.AddBuildingTip"));

      btnAddBuildingPart = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            addBuildingPart();
         }
      });
      btnAddBuildingPart.setEnabled(false);
      btnAddBuildingPart.setIcon(ImageCache.getIcon("icons/floor", "icons/new-overlay2"));
      btnAddBuildingPart.setToolTipText(I18n.getMessage("PhysicalView.AddBuildingPartTip"));

      btnAddRoom = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            addRoom(RoomType.ROOM);
         }
      });
      btnAddRoom.setEnabled(false);
      btnAddRoom.setIcon(ImageCache.getIcon("icons/room-new"));
      btnAddRoom.setToolTipText(I18n.getMessage("PhysicalView.AddRoomTip"));

      btnAddCabinet = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            addRoom(RoomType.CABINET);
         }
      });
      btnAddCabinet.setEnabled(false);
      btnAddCabinet.setIcon(ImageCache.getIcon("icons/cabinet", "icons/new-overlay2"));
      btnAddCabinet.setToolTipText(I18n.getMessage("PhysicalView.AddCabinetTip"));

      btnAddDevice = toolBar.add(Actions.ADD_DEVICES.action);
      btnAddDevice.setEnabled(false);
      btnAddDevice.setIcon(ImageCache.getIcon("icons/device-new"));
      btnAddDevice.setToolTipText(I18n.getMessage("PhysicalView.AddDeviceTip"));

      btnEdit = new ToolBarButton(ImageCache.getIcon("icons/configure"));
      toolBar.add(btnEdit);
      btnEdit.setEnabled(false);
      btnEdit.setToolTipText(I18n.getMessage("PhysicalView.EditItemTip"));
      btnEdit.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent arg0)
         {
            ProjectManager.getController().edit(getSelectedObject());
         }
      });

      btnDelete = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent arg0)
         {
            ProjectManager.getController().remove(getSelectedObject());
         }
      });
      toolBar.add(btnDelete);
      btnDelete.setEnabled(false);
      btnDelete.setIcon(ImageCache.getIcon("icons/delete"));
      btnDelete.setToolTipText(I18n.getMessage("PhysicalView.DeleteItemTip"));
   }

   /**
    * Add a building
    */
   public void addBuilding()
   {
      final Set<Building> buildings = ProjectManager.getProject().getBuildings();
      final Building building = new Building();

      for (int i = 1; i < 1000; ++i)
      {
         building.setName(I18n.formatMessage("NewBuilding", Integer.toString(i)));
         if (!buildings.contains(building))
            break;
      }

      ProjectManager.getController().add(building);
   }

   /**
    * Add a building part / floor
    */
   public void addBuildingPart()
   {
      Object obj = getSelectedObject();

      if (obj instanceof Device)
         obj = ((Device) obj).getRoom();
      if (obj instanceof Room)
         obj = ((Room) obj).getBuilding();

      final Set<Building> buildings = ProjectManager.getProject().getBuildings();
      final Building parent = (Building) obj;

      final Building building = new Building();
      building.setParent(parent);

      for (int i = 1; i < 1000; ++i)
      {
         building.setName(I18n.formatMessage("NewBuildingPart", Integer.toString(i)));
         if (!buildings.contains(building))
            break;
      }

      ProjectManager.getController().add(building);
   }

   /**
    * Add a room to the current building, building part, or floor.
    * 
    * @param type - the type of the room to add.
    */
   public void addRoom(RoomType type)
   {
      Object obj = getSelectedObject();

      if (obj instanceof Device)
         obj = ((Device) obj).getRoom();
      if (obj instanceof Room)
         obj = ((Room) obj).getBuilding();

      final Building building = (Building) obj;
      final Set<Room> rooms = building.getRooms();

      final Room room = new Room();
      room.setType(type);

      for (int i = 1; i < 1000; ++i)
      {
         room.setName(I18n.formatMessage(type == RoomType.ROOM ? "NewRoom" : "NewCabinet", Integer.toString(i)));
         if (!rooms.contains(room))
            break;
      }

      ProjectManager.getController().add(building, room);
   }

   /**
    * Update the page's contents.
    */
   @Override
   public void updateContents()
   {
      getRootNode().removeAllChildren();

      final Project project = ProjectManager.getProject();
      if (project == null)
         return;

      // Find devices that are not assigned to a room, and put them
      // into "unassigned devices" (the grab bag).
      MutableIconTreeNode unassignedDevicesNode = null;
      for (Area area : project.getAreas())
      {
         for (Line line : area.getLines())
         {
            for (Device device : line.getDevices())
            {
               if (device.getRoom() == null)
               {
                  if (unassignedDevicesNode == null)
                  {
                     unassignedDevicesNode = new MutableIconTreeNode(new UnassignedDevicesStore(), true);
                     unassignedDevicesNode.setIcon(unassignedDevicesStoreIcon);
                     getRootNode().add(unassignedDevicesNode);
                  }

                  final MutableIconTreeNode deviceNode = new MutableIconTreeNode(device, true);
                  deviceNode.setIcon(deviceIcon);
                  unassignedDevicesNode.add(deviceNode);
               }
            }
         }
      }

      // Add all rooms and devices and create the building and building part
      // tree nodes. The tree nodes for buildings and building parts are
      // added later, to make the building parts appear in the tree after
      // the rooms of a building.
      final Map<Building, MutableIconTreeNode> buildingNodes = new TreeMap<Building, MutableIconTreeNode>();
      for (final Building building : project.getBuildings())
      {
         final Building parentBuilding = building.getParent();

         final MutableIconTreeNode buildingNode = new MutableIconTreeNode(building, true);
         buildingNode.setIcon(parentBuilding == null ? buildingIcon : buildingPartIcon);
         buildingNodes.put(building, buildingNode);

         for (Room room : building.getRooms())
         {
            final MutableIconTreeNode roomNode = new MutableIconTreeNode(room, true);

            if (room.getType() == RoomType.CABINET)
               roomNode.setIcon(cabinetIcon);
            else roomNode.setIcon(roomIcon);

            buildingNode.add(roomNode);

            for (Device device : room.getDevices())
            {
               final MutableIconTreeNode deviceNode = new MutableIconTreeNode(device, true);
               deviceNode.setIcon(deviceIcon);
               roomNode.add(deviceNode);
            }
         }
      }

      // Add all buildings and building parts to the tree
      for (final Entry<Building, MutableIconTreeNode> e : buildingNodes.entrySet())
      {
         final MutableIconTreeNode buildingNode = e.getValue();
         final Building parentBuilding = e.getKey().getParent();

         final MutableIconTreeNode parentNode = parentBuilding == null ? getRootNode() : buildingNodes.get(parentBuilding);
         parentNode.add(buildingNode);
      }

      getTreeModel().reload();
      TreeUtils.expandAll(getTree());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected boolean isRelevant(final Object obj)
   {
      return obj instanceof Building || obj instanceof Room || obj instanceof Device;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void objectSelected(Object obj)
   {
      if (obj instanceof Building)
      {
         setButtonsEnabled(true);
         btnAddDevice.setEnabled(false);
      }
      else if (obj instanceof Room)
      {
         setButtonsEnabled(true);
      }
      else if (obj instanceof Device)
      {
         setButtonsEnabled(true);
      }
      else
      {
         setButtonsEnabled(false);
      }
   }


   /**
    * {@inheritDoc}
    */
   @Override
   protected boolean acceptsDrop(Object target, Transferable trans)
   {
      final List<Object> objs = getTransferableObjects(trans);

      for (final Object obj : objs)
         if (obj == target)
            return true;

      if (target instanceof Room || target instanceof Device)
      {
         for (final Object obj : objs)
            if (obj instanceof Device)
               return true;
      }
      else if (target instanceof Building)
      {
         for (final Object obj : objs)
            if (obj instanceof Room || obj instanceof Building)
               return true;
      }

      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected boolean handleDrop(Object target, Transferable trans)
   {
      final List<Object> objs = getTransferableObjects(trans);
      boolean dropped = false;

      for (final Object obj : objs)
      {
         if (obj == target)
         {
            dropped = true;
            continue;
         }

         if (target instanceof Room && obj instanceof Device)
            ((Room) target).add((Device) obj);
         else if (target instanceof Building && obj instanceof Building)
            ((Building) obj).setParent((Building) target);
         else if (target instanceof Building && obj instanceof Room)
            ((Building) target).add((Room) obj);
         else continue;

         ProjectManager.fireComponentModified(obj);
         ProjectManager.fireComponentModified(target);
      }

      if (dropped)
         ProjectManager.fireComponentModified(target);

      return dropped;
   }
}
