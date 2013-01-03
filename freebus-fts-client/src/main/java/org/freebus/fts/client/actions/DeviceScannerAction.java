package org.freebus.fts.client.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.client.application.MainWindow;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.editors.scanner.Scanner;
import org.freebus.fts.elements.services.ImageCache;

/**
 * Open the device scanner page.
 */
public final class DeviceScannerAction extends BasicAction
{
   private static final long serialVersionUID = -5586119497744368708L;

   /**
    * Create an action object.
    */
   DeviceScannerAction()
   {
      super(I18n.getMessage("DeviceScannerAction.Name"), I18n.getMessage("DeviceScannerAction.ToolTip"), ImageCache.getIcon("icons/find"));
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      MainWindow.getInstance().showUniquePanel(Scanner.class);
   }
}
