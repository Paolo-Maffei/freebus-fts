package org.freebus.fts.actions;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JToolBar;

/**
 * The actions of FTS.
 */
public enum Actions
{
   /** Browse the contents of a VD_ products file. */
   BROWSE_PRODUCTS_VDX(new BrowseProductsVdxAction()),
   
   /** Show the bus-monitor. */
   BUS_MONITOR(new BusMonitorAction()),

   /** Exit the application. */
   EXIT(new ExitAction()),

   /** Open the settings dialog. */
   SETTINGS(new SettingsAction()),

   /** Open the topological structure of the project. */
   TOPOLOGY_VIEW(new TopologyViewAction());

   /** The action object. */
   public final Action action;

   /**
    * Add the action to the given menu.
    *
    * @param menu - the menu to which the action is added.
    */
   public void addTo(JMenu menu)
   {
      menu.add(action);
   }

   /**
    * Add the action to the tool bar.
    *
    * @param toolBar - the tool bar to which the action is added.
    */
   public void addTo(JToolBar toolBar)
   {
      JButton btn = toolBar.add(action);

      if (action instanceof BasicAction)
      {
         btn.setToolTipText(((BasicAction) action).getToolTipText());
      }
   }

   /*
    * Internal constructor.
    */
   private Actions(Action action)
   {
      this.action = action;
   }
}
