package org.freebus.fts.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

/**
 * An abstract action class that extracts the mnemonic from the action's name.
 * E.g. "E&xit" will result in the name "Exit" and the mnemonic "x".
 */
public abstract class BasicAction extends AbstractAction
{
   private static final long serialVersionUID = -4596730218025617438L;
   private String toolTipText;

   /**
    * Create an action with the given name, icon, and tool-tip text.
    *
    * @param name - The name of the action. A "&" marks the mnemonic.
    * @param toolTipText - The text for the tool-tip.
    * @param icon - The icon for the action.
    */
   public BasicAction(String name, String toolTipText, Icon icon)
   {
      super();

      this.setToolTipText(toolTipText);

      final int idx = name.indexOf('&');
      if (idx >= 0)
      {
         putValue(Action.MNEMONIC_KEY, (int)name.codePointAt(idx + 1));
         name = name.substring(0, idx) + name.substring(idx + 1);
      }

      putValue(Action.NAME, name);
      if (icon != null) putValue(Action.SMALL_ICON, icon);
   }

   /**
    * Create an action with the given name and icon.
    *
    * @param name - The name of the action. A "&" marks the mnemonic.
    * @param icon - The icon for the action.
    */
   public BasicAction(String name, Icon icon)
   {
      this(name, null, icon);
   }

   /**
    * Create an action with the given name.
    *
    * @param name - The name of the action. A "&" marks the mnemonic.
    */
   public BasicAction(String name)
   {
      this(name, null);
   }

   /**
    * Create an unnamed action.
    */
   public BasicAction()
   {
      super();
   }

   /**
    * Set the tool-tip text.
    *
    * @param toolTipText - The text for the tool-tip.
    */
   public void setToolTipText(String toolTipText)
   {
      this.toolTipText = toolTipText;
   }

   /**
    * @return The text for the tool-tip.
    */
   public String getToolTipText()
   {
      return toolTipText;
   }

   /**
    * Perform the action.
    */
   public void perform()
   {
      actionPerformed(new ActionEvent(this, 1, ""));
   }
}
