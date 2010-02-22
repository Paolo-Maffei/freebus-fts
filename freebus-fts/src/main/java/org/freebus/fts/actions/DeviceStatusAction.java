package org.freebus.fts.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.MainWindow;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.pages.DeviceStatus;

/**
 * Open Device Status
 * 
 */
public final class DeviceStatusAction extends BasicAction
{

   private static final long serialVersionUID = 4019228614916935274L;

   /**
    * Create an action object.
    */

   public DeviceStatusAction()
   {
      super(I18n.getMessage("DeviceStatusAction.Name"), I18n.getMessage("DeviceStatusAction.ToolTip"), ImageCache
            .getIcon("icons/info"));

   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      MainWindow.getInstance().showUniquePage(DeviceStatus.class, null);
   }

}
