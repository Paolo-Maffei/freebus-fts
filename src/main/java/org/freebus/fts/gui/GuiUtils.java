package org.freebus.fts.gui;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Some utility GUI methods.
 */
public final class GuiUtils
{
   /**
    * Set expansion state for all tree-items of the tree.
    */
   public static void setExpandedAll(Tree tree, boolean expanded)
   {
      for (TreeItem treeItem: tree.getItems())
         setExpandedAllRecursive(treeItem, expanded);
   }

   // Internal recursive worker of setExpandAll
   private static void setExpandedAllRecursive(TreeItem item, boolean expanded)
   {
      item.setExpanded(expanded);
      for (TreeItem treeItem: item.getItems())
         setExpandedAllRecursive(treeItem, expanded);
   }

}
