package org.freebus.fts.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;

/**
 * A {@link TabPage} that contains a tree widget and a toolbar with
 * collapse-all / expand-all buttons. 
 */
public abstract class TreeTabPage extends TabPage
{
   protected final Tree tree;

   private enum Action
   {
      CollapseAll,
      ExpandAll
   }

   /**
    * Create a new widget.
    * 
    * @param parent - the parent widget.
    */
   public TreeTabPage(Composite parent)
   {
      super(parent);

      final ToolBar toolBar = createToolBar();

      createToolItem("icons/collapse-all", SWT.PUSH, Action.CollapseAll);
      createToolItem("icons/expand-all", SWT.PUSH, Action.ExpandAll);

      tree = new Tree(this, SWT.BORDER | SWT.SINGLE);

      final FormData formData = new FormData();
      formData.top = new FormAttachment(toolBar, 0);
      formData.bottom = new FormAttachment(99);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      tree.setLayoutData(formData);
   }

   @Override
   public void widgetSelected(SelectionEvent e)
   {
      final Action action = (Action) e.widget.getData();

      if (action == Action.ExpandAll)
      {
         GuiUtils.setExpandedAll(tree, true);
      }
      else if (action == Action.CollapseAll)
      {
         GuiUtils.setExpandedAll(tree, false);
      }
   }
}
