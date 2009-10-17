package org.freebus.fts.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MainGroups;
import org.freebus.fts.project.MiddleGroup;
import org.freebus.fts.project.Project;
import org.freebus.fts.utils.ImageCache;

public class LogicalTab extends Composite
{
   private final Tree tree;
   private Project project = null;

   public LogicalTab(Composite parent, Project project)
   {
      super(parent, SWT.NONE);

      FillLayout layout = new FillLayout();
      layout.type = SWT.VERTICAL;
      setLayout(layout);

      tree = new Tree(this, SWT.BORDER | SWT.SINGLE);
      
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
      tree.removeAll();
      if (project==null) return;
      
      TreeItem mainGroupItem, midGroupItem, deviceItem;
      String mainGroupIdStr, midGroupIdStr, deviceIdStr;

      final Image mainGroupIcon = ImageCache.getImage("icons/main-group");
      final Image midGroupIcon = ImageCache.getImage("icons/middle-group");
      final Image deviceIcon = ImageCache.getImage("icons/sub-group");
      
      final MainGroups mainGroups = project.getMainGroups();
      final int numMainGroups = mainGroups.size();
      for (int mainGroupIdx=0; mainGroupIdx<numMainGroups; ++mainGroupIdx)
      {
         final MainGroup mainGroup = mainGroups.get(mainGroupIdx);
         mainGroupItem = new TreeItem(tree, SWT.DEFAULT);
         mainGroupIdStr = Integer.toString(mainGroup.getId());
         mainGroupItem.setText(mainGroup.getName()+" ("+mainGroupIdStr+")");
         mainGroupItem.setImage(mainGroupIcon);
      
         final int numMidGroups = mainGroup.size();
         for (int midGroupIdx=0; midGroupIdx<numMidGroups; ++midGroupIdx)
         {
            final MiddleGroup midGroup = mainGroup.get(midGroupIdx);
            midGroupItem = new TreeItem(mainGroupItem, SWT.DEFAULT);
            midGroupIdStr = mainGroupIdStr+"/"+Integer.toString(midGroup.getId());
            midGroupItem.setText(midGroup.getName()+" ("+midGroupIdStr+")");
            midGroupItem.setImage(midGroupIcon);
      
            final int numDevices = midGroup.size();
            for (int deviceIdx=0; deviceIdx<numDevices; ++deviceIdx)
            {
               final Device device = midGroup.get(deviceIdx);
               deviceItem = new TreeItem(midGroupItem, SWT.DEFAULT);
               deviceIdStr = midGroupIdStr+"/"+device.getPhysicalAddr().getDeviceId();
               deviceItem.setText(device.getName()+" ("+deviceIdStr+")");
               deviceItem.setImage(deviceIcon);
               deviceItem.setExpanded(true);
            }
      
           if (midGroupIdx==0) mainGroupItem.setExpanded(true);
           midGroupItem.setExpanded(true);
         }      
       }
   }
}
