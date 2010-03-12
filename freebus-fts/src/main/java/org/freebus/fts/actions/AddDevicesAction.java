package org.freebus.fts.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.MainWindow;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.pages.AddVirtualDeviceBrowser;

/**
 * Open a org.freebus.fts.products browser that allows adding devices to the current project.
 */
public final class AddDevicesAction extends BasicAction
{
   private static final long serialVersionUID = -4510414190308875323L;

   /**
    * Create an action object.
    */
   AddDevicesAction()
   {
      super(I18n.getMessage("AddDevicesAction.Name"), I18n.getMessage("AddDevicesAction.ToolTip"), ImageCache.getIcon("icons/device-new"));
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent event)
   {
      final MainWindow mainWin = MainWindow.getInstance();
      final AddVirtualDeviceBrowser page;

      mainWin.showUniquePage(AddVirtualDeviceBrowser.class, null);
      page = (AddVirtualDeviceBrowser) mainWin.getUniquePage(AddVirtualDeviceBrowser.class);
      page.setProjectController(mainWin);
   }
}
