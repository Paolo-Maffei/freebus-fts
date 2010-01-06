package org.freebus.fts.pages;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.PagePosition;
import org.freebus.fts.core.I18n;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.Room;
import org.freebus.fts.utils.TreeUtils;

/**
 * A page that shows the physical components of the project
 * (buildings, floors, rooms).
 */
public class PhysicalView extends AbstractPage
{
   private static final long serialVersionUID = 4442753739761863742L;

   private final JTree tree;
   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Project");
   private final JScrollPane treeView;

   /**
    * Create a page that shows the topological structure of the project.
    */
   public PhysicalView()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("PhysicalView.Title"));

      tree = new JTree(rootNode);
      tree.setRootVisible(false);

      treeView = new JScrollPane(tree);
      add(treeView, BorderLayout.CENTER);
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
         DefaultMutableTreeNode buildingNode = new DefaultMutableTreeNode(building.toString(), true);
         rootNode.add(buildingNode);

         for (Room room : building.getRooms())
         {
            DefaultMutableTreeNode roomNode = new DefaultMutableTreeNode(room.toString(), true);
            buildingNode.add(roomNode);

            for (Device device: room.getDevices())
            {
               DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode(device.toString(), true);
               roomNode.add(deviceNode);
            }
         }
      }

      TreeUtils.expandAll(tree);
   }
}
