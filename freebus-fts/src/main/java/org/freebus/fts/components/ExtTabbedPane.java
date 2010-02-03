package org.freebus.fts.components;

import java.awt.Component;

import javax.swing.JTabbedPane;

/**
 * An extended tabbed pane widget, that adds icons and/or buttons to the tabs.
 */
public class ExtTabbedPane extends JTabbedPane
{
   private static final long serialVersionUID = 6376413536236202913L;

   /**
    * Creates an empty <code>ExtTabbedPane</code> with a default tab placement
    * of <code>JTabbedPane.TOP</code>.
    * 
    * @see #addTab
    */
   public ExtTabbedPane()
   {
      super();
   }

   /**
    * Creates an empty <code>TabbedPane</code> with the specified tab placement
    * of either: <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
    * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>.
    * 
    * @param tabPlacement the placement for the tabs relative to the content
    * @see #addTab
    */
   public ExtTabbedPane(int tabPlacement)
   {
      super(tabPlacement);
   }

   /**
    * Creates an empty <code>TabbedPane</code> with the specified tab placement
    * and tab layout policy. Tab placement may be either:
    * <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
    * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>. Tab
    * layout policy may be either: <code>JTabbedPane.WRAP_TAB_LAYOUT</code> or
    * <code>JTabbedPane.SCROLL_TAB_LAYOUT</code>.
    * 
    * @param tabPlacement the placement for the tabs relative to the content
    * @param tabLayoutPolicy the policy for laying out tabs when all tabs will
    *           not fit on one run
    * @exception IllegalArgumentException if tab placement or tab layout policy
    *               are not one of the above supported values
    * @see #addTab
    * @since 1.4
    */
   public ExtTabbedPane(int tabPlacement, int tabLayoutPolicy)
   {
      super(tabPlacement, tabLayoutPolicy);
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
