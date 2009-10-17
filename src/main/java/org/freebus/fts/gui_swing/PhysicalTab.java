package org.freebus.fts.gui_swing;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.swt.SWT;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Buildings;
import org.freebus.fts.project.Floor;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.Room;
import org.freebus.fts.utils.I18n;

/**
 * Tab that shows the physical components of the project
 * (buildings, floors, rooms)
 */
public class PhysicalTab extends TabPage
{
   private static final long serialVersionUID = 5961775403123671913L;

   private Project project = null;
   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("/");
   private final JTree tree = new JTree(rootNode);
   private final JScrollPane treeView = new JScrollPane(tree);
   
   public PhysicalTab(Project project)
   {
      super();
      setLayout(new BorderLayout());

      tree.setRootVisible(false);
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      add(treeView);

      setProject(project);
   }

   /**
    * Set the project that is displayed.
    * Calls {@link #updateContents}.
    */
   public void setProject(Project project)
   {
      this.project = project;
      updateContents();
   }
   
   /**
    * Call to update the widget's contents.
    */
   public void updateContents()
   {
      rootNode.removeAllChildren();
      if (project==null) return;
      
      DefaultMutableTreeNode roomItem, floorItem, buildingItem;
//      final Image buildingIcon = ImageCache.getImage("icons/building");
//      final Image floorIcon = ImageCache.getImage("icons/floor");
//      final Image roomIcon = ImageCache.getImage("icons/room");
      
      final Buildings buildings = project.getBuildings();
      final int numBuildings = buildings.size();
      for (int buildingIdx=0; buildingIdx<numBuildings; ++buildingIdx)
      {
         final Building building = buildings.get(buildingIdx);
         buildingItem = new DefaultMutableTreeNode(building.getName());
         rootNode.add(buildingItem);
      
         final int numFloors = building.size();
         for (int floorIdx=0; floorIdx<numFloors; ++floorIdx)
         {
            final Floor floor = building.get(floorIdx);
            floorItem = new DefaultMutableTreeNode(floor.getName());
            buildingItem.add(floorItem);
      
            final int numRooms = floor.size();
            for (int roomIdx=0; roomIdx<numRooms; ++roomIdx)
            {
               final Room room = floor.get(roomIdx);
               roomItem = new DefaultMutableTreeNode(room.getName());
               floorItem.add(roomItem);
            }
         }
      }

      tree.setRootVisible(true);
      for (int row=0; row<tree.getRowCount(); ++row)
        tree.expandRow(row);
      tree.setRootVisible(false);

      tree.treeDidChange();
   }

   /**
    * @return the title of the tab-page.
    */
   @Override
   public String getTitle()
   {
      return I18n.getMessage("Physical_Tab");
   }
}
