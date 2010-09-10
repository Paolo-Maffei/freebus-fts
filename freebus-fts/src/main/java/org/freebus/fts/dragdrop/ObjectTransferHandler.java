package org.freebus.fts.dragdrop;

import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class ObjectTransferHandler extends TransferHandler
{
   private static final long serialVersionUID = -3207689686622973309L;

   public ObjectTransferHandler()
   {
   }

   public Transferable createTransferable(JComponent c)
   {
      List<Object> objs;

      if (c instanceof JList)
      {
         Object[] values = ((JList) c).getSelectedValues();
         objs = Arrays.asList(values);
      }
      else if (c instanceof JTree)
      {
         final JTree tree = (JTree) c;
         objs = new Vector<Object>();
         for (TreePath path : tree.getSelectionPaths())
         {
            final Object n = path.getLastPathComponent();
            if (n instanceof DefaultMutableTreeNode)
               objs.add(((DefaultMutableTreeNode) n).getUserObject());
         }
      }
      else throw new RuntimeException("Component type not supported: " + c.getClass().getName());
      
      return new TransferableObject(objs);
   }

   public int getSourceActions(JComponent c)
   {
      return COPY;
   }
}
