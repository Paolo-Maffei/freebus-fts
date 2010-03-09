package org.freebus.fts.components;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * A tool bar that styles its buttons for a nicer look.
 */
public class ToolBar extends JToolBar
{
   private static final long serialVersionUID = -7200515392906890824L;

   /**
    * {@inheritDoc}
    */
   public ToolBar()
   {
      this(HORIZONTAL);
   }

   /**
    * {@inheritDoc}
    */
   public ToolBar(int orientation)
   {
      this(null, orientation);
   }

   /**
    * {@inheritDoc}
    */
   public ToolBar(String name)
   {
      this(name, HORIZONTAL);
   }

   /**
    * {@inheritDoc}
    */
   public ToolBar(String name, int orientation)
   {
      super(name, orientation);

      setOpaque(false);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected JButton createActionComponent(Action a)
   {
      final JButton btn = super.createActionComponent(a);
      ToolBarButton.useToolBarStyle(btn);
      return btn;
   }
}
