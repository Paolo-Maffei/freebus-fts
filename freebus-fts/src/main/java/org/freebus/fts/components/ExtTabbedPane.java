package org.freebus.fts.components;

import java.awt.Component;

import javax.swing.JTabbedPane;

/**
 * An extended tabbed pane widget, that adds icons and/or buttons to the tabs.
 */
public class ExtTabbedPane extends JTabbedPane
{
   private static final long serialVersionUID = 6376413536236202913L;

   public ExtTabbedPane()
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addTab(String title, Component component)
   {
      super.addTab(title, component);
      setTabComponentAt(getTabCount() - 1, new ExtTab(title, this, true));
   }
}
