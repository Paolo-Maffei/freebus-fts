package org.freebus.fts.gui.actions;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


/**
 * All common GUI actions.
 */
public enum Action
{
   /**
    * Open the bus-monitor widget.
    */
   BUS_MONITOR(new ActionBusMonitor()),

   /**
    * Exit the application.
    */
   EXIT(new ActionExit()),

   /**
    * Open the products-database browser.
    */
   PRODUCTS_BROWSER(new ActionProductsBrowser()),

   /**
    * Open a dialog that allows direct setting of the physical address of a device
    * on the KNX/EIB bus.
    */
   PROGRAM_ADDRESS(new ActionProgramAddress()),

   /**
    * Create a new project.
    */
   PROJECT_NEW(new ActionProjectNew()),

   /**
    * Open an existing project.
    */
   PROJECT_OPEN(new ActionProjectOpen()),

   /**
    * Save the project.
    */
   PROJECT_SAVE(new ActionProjectSave()),

   /**
    * Open the settings dialog.
    */
   SETTINGS(new ActionSettings()),

   /**
    * Inspect a VDX file.
    */
   VDX_BROWSER(new ActionVdxBrowser()),

   /**
    * Import a VDX file.
    */
   VDX_IMPORT(new ActionVdxImport());


   /**
    * The action object.
    */
   public final GenericAction obj;

   /**
    * Create a button and attach this action to it.
    * Calls {@link GenericAction#newButton}.
    * 
    * @return the created button.
    */
   public final Button newButton(Composite parent, int style)
   {
      return obj.newButton(parent, style);
   }

   /**
    * Create a menu-item and attach this action to it.
    * Calls {@link GenericAction#newButton}.
    * 
    * @return the created menu-item.
    */
   public final MenuItem newMenuItem(Menu parent, int style)
   {
      return obj.newMenuItem(parent, style);
   }

   /**
    * Create a tool-item and attach this action to it.
    * Calls {@link GenericAction#newToolItem}.
    * 
    * @return the created tool-item.
    */
   public final ToolItem newToolItem(ToolBar parent, int style)
   {
      return obj.newToolItem(parent, style);
   }

   /*
    * Internal constructor
    */
   Action(GenericAction obj)
   {
      this.obj = obj;
   }
}
