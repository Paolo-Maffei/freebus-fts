package org.freebus.fts.view;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.PagePosition;
import org.freebus.fts.dragdrop.ObjectTransferHandler;
import org.freebus.fts.dragdrop.TransferableObject;
import org.freebus.fts.elements.tree.IconTreeCellRenderer;
import org.freebus.fts.elements.tree.MutableIconTreeNode;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectListener;

public abstract class AbstractTreeView extends AbstractPage
{
   private static final long serialVersionUID = -1839540884682454863L;

   private final MutableIconTreeNode rootNode = new MutableIconTreeNode("/");
   private final JTree tree = new JTree(rootNode);
   private final JScrollPane treeView = new JScrollPane(tree);
   private Object selectedObject;

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
         {
            updateContents();
            setSelectedObject(obj);
         }
      }

      @Override
      public void projectComponentAdded(Object obj)
      {
         if (isRelevant(obj))
         {
            updateContents();
            setSelectedObject(obj);
         }
      }

      @Override
      public void projectChanged(Project project)
      {
         updateContents();
      }
   };

   /**
    * Create an abstract tree view.
    */
   public AbstractTreeView()
   {
      setLayout(new BorderLayout());

      tree.setRootVisible(false);
      tree.setCellRenderer(new IconTreeCellRenderer());

      add(treeView, BorderLayout.CENTER);

      tree.addMouseListener(new MouseAdapter()
      {
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

      tree.addTreeSelectionListener(new TreeSelectionListener()
      {
         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            selectedObject = node != null ? node.getUserObject() : null;
            objectSelected(selectedObject);
         }
      });

      ProjectManager.addListener(projectListener);
   }

   /**
    * Enable drag & drop in the tree. Call this method once from the constructor
    * if you want drag & drop functionality.
    * <p>
    * If the tree is enabled as a drop target, you have to implement
    * {@link #handleDrop(Object, Transferable)}, and you should implement
    * {@link #acceptsDrop(Object, Transferable)}.
    * 
    * @param dropTarget - shall the tree be a drop target.
    * @param dragOperation - the default drag operation.
    */
   protected final void enableTreeDragDrop(final boolean dropTarget, final int dragOperation)
   {
      tree.setDragEnabled(true);
      tree.setTransferHandler(new ObjectTransferHandler());

      if (dropTarget)
      {
         tree.setDropTarget(new DropTarget(this, new DropTargetAdapter()
         {
            @Override
            public void dragEnter(DropTargetDragEvent e)
            {
               dragOver(e);
            }

            @Override
            public void dragOver(DropTargetDragEvent e)
            {
               if (acceptsDrop(getObjectAt(e.getLocation()), e.getTransferable()))
                  e.acceptDrag(dragOperation);
               else e.rejectDrag();
            }

            @Override
            public void drop(DropTargetDropEvent e)
            {
               if (handleDrop(getObjectAt(e.getLocation()), e.getTransferable()))
                  e.acceptDrop(dragOperation);
               else e.rejectDrop();
            }
         }));
      }
   }

   /**
    * Get the objects that the transferable contains. The objects fetched are
    * expected to be from the flavor {@link TransferableObject#objectFlavor}.
    * 
    * @param trans - the transferable to get the objects from.
    * @return The list of objects, or an empty list in case of an error.
    */
   public List<Object> getTransferableObjects(final Transferable trans)
   {
      try
      {
         @SuppressWarnings("unchecked")
         final List<Object> objs = (List<Object>) trans.getTransferData(TransferableObject.objectFlavor);

         return objs;
      }
      catch (UnsupportedFlavorException e)
      {
         Logger.getLogger(getClass()).error(e);
      }
      catch (IOException e)
      {
         Logger.getLogger(getClass()).error(e);
      }

      return new LinkedList<Object>();
   }

   /**
    * @return The root node of the tree.
    */
   public MutableIconTreeNode getRootNode()
   {
      return rootNode;
   }

   /**
    * @return The tree.
    */
   public JTree getTree()
   {
      return tree;
   }

   /**
    * @return The tree model.
    */
   public DefaultTreeModel getTreeModel()
   {
      return (DefaultTreeModel) tree.getModel();
   }

   /**
    * @return The tree view that contains the tree.
    */
   public JScrollPane getTreeView()
   {
      return treeView;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void closeEvent()
   {
      ProjectManager.removeListener(projectListener);
   }

   /**
    * Test if an object is relevant for this view. Used e.g. for event handlers.
    * 
    * @param obj - the object to test.
    * @return true if the object is relevant.
    */
   protected abstract boolean isRelevant(final Object obj);

   /**
    * Called when a node in the tree is selected.
    * 
    * @param obj - the selected user object of the selected tree node.
    */
   protected void objectSelected(Object obj)
   {
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
    * Get the user object of the tree node that is closest to the position.
    * 
    * @param pos - the position to search the object for.
    * @return The user object, or null if nothing was found.
    */
   public Object getObjectAt(Point pos)
   {
      final TreePath path = tree.getClosestPathForLocation(pos.x, pos.y);
      return ((DefaultMutableTreeNode) path.getPathComponent(path.getPathCount() - 1)).getUserObject();
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
    * Set the selected object.
    * 
    * @param obj - the object to select.
    */
   public void setSelectedObject(Object obj)
   {
      tree.getSelectionModel().clearSelection();

      @SuppressWarnings("unchecked")
      final Enumeration<DefaultMutableTreeNode> nodes = rootNode.breadthFirstEnumeration();

      while (nodes.hasMoreElements())
      {
         final DefaultMutableTreeNode node = nodes.nextElement();
         final Object nodeObj = node.getUserObject();

         if (nodeObj == obj)
         {
            tree.getSelectionModel().setSelectionPath(new TreePath(node.getPath()));
            break;
         }
      }
   }

   /**
    * Test if the target accepts at least one object to be dropped onto it.
    * 
    * @param target - the target of the drop.
    * @param trans - the transferable.
    * @return True if at least one object can be dropped onto the target.
    * 
    * @see #getTransferableObjects(Transferable)
    */
   protected boolean acceptsDrop(Object target, Transferable trans)
   {
      return true;
   }

   /**
    * Handle the drop of one or more objects.
    * 
    * @param target - the target of the drop.
    * @param trans - the transferable.
    * @return True if at least one object could be dropped onto the target.
    * 
    * @see #getTransferableObjects(Transferable)
    */
   protected boolean handleDrop(Object target, Transferable trans)
   {
      return false;
   }
}
