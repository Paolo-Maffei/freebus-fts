package org.freebus.fts.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Areas;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.utils.ImageCache;
import org.freebus.fts.utils.SimpleSelectionListener;

public class TopologyTab extends Composite
{
   private final Tree tree;
   private Project project = null;

   public TopologyTab(Composite parent, Project project)
   {
      super(parent, SWT.NONE);
      setLayout(new FormLayout());

      FormData formData;
      ToolItem toolItem;

      ToolBar toolBar = new ToolBar(this, SWT.FLAT|SWT.RIGHT_TO_LEFT);
      formData = new FormData();
      formData.top = new FormAttachment(0);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      toolBar.setLayoutData(formData);

      toolItem = new ToolItem(toolBar, SWT.PUSH);
      toolItem.setImage(ImageCache.getImage("icons/collapse-all"));
      toolItem.addSelectionListener(new onCollapseAll());

      toolItem = new ToolItem(toolBar, SWT.PUSH);
      toolItem.setImage(ImageCache.getImage("icons/expand-all"));
      toolItem.addSelectionListener(new onExpandAll());

      toolBar.pack();

      tree = new Tree(this, SWT.BORDER | SWT.SINGLE);
      formData = new FormData();
      formData.top = new FormAttachment(toolBar, 0);
      formData.bottom = new FormAttachment(99);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      tree.setLayoutData(formData);

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

   /**
    * Event listener for: collapse-all
    */
   private class onCollapseAll extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         GuiUtils.setExpandedAll(tree, false);
      }
   }

   /**
    * Event listener for: expand-all
    */
   private class onExpandAll extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         GuiUtils.setExpandedAll(tree, true);
      }
   }
}
