package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.freebus.fts.actions.Actions;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.PagePosition;
import org.freebus.fts.components.ToolBar;
import org.freebus.fts.components.ToolBarButton;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.Room;
import org.freebus.fts.renderers.DynamicIconTreeCellRenderer;
import org.freebus.fts.utils.TreeUtils;

/**
 * A page that shows the physical components of the project
 * (buildings, floors, rooms).
 */
public class PhysicalView extends AbstractPage
{
   private static final long serialVersionUID = 8538623742260660512L;

   private final JTree tree;
   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Project");
   private final JScrollPane treeView;
   private JButton btnAddBuilding, btnAddRoom, btnAddDevice, btnEditProperties, btnEditDevice, btnDelete;

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

      treeView = new JScrollPane(tree);
      add(treeView, BorderLayout.CENTER);

      initToolBar();

      tree.addTreeSelectionListener(new TreeSelectionListener()
      {
         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            final Object userObject = node != null ? node.getUserObject() : null;

            if (userObject instanceof Building)
            {
               btnAddRoom.setEnabled(true);
               btnAddDevice.setEnabled(false);
               btnEditProperties.setEnabled(true);
               btnEditDevice.setEnabled(false);
               btnDelete.setEnabled(true);
            }
            else if (userObject instanceof Room)
            {
               btnAddRoom.setEnabled(true);
               btnAddDevice.setEnabled(true);
               btnEditProperties.setEnabled(true);
               btnEditDevice.setEnabled(false);
               btnDelete.setEnabled(true);
            }
            else if (userObject instanceof Device)
            {
               btnAddRoom.setEnabled(true);
               btnAddDevice.setEnabled(true);
               btnEditProperties.setEnabled(true);
               btnEditDevice.setEnabled(true);
               btnDelete.setEnabled(true); // TODO not yet done
            }
            else
            {
               btnAddRoom.setEnabled(false);
               btnAddDevice.setEnabled(false);
               btnEditProperties.setEnabled(false);
               btnEditDevice.setEnabled(false);
               btnDelete.setEnabled(false);
            }
         }
      });
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
      btnAddBuilding.setIcon(ImageCache.getIcon("icons/area-new"));
      btnAddBuilding.setToolTipText(I18n.getMessage("PhysicalView.AddBuildingTip"));

      btnAddRoom = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            addRoom();
         }
      });
      btnAddRoom.setEnabled(false);
      btnAddRoom.setIcon(ImageCache.getIcon("icons/room-new"));
      btnAddRoom.setToolTipText(I18n.getMessage("PhysicalView.AddRoomTip"));

      btnAddDevice = toolBar.add(Actions.ADD_DEVICES.action);
      btnAddDevice.setEnabled(false);
      btnAddDevice.setIcon(ImageCache.getIcon("icons/device-new"));
      btnAddDevice.setToolTipText(I18n.getMessage("PhysicalView.AddDeviceTip"));

      btnEditProperties = new ToolBarButton(ImageCache.getIcon("icons/edit-properties"));
      toolBar.add(btnEditProperties);
      btnEditProperties.setEnabled(false);
      btnEditProperties.setToolTipText(I18n.getMessage("PhysicalView.EditItemTip"));
      btnEditProperties.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent arg0)
         {
//            final Object obj = getSelectedUserObject();
//            if (obj instanceof Device)
//               editDeviceProperties((Device) obj);
//            else if (obj instanceof Area)
//               editAreaProperties((Area) obj);
//            else if (obj instanceof Line)
//               editLineProperties((Line) obj);
         }
      });

      btnEditDevice = new ToolBarButton(ImageCache.getIcon("icons/configure"));
      toolBar.add(btnEditDevice);
      btnEditDevice.setEnabled(false);
      btnEditDevice.setToolTipText(I18n.getMessage("PhysicalView.EditItemTip"));
      btnEditDevice.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent arg0)
         {
//            final Object obj = getSelectedUserObject();
//            if (obj instanceof Device)
//               editDevice((Device) obj);
         }
      });

      btnDelete = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 5637927204922440539L;

         @Override
         public void actionPerformed(ActionEvent arg0)
         {
//            final Object obj = getSelectedUserObject();
//            if (obj instanceof Area)
//               deleteArea((Area) obj);
//            else if (obj instanceof Line)
//               deleteLine((Line) obj);
//            else if (obj instanceof Device)
//               deleteDevice((Device) obj);
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
      // TODO
   }

   /**
    * Add a room to the current building
    */
   public void addRoom()
   {
      // TODO
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
      if (project == null) return;

      for (Building building : project.getBuildings())
      {
         DefaultMutableTreeNode buildingNode = new DefaultMutableTreeNode(building, true);
         rootNode.add(buildingNode);

         for (Room room : building.getRooms())
         {
            DefaultMutableTreeNode roomNode = new DefaultMutableTreeNode(room, true);
            buildingNode.add(roomNode);

            for (Device device: room.getDevices())
            {
               DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode(device, true);
               roomNode.add(deviceNode);
            }
         }
      }

      ((DefaultTreeModel) tree.getModel()).reload();
      TreeUtils.expandAll(tree);
   }
}
