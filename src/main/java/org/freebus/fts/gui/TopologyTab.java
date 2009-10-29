package org.freebus.fts.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Areas;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.ImageCache;

public class TopologyTab extends TreeTabPage
{
   private Project project = null;

   public TopologyTab(Composite parent)
   {
      super(parent);
      setTitle(I18n.getMessage("Topology_Tab"));
      setPlace(SWT.LEFT);
      getToolBar().pack();
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
      
      TreeItem deviceItem, lineItem, areaItem;
      final Image areaIcon = ImageCache.getImage("icons/area");
      final Image lineIcon = ImageCache.getImage("icons/line");
      final Image deviceIcon = ImageCache.getImage("icons/device");
      
      final Areas areas = project.getAreas();
      final int numAreas = areas.size();
      for (int areaIdx=0; areaIdx<numAreas; ++areaIdx)
      {
         final Area area = areas.get(areaIdx);
         areaItem = new TreeItem(tree, SWT.DEFAULT);
         final String areaIdStr = Integer.toString(area.getId());
         areaItem.setText(area.getName()+" ("+areaIdStr+")");
         areaItem.setImage(areaIcon);
      
         final int numLines = area.size();
         for (int lineIdx=0; lineIdx<numLines; ++lineIdx)
         {
            final Line line = area.get(lineIdx);
            lineItem = new TreeItem(areaItem, SWT.DEFAULT);
            lineItem.setText(line.getName()+" ("+areaIdStr+"."+Integer.toString(line.getId())+")");
            lineItem.setImage(lineIcon);
      
            final int numDevices = line.size();
            for (int deviceIdx=0; deviceIdx<numDevices; ++deviceIdx)
            {
               final Device device = line.get(deviceIdx);
               deviceItem = new TreeItem(lineItem, SWT.DEFAULT);
               deviceItem.setText(device.getPhysicalAddr().toString());
               deviceItem.setImage(deviceIcon);
            }
      
            if (lineIdx==0) areaItem.setExpanded(true);
            lineItem.setExpanded(true);
         }
      }
   }
}
