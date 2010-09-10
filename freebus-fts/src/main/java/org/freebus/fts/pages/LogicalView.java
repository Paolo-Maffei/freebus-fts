package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.PagePosition;
import org.freebus.fts.components.ToolBar;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.dragdrop.ObjectTransferHandler;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.SubGroup;
import org.freebus.fts.project.service.ProjectListener;
import org.freebus.fts.renderers.DynamicIconTreeCellRenderer;
import org.freebus.fts.utils.TreeUtils;

/**
 * Shows the logical structure of the project. The main- and sub-groups with the
 * devices.
 */
public class LogicalView extends AbstractPage
{
   private static final long serialVersionUID = 4775024754983180786L;

   private final JTree tree;
   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Project");
   private final JScrollPane treeView;
   private JButton btnAddMainGrp, btnAddMidGrp, btnAddSubGrp, btnDelete, btnProperties;
   private Object selectedObject;

   /**
    * Create a page that shows the topological structure of the project.
    */
   public LogicalView()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("LogicalView.Title"));

      tree = new JTree(rootNode);
      tree.setRootVisible(false);
      tree.setDragEnabled(true);
      tree.setTransferHandler(new ObjectTransferHandler());

      final DynamicIconTreeCellRenderer renderer = new DynamicIconTreeCellRenderer();
      tree.setCellRenderer(renderer);
      renderer.setCellTypeIcon(MainGroup.class, ImageCache.getIcon("icons/main-group"));
      renderer.setCellTypeIcon(MidGroup.class, ImageCache.getIcon("icons/middle-group"));
      renderer.setCellTypeIcon(SubGroup.class, ImageCache.getIcon("icons/sub-group"));

      treeView = new JScrollPane(tree);
      add(treeView, BorderLayout.CENTER);

      initToolBar();

      tree.addTreeSelectionListener(new TreeSelectionListener()
      {
         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            final Object selectedObject = node != null ? node.getUserObject() : null;

            setIconEnabledFromSelected(selectedObject);
         }
      });

      tree.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mousePressed(MouseEvent e)
         {
            final JComponent component = (JComponent) e.getSource();
            final TransferHandler handler = component.getTransferHandler();
            handler.exportAsDrag(component, e, TransferHandler.COPY);
         }

         @Override
         public void mouseClicked(MouseEvent e)
         {
            if (e.getClickCount() == 2)
            {
               ProjectManager.getController().edit(selectedObject);
               e.consume();
            }
         }
      });

      ProjectManager.addListener(projectListener);

      setIconEnabledFromSelected(null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void closeEvent()
   {
      ProjectManager.removeListener(projectListener);
   }

   /*
    * Listener for project changes
    */
   private final ProjectListener projectListener = new ProjectListener()
   {
      @Override
      public void projectComponentRemoved(Object obj)
      {
         if (isRelevant(obj))
            updateContents();
      }

      @Override
      public void projectComponentModified(Object obj)
      {
         if (isRelevant(obj))
            tree.updateUI();
      }

      @Override
      public void projectComponentAdded(Object obj)
      {
         if (isRelevant(obj))
            updateContents();
      }

      @Override
      public void projectChanged(Project project)
      {
         updateContents();
      }
   };

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
            deleteGrp();
         }

      });
      btnDelete.setIcon(ImageCache.getIcon("icons/delete"));
      btnDelete.setToolTipText(I18n.getMessage("LogicalView.DeleteGroup"));

   }

   /**
    * Test if an object is relevant for this view. Used e.g. for event handlers.
    * 
    * @param obj - the object to test.
    * @return true if the object is relevant.
    */
   private boolean isRelevant(final Object obj)
   {
      return obj instanceof MainGroup || obj instanceof MidGroup || obj instanceof SubGroup;
   }

   /**
    * Modify the toolbar's icons when an object is selected.
    * 
    * @param userObject - the selected object.
    */
   private void setIconEnabledFromSelected(Object userObject)
   {
      if (userObject instanceof MainGroup)
      {
         btnAddMainGrp.setEnabled(true);
         btnAddMidGrp.setEnabled(true);
         btnAddSubGrp.setEnabled(false);
         btnProperties.setEnabled(true);
         btnDelete.setEnabled(true);
      }
      else if (userObject instanceof MidGroup)
      {
         btnAddMainGrp.setEnabled(true);
         btnAddMidGrp.setEnabled(true);
         btnAddSubGrp.setEnabled(true);
         btnProperties.setEnabled(true);
         btnDelete.setEnabled(true);
      }
      else if (userObject instanceof SubGroup)
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

   private void deleteGrp()
   {
      // TODO Auto-generated method stub

   }

   private void editProperties()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @return the user-object of the currently selected tree node, or null if
    *         nothing is selected.
    */
   public Object getSelectedObject()
   {
      return selectedObject;
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
      if (project == null)
         return;

      for (MainGroup mainGroup : project.getMainGroups())
      {
         final DefaultMutableTreeNode areaNode = new DefaultMutableTreeNode(mainGroup, true);
         rootNode.add(areaNode);

         for (MidGroup midGroup : mainGroup.getMidGroups())
         {
            final DefaultMutableTreeNode lineNode = new DefaultMutableTreeNode(midGroup, true);
            areaNode.add(lineNode);

            for (SubGroup group : midGroup.getSubGroups())
            {
               final DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode(group, true);
               lineNode.add(deviceNode);
            }
         }
      }

      ((DefaultTreeModel) tree.getModel()).reload();
      TreeUtils.expandAll(tree);
   }
}
