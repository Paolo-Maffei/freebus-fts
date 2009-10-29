package org.freebus.fts.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Buildings;
import org.freebus.fts.project.Floor;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.Room;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.ImageCache;

/**
 * Tab that shows the physical components of the project
 * (buildings, floors, rooms)
 */
public class PhysicalTab extends TreeTabPage implements MouseListener
{
   private Project project = null;
   

   public PhysicalTab(Composite parent)
   {
      super(parent);
      setTitle(I18n.getMessage("Physical_Tab"));
      setPlace(SWT.LEFT);
      getToolBar().pack();
      
      tree.addMouseListener(this);
   }

   /**
    * Set the project that is displayed.
    * Calls {@link #updateContents}.
    */
   @Override
   public void setObject(Object o)
   {
      this.project = (Project) o;
      updateContents();
   }
   
   /**
    * Call to update the widget's contents.
    */
   public void updateContents()
   {
      tree.removeAll();
      if (project==null) return;
      
      TreeItem roomItem, floorItem, buildingItem;
      final Image buildingIcon = ImageCache.getImage("icons/building");
      final Image floorIcon = ImageCache.getImage("icons/floor");
      final Image roomIcon = ImageCache.getImage("icons/room");
      
      final Buildings buildings = project.getBuildings();
      final int numBuildings = buildings.size();
      for (int buildingIdx=0; buildingIdx<numBuildings; ++buildingIdx)
      {
         final Building building = buildings.get(buildingIdx);
         buildingItem = new TreeItem(tree, SWT.DEFAULT);
         buildingItem.setText(building.getName());
         buildingItem.setImage(buildingIcon);
      
         final int numFloors = building.size();
         for (int floorIdx=0; floorIdx<numFloors; ++floorIdx)
         {
            final Floor floor = building.get(floorIdx);
            floorItem = new TreeItem(buildingItem, SWT.DEFAULT);
            floorItem.setText(floor.getName());
            floorItem.setImage(floorIcon);
            floorItem.setData(floor);
      
            final int numRooms = floor.size();
            for (int roomIdx=0; roomIdx<numRooms; ++roomIdx)
            {
               final Room room = floor.get(roomIdx);
               roomItem = new TreeItem(floorItem, SWT.DEFAULT);
               roomItem.setText(room.getName());
               roomItem.setImage(roomIcon);
            }

            if (floorIdx==0) buildingItem.setExpanded(true);
            floorItem.setExpanded(true);
         }
      }
   }

   @Override
   public void mouseDoubleClick(MouseEvent e)
   {
   }

   @Override
   public void mouseDown(MouseEvent e)
   {
      if (e.widget != null && e.widget instanceof Tree)
      {
         final TreeItem treeItem = tree.getItem(new Point(e.x, e.y));
         final Object data = treeItem.getData();
         if (data instanceof Floor)
         {
            MainWindow.getInstance().showTabPage(FloorPlanTab.class, (Floor) data);
         }
      }
   }

   @Override
   public void mouseUp(MouseEvent e)
   {
   }
}
