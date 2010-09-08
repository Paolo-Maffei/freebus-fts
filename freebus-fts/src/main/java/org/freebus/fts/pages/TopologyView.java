package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.freebus.fts.MainWindow;
import org.freebus.fts.actions.Actions;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.PagePosition;
import org.freebus.fts.components.ToolBar;
import org.freebus.fts.components.ToolBarButton;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.dialogs.AreaProperties;
import org.freebus.fts.dialogs.DeviceProperties;
import org.freebus.fts.dialogs.LineProperties;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectListener;
import org.freebus.fts.renderers.DynamicIconTreeCellRenderer;
import org.freebus.fts.utils.TreeUtils;

/**
 * Shows the topological structure of the project.
 */
public class TopologyView extends AbstractPage
{
   private static final long serialVersionUID = 4442753739761863742L;

   private final JTree tree;
   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Project");
   private final JScrollPane treeView;
   private JButton btnAddArea, btnAddLine, btnAddDevice, btnEditProperties, btnEditDevice, btnDelete;
   private Object selectedObject;

   /**
    * Create a page that shows the topological structure of the project.
    */
   public TopologyView()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("TopologyView.Title"));

      tree = new JTree(rootNode);
      tree.setRootVisible(false);

      final DynamicIconTreeCellRenderer renderer = new DynamicIconTreeCellRenderer();
      tree.setCellRenderer(renderer);
      renderer.setCellTypeIcon(Area.class, ImageCache.getIcon("icons/area"));
      renderer.setCellTypeIcon(Line.class, ImageCache.getIcon("icons/line"));
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
            selectedObject = node != null ? node.getUserObject() : null;

            if (selectedObject instanceof Area)
            {
               btnAddLine.setEnabled(true);
               btnAddDevice.setEnabled(false);
               btnEditProperties.setEnabled(true);
               btnEditDevice.setEnabled(false);
               btnDelete.setEnabled(true);
            }
            else if (selectedObject instanceof Line)
            {
               btnAddLine.setEnabled(true);
               btnAddDevice.setEnabled(true);
               btnEditProperties.setEnabled(true);
               btnEditDevice.setEnabled(false);
               btnDelete.setEnabled(true);
            }
            else if (selectedObject instanceof Device)
            {
               btnAddLine.setEnabled(true);
               btnAddDevice.setEnabled(true);
               btnEditProperties.setEnabled(true);
               btnEditDevice.setEnabled(true);
               btnDelete.setEnabled(true);
            }
            else
            {
               btnAddLine.setEnabled(false);
               btnAddDevice.setEnabled(false);
               btnEditProperties.setEnabled(false);
               btnEditDevice.setEnabled(false);
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
               if (selectedObject instanceof Device)
                  ProjectManager.getController().edit((Device) selectedObject);

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
         if (isRelevant(obj))
            tree.updateUI();
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

      btnAddArea = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            addArea();
         }
      });
      btnAddArea.setIcon(ImageCache.getIcon("icons/area-new"));
      btnAddArea.setToolTipText(I18n.getMessage("TopologyView.AddAreaTip"));

      btnAddLine = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            addLine();
         }
      });
      btnAddLine.setEnabled(false);
      btnAddLine.setIcon(ImageCache.getIcon("icons/line-new"));
      btnAddLine.setToolTipText(I18n.getMessage("TopologyView.AddLineTip"));

      btnAddDevice = toolBar.add(Actions.ADD_DEVICES.action);
      btnAddDevice.setEnabled(false);
      btnAddDevice.setIcon(ImageCache.getIcon("icons/device-new"));
      btnAddDevice.setToolTipText(I18n.getMessage("TopologyView.AddDeviceTip"));

      btnEditProperties = new ToolBarButton(ImageCache.getIcon("icons/edit-properties"));
      toolBar.add(btnEditProperties);
      btnEditProperties.setEnabled(false);
      btnEditProperties.setToolTipText(I18n.getMessage("TopologyView.EditItemTip"));
      btnEditProperties.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent arg0)
         {
            final Object obj = getSelectedObject();
            if (obj instanceof Device)
               editDeviceProperties((Device) obj);
            else if (obj instanceof Area)
               editAreaProperties((Area) obj);
            else if (obj instanceof Line)
               editLineProperties((Line) obj);
         }
      });

      btnEditDevice = new ToolBarButton(ImageCache.getIcon("icons/configure"));
      toolBar.add(btnEditDevice);
      btnEditDevice.setEnabled(false);
      btnEditDevice.setToolTipText(I18n.getMessage("TopologyView.EditItemTip"));
      btnEditDevice.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent arg0)
         {
            ProjectManager.getController().edit(getSelectedObject());
         }
      });

      btnDelete = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 5637927204922440539L;

         @Override
         public void actionPerformed(ActionEvent arg0)
         {
            ProjectManager.getController().remove(getSelectedObject());
         }
      });
      toolBar.add(btnDelete);
      btnDelete.setEnabled(false);
      btnDelete.setIcon(ImageCache.getIcon("icons/delete"));
      btnDelete.setToolTipText(I18n.getMessage("TopologyView.DeleteItemTip"));
   }

   /**
    * Test if an object is relevant for this view. Used e.g. for event handlers.
    * 
    * @param obj - the object to test.
    * @return true if the object is relevant.
    */
   private boolean isRelevant(final Object obj)
   {
      return obj instanceof Device || obj instanceof Line || obj instanceof Area;
   }

   /**
    * Add an area.
    */
   protected void addArea()
   {
      final Project project = ProjectManager.getProject();
      if (project == null)
         return;

      // Request name first
      final AreaProperties dlg = new AreaProperties(MainWindow.getInstance());
      dlg.setVisible(true); // this dialog is modal
      if (dlg.isAccepted() == false)
      {
         return;
      }

      final Area area = project.addArea(dlg.getAreaName());
      area.setAddress(dlg.getAddress());

      final DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
      final DefaultMutableTreeNode areaNode = new DefaultMutableTreeNode(area, true);

      treeModel.insertNodeInto(areaNode, rootNode, 0);
      tree.expandPath(new TreePath(rootNode));

      updateContents();
   }

   /**
    * open the dialog to edit the properties of the area
    * 
    * @param area to edit
    */
   protected void editAreaProperties(Area area)
   {
      if (area == null)
         return;

      final AreaProperties dlg = new AreaProperties(MainWindow.getInstance(), area.getName(), area.getAddress());

      dlg.setVisible(true); // this dialog is modal
      if (dlg.isAccepted() == false)
      {
         return;
      }
      area.setName(dlg.getAreaName());
      area.setAddress(dlg.getAddress());

      updateContents();
   }

   /**
    * Add a line to the selected area.
    */
   protected void addLine()
   {
      final Project project = ProjectManager.getProject();
      if (project == null)
         return;

      DefaultMutableTreeNode areaNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
      if (areaNode == null)
         return;

      final Object selectedObject = areaNode.getUserObject();
      Area area = null;

      if (selectedObject instanceof Area)
      {
         area = (Area) selectedObject;
      }
      else if (selectedObject instanceof Line)
      {
         area = ((Line) selectedObject).getArea();
         areaNode = (DefaultMutableTreeNode) areaNode.getParent();
      }

      if (area == null)
         return;

      // Request name first
      final LineProperties dlg = new LineProperties(MainWindow.getInstance(), area);
      dlg.setVisible(true); // this dialog is modal
      if (dlg.isAccepted() == false)
      {
         return;
      }

      final Line line = new Line();
      line.setName(dlg.getLineName());
      line.setAddress(dlg.getAddress());
      // line.setArea(area); // wird in area.add erledigt.
      area.add(line);

      final DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

      final DefaultMutableTreeNode lineNode = new DefaultMutableTreeNode(line, true);
      treeModel.insertNodeInto(lineNode, areaNode, 0);

      tree.expandPath(new TreePath(areaNode));

      // updateContents();
   }

   /**
    * open the dialog to edit the properties of the line
    * 
    * @param line to edit
    */
   protected void editLineProperties(Line line)
   {
      if (line == null)
         return;

      final LineProperties dlg = new LineProperties(MainWindow.getInstance(), line);

      dlg.setVisible(true); // this dialog is modal
      if (dlg.isAccepted() == false)
      {
         return;
      }
      line.setName(dlg.getLineName());
      line.setAddress(dlg.getAddress());

      updateContents();
   }

   /**
    * Add a device to the selected line.
    */
   public void editDeviceProperties(Device device)
   {
      Line line = device.getLine();
      if (line == null)
         return;

      // Request name first
      final DeviceProperties dlg = new DeviceProperties(MainWindow.getInstance(), device);
      dlg.setVisible(true); // this dialog is modal
      if (dlg.isAccepted() == false)
      {
         return;
      }

      // device.setName(dlg.getDeviceName()); //TODO do we allow to change the
      // name ?
      device.setAddress(dlg.getAddress());
   }

   /**
    * Add a device to the selected line.
    */
   public void addDevice(Device device)
   {
      DefaultMutableTreeNode lineNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
      final Object selectedObject = lineNode == null ? null : lineNode.getUserObject();
      Line line = null;

      if (selectedObject instanceof Line)
      {
         line = (Line) selectedObject;
      }
      else if (selectedObject instanceof Device)
      {
         line = ((Device) selectedObject).getLine();
         lineNode = (DefaultMutableTreeNode) lineNode.getParent();
      }

      if (line == null)
      {
         JOptionPane.showMessageDialog(MainWindow.getInstance(), I18n.getMessage("TopologyView.ErrNoLineSelected"),
               I18n.getMessage("TopologyView.ErrTitle"), JOptionPane.ERROR_MESSAGE);
         return;
      }

      int freeAddr = 0;
      try
      {
         freeAddr = line.getFreeAddress();
      }
      catch (RuntimeException e)
      {
         // TODO Error dialog
         return;
      }

      device.setAddress(freeAddr);
      line.add(device);

      final DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

      final DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode(device, true);
      treeModel.insertNodeInto(deviceNode, lineNode, 0);

      tree.expandPath(new TreePath(lineNode));
   }

   /**
    * @return the user-object of the currently selected tree node, or null if
    *         nothing is selected.
    */
   public Object getSelectedObject()
   {
      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
      if (node == null)
         return null;
      return node.getUserObject();
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

      for (Area area : project.getAreas())
      {
         DefaultMutableTreeNode areaNode = new DefaultMutableTreeNode(area, true);
         rootNode.add(areaNode);

         for (Line line : area.getLines())
         {
            DefaultMutableTreeNode lineNode = new DefaultMutableTreeNode(line, true);
            areaNode.add(lineNode);

            for (Device device : line.getDevices())
            {
               DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode(device, true);
               lineNode.add(deviceNode);
            }
         }
      }

      ((DefaultTreeModel) tree.getModel()).reload();
      TreeUtils.expandAll(tree);
   }
}
