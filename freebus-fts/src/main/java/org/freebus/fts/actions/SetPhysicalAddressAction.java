package org.freebus.fts.actions;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.freebus.fts.I18n;
import org.freebus.fts.MainWindow;
import org.freebus.fts.backend.job.JobQueue;
import org.freebus.fts.backend.job.device.SetPhysicalAddressJob;
import org.freebus.fts.dialogs.SetPhysicalAddress;
import org.freebus.fts.elements.services.ImageCache;

/**
 * Set the physical address of a device on the bus that is in programming mode.
 */
public final class SetPhysicalAddressAction extends BasicAction
{
   private static final long serialVersionUID = 9116383593674035382L;

   /**
    * Create an action object.
    */
   SetPhysicalAddressAction()
   {
      super(I18n.getMessage("SetPhysicalAddressAction.Name"), I18n.getMessage("SetPhysicalAddressAction.ToolTip"), ImageCache
            .getIcon("icons/signature"));
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      final SetPhysicalAddress dlg = new SetPhysicalAddress(MainWindow.getInstance());

      dlg.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosed(WindowEvent e)
         {
            if (dlg.isAccepted())
               JobQueue.getDefaultJobQueue().add(new SetPhysicalAddressJob(dlg.getPhysicalAddress()));
         }
      });

      dlg.setVisible(true);
   }
}
