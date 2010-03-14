package org.freebus.fts.actions;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

/**
 * The actions of FTS.
 */
public enum Actions
{
   /** Open a org.freebus.fts.products browser that allows adding devices to the current project. */
   ADD_DEVICES(new AddDevicesAction()),

   /** Browse the contents of a VD_ org.freebus.fts.products file. */
   BROWSE_PRODUCTS_VDX(new BrowseProductsVdxAction()),

   /** Show the bus-monitor. */
   BUS_MONITOR(new BusMonitorAction()),

   /** Read the device status. */
   DEVICE_STATUS(new DeviceStatusAction()),

   /** Exit the application. */
   EXIT(new ExitAction()),

   /** Import org.freebus.fts.products from of a VD_ file into the FTS database. */
   IMPORT_PRODUCTS(new ImportProductsAction()),

   /** Inspect the contents of a VD_ file. */
   INSPECT_VDX_FILE(new InspectVdxFileAction()),

   /** Open the logical structure of the project. */
   LOGICAL_VIEW(new LogicalViewAction()),

   /** Create a new project. */
   NEW_PROJECT(new NewProjectAction()),

   /** Open a project. */
   OPEN_PROJECT(new OpenProjectAction()),

   /** Open the physical structure of the project. */
   PHYSICAL_VIEW(new PhysicalViewAction()),

   /** Show the properties of the current project. */
   PROJECT_PROPERTIES(new ProjectPropertiesAction()),

   /** Restart the application. */
   RESTART(new RestartAction()),

   /** Save the current project. */
   SAVE_PROJECT(new SaveProjectAction()),

   /** Send a test telegram to the KNX/EIB bus. */
   SEND_TEST_TELEGRAM(new SendTestTelegramAction()),

   /** Open a dialog for setting the physical address of a device on the bus. */
   SET_PHYSICAL_ADDRESS(new SetPhysicalAddressAction()),

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
    * @return The created menu item.
    */
   public JMenuItem addTo(JMenu menu)
   {
      return menu.add(action);
   }

   /**
    * Add the action to the tool bar.
    *
    * @param toolBar - the tool bar to which the action is added.
    * @return The created tool bar button.
    */
   public JButton addTo(JToolBar toolBar)
   {
      final JButton btn = toolBar.add(action);

      if (action instanceof BasicAction)
         btn.setToolTipText(((BasicAction) action).getToolTipText());

      return btn;
   }

   /*
    * Internal constructor.
    */
   private Actions(Action action)
   {
      this.action = action;
   }
}
