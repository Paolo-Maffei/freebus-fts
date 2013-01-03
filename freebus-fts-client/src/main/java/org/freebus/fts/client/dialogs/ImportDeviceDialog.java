package org.freebus.fts.client.dialogs;

import java.awt.Window;

import org.freebus.fts.elements.components.Dialog;
import org.freebus.fts.service.job.entity.DeviceInfo;

/**
 * A dialog for importing a device that exists on the bus to the
 * project.
 */
public class ImportDeviceDialog extends Dialog
{
   private static final long serialVersionUID = -4086624009857767396L;

   /**
    * Create a dialog for importing a device that exists on the bus 
    * to the project.
    * 
    * @param deviceInfo - information about the device to import.
    * @param owner - the owning window.
    */
   public ImportDeviceDialog(DeviceInfo deviceInfo, Window owner)
   {
      super(owner, ModalityType.APPLICATION_MODAL);
   }
}
