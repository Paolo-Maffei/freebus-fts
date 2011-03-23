package org.freebus.fts.client.views;

import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

import org.freebus.fts.client.actions.ActionFactory;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.dragdrop.ObjectTransferHandler;
import org.freebus.fts.elements.components.ToolBar;
import org.freebus.fts.elements.components.ToolBarButton;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.elements.tree.MutableIconTreeNode;
import org.freebus.fts.elements.utils.TreeUtils;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;

/**
 * Shows the topological structure of the project.
 */
public class TopologyView extends AbstractTreeView
{
   private static final long serialVersionUID = 4442753739761863742L;

   private final Icon areaIcon = ImageCache.getIcon("icons/area");
   private final Icon lineIcon = ImageCache.getIcon("icons/line");
   private final Icon deviceIcon = ImageCache.getIcon("icons/device");

   private JButton btnAddArea, btnAddLine, btnAddDevice, btnEdit, btnDelete, btnProgram;

   /**
    * Create a page that shows the topological structure of the project.
    */
   public TopologyView()
   {
      setName(I18n.getMessage("TopologyView.Title"));
      initToolBar();

      enableTreeDragDrop(true, DnDConstants.ACTION_MOVE);
      getTree().setTransferHandler(new ObjectTransferHandler(TransferHandler.MOVE)
      {
         private static final long serialVersionUID = -4792770729250636640L;

         @Override
         public boolean isDragable(Object obj)
         {
            if (obj instanceof Building)
               return ((Building) obj).getParent() != null;

            return true;
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

      btnAddDevice = toolBar.add(ActionFactory.getInstance().getAction("addDevicesAction"));
      btnAddDevice.setEnabled(false);
      btnAddDevice.setIcon(ImageCache.getIcon("icons/device-new"));
      btnAddDevice.setToolTipText(I18n.getMessage("TopologyView.AddDeviceTip"));

      toolBar.add(Box.createHorizontalStrut(4));

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
      btnDelete.setToolTipText(I18n.getMessage("TopologyView.DeleteTip"));

      toolBar.add(Box.createHorizontalStrut(4));

      btnEdit = new ToolBarButton(ImageCache.getIcon("icons/configure"));
      toolBar.add(btnEdit);
      btnEdit.setEnabled(false);
      btnEdit.setToolTipText(I18n.getMessage("TopologyView.EditTip"));
      btnEdit.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent arg0)
         {
            ProjectManager.getController().edit(getSelectedObject());
         }
      });

      btnProgram = new ToolBarButton(ImageCache.getIcon("icons/launch"));
      toolBar.add(btnProgram);
      btnProgram.setEnabled(false);
      btnProgram.setToolTipText(I18n.getMessage("TopologyView.ProgramTip"));
      btnProgram.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent arg0)
         {
            ProjectManager.getController().program(getSelectedObject());
         }
      });
   }

   /**
    * Test if an object is relevant for this view. Used e.g. for event handlers.
    * 
    * @param obj - the object to test.
    * @return true if the object is relevant.
    */
   @Override
   protected boolean isRelevant(final Object obj)
   {
      return obj instanceof Device || obj instanceof Line || obj instanceof Area;
   }

   /**
    * Add an area.
    */
   protected void addArea()
   {
      ProjectManager.getController().createArea();
   }

   /**
    * Add a line to the selected area.
    */
   protected void addLine()
   {
      Object obj = getSelectedObject();
      if (obj instanceof Device)
         obj = ((Device) obj).getLine();
      if (obj instanceof Line)
         obj = ((Line) obj).getArea();
      if (obj instanceof Area)
         ProjectManager.getController().createLine((Area) obj);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void objectSelected(Object obj)
   {
      if (obj instanceof Area)
      {
         btnAddLine.setEnabled(true);
         btnAddDevice.setEnabled(false);
         btnEdit.setEnabled(true);
         btnDelete.setEnabled(true);
         btnProgram.setEnabled(false);
      }
      else if (obj instanceof Line)
      {
         btnAddLine.setEnabled(true);
         btnAddDevice.setEnabled(true);
         btnEdit.setEnabled(true);
         btnDelete.setEnabled(true);
         btnProgram.setEnabled(false);
      }
      else if (obj instanceof Device)
      {
         btnAddLine.setEnabled(true);
         btnAddDevice.setEnabled(true);
         btnEdit.setEnabled(true);
         btnDelete.setEnabled(true);
         //         btnProgram.setEnabled(((Device) obj).isProgrammingRequired());
         btnProgram.setEnabled(true);
      }
      else
      {
         btnAddLine.setEnabled(false);
         btnAddDevice.setEnabled(false);
         btnEdit.setEnabled(false);
         btnDelete.setEnabled(false);
         btnProgram.setEnabled(false);
      }
   }

   /**
    * Update the page's contents.
    */
   @Override
   public void updateContents()
   {
      final MutableIconTreeNode rootNode = getRootNode();

      rootNode.removeAllChildren();

      final Project project = ProjectManager.getProject();
      if (project == null)
         return;

      for (Area area : project.getAreas())
      {
         MutableIconTreeNode areaNode = new MutableIconTreeNode(area, true);
         areaNode.setIcon(areaIcon);
         rootNode.add(areaNode);

         for (Line line : area.getLines())
         {
            MutableIconTreeNode lineNode = new MutableIconTreeNode(line, true);
            lineNode.setIcon(lineIcon);
            areaNode.add(lineNode);

            for (Device device : line.getDevices())
            {
               MutableIconTreeNode deviceNode = new MutableIconTreeNode(device, true);
               deviceNode.setIcon(deviceIcon);
               lineNode.add(deviceNode);
            }
         }
      }

      getTreeModel().reload();
      TreeUtils.expandAll(getTree());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected boolean acceptsDrop(Object target, Transferable trans)
   {
      final List<Object> objs = getTransferableObjects(trans);

      for (final Object obj : objs)
         if (obj == target)
            return true;

      if (target instanceof Line)
      {
         for (final Object obj : objs)
            if (obj instanceof Device)
               return true;
      }
      else if (target instanceof Area)
      {
         for (final Object obj : objs)
            if (obj instanceof Line)
               return true;
      }

      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected boolean handleDrop(Object target, Transferable trans)
   {
      final List<Object> objs = getTransferableObjects(trans);
      boolean dropped = false;

      for (final Object obj : objs)
      {
         if (obj == target)
         {
            dropped = true;
            continue;
         }

         if (target instanceof Line && obj instanceof Device)
            ((Line) target).add((Device) obj);
         else if (target instanceof Area && obj instanceof Line)
            ((Area) target).add((Line) obj);
         else continue;

         ProjectManager.fireComponentModified(obj);
         ProjectManager.fireComponentModified(target);
      }

      if (dropped)
         ProjectManager.fireComponentModified(target);

      return dropped;
   }
}
