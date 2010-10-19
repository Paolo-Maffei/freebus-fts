package org.freebus.fts.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.freebus.fts.I18n;
import org.freebus.fts.actions.Actions;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.PagePosition;
import org.freebus.fts.elements.components.ToolBar;
import org.freebus.fts.elements.components.ToolBarButton;
import org.freebus.fts.elements.renderers.DynamicIconTreeCellRenderer;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.elements.utils.TreeUtils;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.Room;
import org.freebus.fts.project.RoomType;
import org.freebus.fts.project.service.ProjectController;
import org.freebus.fts.project.service.ProjectListener;

/**
 * A page that shows the physical components of the project (buildings, floors,
 * rooms).
 */
public class PhysicalView extends AbstractPage
{
   private static final long serialVersionUID = 8538623742260660512L;

   private final JTree tree;
   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Project");
   private final JScrollPane treeView;
   private JButton btnAddBuilding, btnAddBuildingPart, btnAddCabinet, btnAddRoom, btnAddDevice;
   private JButton btnEdit, btnDelete;
   private Object selectedObject;

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
    * Internal class that encapsulates bulding parts / floors.
    */
   static class BuildingPartWrapper
   {
      public final Building building;

      public BuildingPartWrapper(Building building)
      {
         this.building = building;
      }

      @Override
      public String toString()
      {
         return building.toString();
      }
   }

   /**
    * Internal class that encapsulates rooms of type cabinet.
    */
   static class CabinetWrapper
   {
      public final Room room;

      public CabinetWrapper(Room room)
      {
         this.room = room;
      }

      @Override
      public String toString()
      {
         return room.toString();
      }
   }

   /**
    * Create a page that shows the topological structure of the project.
    */
   public PhysicalView()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("PhysicalView.Title"));

      tree = new JTree(rootNode);
      tree.setRootVisible(false);

      final DynamicIconTreeCellRenderer renderer = new DynamicIconTreeCellRenderer();
      tree.setCellRenderer(renderer);
      renderer.setCellTypeIcon(Building.class, ImageCache.getIcon("icons/building"));
      renderer.setCellTypeIcon(Room.class, ImageCache.getIcon("icons/room"));
      renderer.setCellTypeIcon(Device.class, ImageCache.getIcon("icons/device"));
      renderer.setCellTypeIcon(UnassignedDevicesStore.class, ImageCache.getIcon("icons/idea"));

      treeView = new JScrollPane(tree);
      add(treeView, BorderLayout.CENTER);

      initToolBar();

      tree.addTreeSelectionListener(new TreeSelectionListener()
      {
         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            selectedObject = node != null ? node.getUserObject() : null;

            if (selectedObject instanceof Building || selectedObject instanceof BuildingPartWrapper)
            {
               setButtonsEnabled(true);
               btnAddDevice.setEnabled(false);
            }
            else if (selectedObject instanceof Room || selectedObject instanceof CabinetWrapper)
            {
               setButtonsEnabled(true);
            }
            else if (selectedObject instanceof Device)
            {
               setButtonsEnabled(true);
            }
            else
            {
               setButtonsEnabled(false);
            }
         }
      });

      tree.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            if (e.getClickCount() == 2)
            {
               ProjectManager.getController().edit(selectedObject);
               e.consume();
            }
         }
      });

      ProjectManager.addListener(projectListener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void closeEvent()
   {
      ProjectManager.removeListener(projectListener);
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

   /*
    * Listener for project changes
    */
   private final ProjectListener projectListener = new ProjectListener()
   {
      @Override
      public void projectComponentRemoved(Object obj)
      {
         if (isRelevant(obj))
            updateContents();
      }

      @Override
      public void projectComponentModified(Object obj)
      {
         if (obj instanceof Device)
            tree.updateUI();
         else if (isRelevant(obj))
            updateContents();
      }

      @Override
      public void projectComponentAdded(Object obj)
      {
         if (isRelevant(obj))
            updateContents();
      }

      @Override
      public void projectChanged(Project project)
      {
         updateContents();
      }
   };

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
    * Test if an object is relevant for this view. Used e.g. for event handlers.
    * 
    * @param obj - the object to test.
    * @return true if the object is relevant.
    */
   private boolean isRelevant(final Object obj)
   {
      return obj instanceof Building || obj instanceof Room || obj instanceof Device;
   }

   /**
    * Add a building
    */
   public void addBuilding()
   {
      final Building building = new Building();
      building.setName(I18n.getMessage("NewBuilding"));
      ProjectManager.getController().add(building);
   }

   /**
    * Add a building part / floor
    */
   public void addBuildingPart()
   {
      final Building parent = (Building) getSelectedObject();

      final Building building = new Building();
      building.setName(I18n.getMessage("NewBuildingPart"));
      building.setParent(parent);

      ProjectManager.getController().add(building);
   }

   /**
    * Add a room to the current building, building part, or floor.
    * 
    * @param type - the type of the room to add.
    */
   public void addRoom(RoomType type)
   {
      final Building building = (Building) getSelectedObject();

      final Room room = new Room();
      room.setName(I18n.getMessage("NewRoom"));
      room.setType(type);

      ProjectManager.getController().add(building, room);
   }

   /**
    * @return the user-object of the currently selected tree node, or null if
    *         nothing is selected.
    */
   public Object getSelectedObject()
   {
      return selectedObject;
   }

   /**
    * @return the preferred position of the page: {@link PagePosition#LEFT}.
    */
   @Override
   public PagePosition getPagePosition()
   {
      return PagePosition.LEFT;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setObject(Object o)
   {
      updateContents();
   }

   /**
    * Update the page's contents.
    */
   @Override
   public void updateContents()
   {
      rootNode.removeAllChildren();

      final Project project = ProjectManager.getProject();
      if (project == null)
         return;

      // Find devices that are not assigned to a room, and put them
      // into "unassigned devices".
      DefaultMutableTreeNode unassignedDevicesNode = null;
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
                     unassignedDevicesNode = new DefaultMutableTreeNode(new UnassignedDevicesStore(), true);
                     rootNode.add(unassignedDevicesNode);
                  }

                  final DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode(device, true);
                  unassignedDevicesNode.add(deviceNode);
               }
            }
         }
      }

      final Set<Building> buldings = project.getBuildings();
      final Map<Building,DefaultMutableTreeNode> buildingNodes = addBuildings(buldings);

      for (Building building : buldings)
      {
         final DefaultMutableTreeNode buildingNode = buildingNodes.get(building);

         for (Room room : building.getRooms())
         {
            final DefaultMutableTreeNode roomNode = new DefaultMutableTreeNode(room, true);
            buildingNode.add(roomNode);

            for (Device device : room.getDevices())
            {
               final DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode(device, true);
               roomNode.add(deviceNode);
            }
         }
      }

      ((DefaultTreeModel) tree.getModel()).reload();
      TreeUtils.expandAll(tree);
   }

   /**
    * Add all buildings and building parts / floors to the tree.
    * 
    * @param buildings - the buildings to add.
    * @return A map containing the tree nodes of the added buildings. 
    */
   private Map<Building,DefaultMutableTreeNode> addBuildings(final Set<Building> buildings)
   {
      final Map<Building,DefaultMutableTreeNode> buildingNodes = new HashMap<Building,DefaultMutableTreeNode>();

      for (boolean found = true; found;)
      {
         found = false;

         for (Building building : buildings)
         {
            if (buildingNodes.containsKey(building))
               continue;

            final Building parentBuilding = building.getParent();
            DefaultMutableTreeNode buildingNode = null;

            if (parentBuilding == null)
            {
               buildingNode = new DefaultMutableTreeNode(building, true);
               rootNode.add(buildingNode);
            }
            else
            {
               final DefaultMutableTreeNode parentNode = buildingNodes.get(parentBuilding);
               if (parentNode == null)
                  continue;

               buildingNode = new DefaultMutableTreeNode(new BuildingPartWrapper(building), true);
               parentNode.add(buildingNode);
            }

            buildingNodes.put(building, buildingNode);
            found = true;
         }
      }

      return buildingNodes;
   }
}
