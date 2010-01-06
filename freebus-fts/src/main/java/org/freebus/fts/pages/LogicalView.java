package org.freebus.fts.pages;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.PagePosition;
import org.freebus.fts.core.I18n;
import org.freebus.fts.project.Group;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.utils.TreeUtils;

/**
 * Shows the logical structure of the project. The main- and sub-groups with
 * the devices.
 */
public class LogicalView extends AbstractPage
{
   private static final long serialVersionUID = 4775024754983180786L;

   private final JTree tree;
   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Project");
   private final JScrollPane treeView;

   /**
    * Create a page that shows the topological structure of the project.
    */
   public LogicalView()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("LogicalView.Title"));

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

      for (MainGroup mainGroup : project.getMainGroups())
      {
         DefaultMutableTreeNode areaNode = new DefaultMutableTreeNode(mainGroup.toString(), true);
         rootNode.add(areaNode);

         for (MidGroup midGroup : mainGroup.getMidGroups())
         {
            DefaultMutableTreeNode lineNode = new DefaultMutableTreeNode(midGroup.toString(), true);
            areaNode.add(lineNode);

            for (Group group: midGroup.getGroups())
            {
               DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode(group.toString(), true);
               lineNode.add(deviceNode);
            }
         }
      }

      TreeUtils.expandAll(tree);
   }
}