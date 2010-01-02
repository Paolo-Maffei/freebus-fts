package org.freebus.fts.pages;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.PagePosition;
import org.freebus.fts.core.I18n;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
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
      // TODO Auto-generated method stub
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

      for (Area area : project.getAreas())
      {
         DefaultMutableTreeNode areaNode = new DefaultMutableTreeNode(area.toString(), true);
         rootNode.add(areaNode);

         for (Line line : area.getLines())
         {
            DefaultMutableTreeNode lineNode = new DefaultMutableTreeNode(line.toString(), true);
            areaNode.add(lineNode);

            for (Device device: line.getDevices())
            {
               DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode("[Device] " + device.toString(), true);
               lineNode.add(deviceNode);
            }
         }
      }

      TreeUtils.expandAll(tree);
   }
}
