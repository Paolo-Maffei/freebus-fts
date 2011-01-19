package org.freebus.fts.client.views;

import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.dragdrop.ObjectTransferHandler;
import org.freebus.fts.elements.components.ToolBar;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.elements.tree.MutableIconTreeNode;
import org.freebus.fts.elements.utils.TreeUtils;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.SubGroup;

/**
 * Shows the logical structure of the project. The main- and sub-groups with the
 * devices.
 */
public class LogicalView extends AbstractTreeView
{
   private static final long serialVersionUID = 3943935990729035854L;

   private final Icon mainGroupIcon = ImageCache.getIcon("icons/main-group");
   private final Icon midGroupIcon = ImageCache.getIcon("icons/middle-group");
   private final Icon subGroupIcon = ImageCache.getIcon("icons/sub-group");

   private JButton btnAddMainGrp, btnAddMidGrp, btnAddSubGrp, btnDelete, btnProperties;

   /**
    * Create a page that shows the topological structure of the project.
    */
   public LogicalView()
   {
      setName(I18n.getMessage("LogicalView.Title"));
      initToolBar();

      enableTreeDragDrop(true, DnDConstants.ACTION_MOVE);
      getTree().setTransferHandler(new ObjectTransferHandler(TransferHandler.MOVE)
      {
         private static final long serialVersionUID = 1L;

         @Override
         public boolean isDragable(Object obj)
         {
            return !(obj instanceof MainGroup);
         }
      });

      objectSelected(null);
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
      return obj instanceof MainGroup || obj instanceof MidGroup || obj instanceof SubGroup;
   }

   /**
    * Initialize the tool-bar.
    */
   private void initToolBar()
   {
      final JToolBar toolBar = new ToolBar();
      add(toolBar, BorderLayout.NORTH);

      btnAddMainGrp = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            addMainGrp();
         }

      });
      btnAddMainGrp.setIcon(ImageCache.getIcon("icons/main-group-new"));
      btnAddMainGrp.setToolTipText(I18n.getMessage("LogicalView.AddMainGroup"));

      btnAddMidGrp = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            addMidGrp();
         }

      });
      btnAddMidGrp.setIcon(ImageCache.getIcon("icons/middle-group-new"));
      btnAddMidGrp.setToolTipText(I18n.getMessage("LogicalView.AddMidGroup"));

      btnAddSubGrp = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            addSubGrp();
         }

      });
      btnAddSubGrp.setIcon(ImageCache.getIcon("icons/sub-group-new"));
      btnAddSubGrp.setToolTipText(I18n.getMessage("LogicalView.AddSubGroup"));

      btnProperties = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            editProperties();
         }

      });
      btnProperties.setIcon(ImageCache.getIcon("icons/edit-properties"));
      btnProperties.setToolTipText(I18n.getMessage("LogicalView.EditProperties"));

      btnDelete = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
            ProjectManager.getController().remove(getSelectedObject());
         }

      });
      btnDelete.setIcon(ImageCache.getIcon("icons/delete"));
      btnDelete.setToolTipText(I18n.getMessage("LogicalView.DeleteGroup"));

   }

   /**
    * Modify the toolbar's icons when an object is selected.
    * 
    * @param obj - the selected object.
    */
   @Override
   protected void objectSelected(Object obj)
   {
      if (obj instanceof MainGroup)
      {
         btnAddMainGrp.setEnabled(true);
         btnAddMidGrp.setEnabled(true);
         btnAddSubGrp.setEnabled(false);
         btnProperties.setEnabled(true);
         btnDelete.setEnabled(true);
      }
      else if (obj instanceof MidGroup)
      {
         btnAddMainGrp.setEnabled(true);
         btnAddMidGrp.setEnabled(true);
         btnAddSubGrp.setEnabled(true);
         btnProperties.setEnabled(true);
         btnDelete.setEnabled(true);
      }
      else if (obj instanceof SubGroup)
      {
         btnAddMainGrp.setEnabled(true);
         btnAddMidGrp.setEnabled(true);
         btnAddSubGrp.setEnabled(true);
         btnProperties.setEnabled(true);
         btnDelete.setEnabled(true);
      }
      else
      {
         btnAddMainGrp.setEnabled(true);
         btnAddMidGrp.setEnabled(false);
         btnAddSubGrp.setEnabled(false);
         btnProperties.setEnabled(false);
         btnDelete.setEnabled(false);
      }
   }

   private void addMainGrp()
   {
      // TODO Auto-generated method stub

   }

   private void addMidGrp()
   {
      // TODO Auto-generated method stub

   }

   private void addSubGrp()
   {
      // TODO Auto-generated method stub

   }

   private void editProperties()
   {
      // TODO Auto-generated method stub

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

      for (MainGroup mainGroup : project.getMainGroups())
      {
         final MutableIconTreeNode mainGroupNode = new MutableIconTreeNode(mainGroup, true);
         mainGroupNode.setIcon(mainGroupIcon);
         rootNode.add(mainGroupNode);

         for (MidGroup midGroup : mainGroup.getMidGroups())
         {
            final MutableIconTreeNode midGroupNode = new MutableIconTreeNode(midGroup, true);
            midGroupNode.setIcon(midGroupIcon);
            mainGroupNode.add(midGroupNode);

            for (SubGroup subGroup : midGroup.getSubGroups())
            {
               final MutableIconTreeNode subGroupNode = new MutableIconTreeNode(subGroup, true);
               subGroupNode.setIcon(subGroupIcon);
               midGroupNode.add(subGroupNode);
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

      if (target instanceof MidGroup)
      {
         for (final Object obj : objs)
            if (obj instanceof SubGroup)
               return true;
      }
      else if (target instanceof MainGroup)
      {
         for (final Object obj : objs)
            if (obj instanceof MidGroup)
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

         if (target instanceof MidGroup && obj instanceof SubGroup)
            ((MidGroup) target).add((SubGroup) obj);
         else if (target instanceof MainGroup && obj instanceof MidGroup)
            ((MainGroup) target).add((MidGroup) obj);
         else continue;

         ProjectManager.fireComponentModified(obj);
         ProjectManager.fireComponentModified(target);
      }

      if (dropped)
         ProjectManager.fireComponentModified(target);

      return dropped;
   }
}
