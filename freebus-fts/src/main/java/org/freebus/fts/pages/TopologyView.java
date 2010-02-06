package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

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
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
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
   private JButton btnAddArea, btnAddLine, btnAddDevice;

   /**
    * Create a page that shows the topological structure of the project.
    */
   public TopologyView()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("TopologyView.Title"));

      tree = new JTree(rootNode);
      tree.setRootVisible(false);

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

            if (userObject instanceof Area)
            {
               btnAddLine.setEnabled(true);
               btnAddDevice.setEnabled(false);
            }
            else if (userObject instanceof Line || userObject instanceof Device)
            {
               btnAddLine.setEnabled(true);
               btnAddDevice.setEnabled(true);
            }
            else
            {
               btnAddLine.setEnabled(false);
               btnAddDevice.setEnabled(false);
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
   }

   /**
    * Add an area.
    */
   protected void addArea()
   {
      final Area area = new Area();
      area.setName("New area");

      final DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
      final DefaultMutableTreeNode areaNode = new DefaultMutableTreeNode(area, true);

      treeModel.insertNodeInto(areaNode, rootNode, 0);
      tree.expandPath(new TreePath(rootNode));
   }

   /**
    * Add a line to the selected area.
    */
   protected void addLine()
   {
      DefaultMutableTreeNode areaNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
      if (areaNode == null) return;

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

      if (area == null) return;

      final Line line = new Line();
      line.setName("New line");
      area.add(line);

      final DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
      
      final DefaultMutableTreeNode lineNode = new DefaultMutableTreeNode(line, true);
      treeModel.insertNodeInto(lineNode, areaNode, 0);

      tree.expandPath(new TreePath(areaNode));
   }

   /**
    * Add a device to the selected line.
    */
   public void addDevice(final Device device)
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
         JOptionPane.showMessageDialog(MainWindow.getInstance(), I18n.getMessage("TopologyView.ErrNoLineSelected"), I18n.getMessage("TopologyView.ErrTitle"), JOptionPane.ERROR_MESSAGE);
         return;
      }

      device.setAddress(line.getFreeAddress());
      line.add(device);

      final DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
      
      final DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode(device, true);
      treeModel.insertNodeInto(deviceNode, lineNode, 0);

      tree.expandPath(new TreePath(lineNode));
   }

   /**
    * @return the user-object of the currently selected tree node, or null if nothing is selected.
    */
   public Object getSelectedUserObject()
   {
      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
      if (node == null) return null;
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
