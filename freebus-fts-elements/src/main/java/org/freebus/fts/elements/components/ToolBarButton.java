package org.freebus.fts.elements.components;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * A generic {@link JButton} that has its look&feel adapted for tool-bars. 
 */
public class ToolBarButton extends JButton
{
   private static final long serialVersionUID = -6435178562438145811L;

   /**
    * Creates an empty button.
    */
   public ToolBarButton()
   {
      super();
      useToolBarStyle(this);
   }

   /**
    * Creates a button with an icon.
    */
   public ToolBarButton(Icon icon)
   {
      super(icon);
      useToolBarStyle(this);
   }

   /**
    * Creates a button with a text.
    */
   public ToolBarButton(String text)
   {
      super(text);
      useToolBarStyle(this);
   }

   /**
    * Creates a button from an action.
    */
   public ToolBarButton(Action a)
   {
      super(a);
      useToolBarStyle(this);
   }

   /**
    * Creates a button with an icon and a text.
    */
   public ToolBarButton(String text, Icon icon)
   {
      super(text, icon);
      useToolBarStyle(this);
   }

   /**
    * Adapt the style of the button to fit for tool-bars.
    */
   public static void useToolBarStyle(JButton btn)
   {
      btn.setFocusable(false);
      btn.setBorderPainted(false);
      btn.setOpaque(false);
   }
}
