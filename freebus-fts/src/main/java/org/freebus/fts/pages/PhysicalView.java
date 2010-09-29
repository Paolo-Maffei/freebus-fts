package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
import org.freebus.fts.core.I18n;
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
   private JButton btnAddBuilding, btnAddRoom, btnAddDevice, btnEdit, btnDelete;
   private Object selectedObject;

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

            if (selectedObject instanceof Building)
            {
               btnAddRoom.setEnabled(true);
               btnAddDevice.setEnabled(false);
               btnEdit.setEnabled(true);
               btnDelete.setEnabled(true);
            }
            else if (selectedObject instanceof Room)
            {
               btnAddRoom.setEnabled(true);
               btnAddDevice.setEnabled(true);
               btnEdit.setEnabled(true);
               btnDelete.setEnabled(true);
            }
            else if (selectedObject instanceof Device)
            {
               btnAddRoom.setEnabled(true);
               btnAddDevice.setEnabled(true);
               btnEdit.setEnabled(true);
               btnDelete.setEnabled(true);
            }
            else
            {
               btnAddRoom.setEnabled(false);
               btnAddDevice.setEnabled(false);
               btnEdit.setEnabled(false);
               btnDelete.setEnabled(false);
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

      DefaultMutableTreeNode unassignedDevicesNode = null;
      for (Area area: project.getAreas())
      {
         for (Line line: area.getLines())
         {
            for (Device device: line.getDevices())
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

      for (Building building : project.getBuildings())
      {
         final DefaultMutableTreeNode buildingNode = new DefaultMutableTreeNode(building, true);
         rootNode.add(buildingNode);

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
}
