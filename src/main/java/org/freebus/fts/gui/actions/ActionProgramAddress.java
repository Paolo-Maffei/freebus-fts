package org.freebus.fts.gui.actions;

import org.eclipse.swt.events.SelectionEvent;
import org.freebus.fts.dialogs.PhysicalAddressProgrammer;
import org.freebus.fts.utils.I18n;

/**
 * Open a dialog that allows direct setting of the physical address of a device
 * on the KNX/EIB bus.
 */
public final class ActionProgramAddress extends GenericAction
{
   ActionProgramAddress()
   {
      super(I18n.getMessage("ActionProgramAddress_Label"), I18n.getMessage("ActionProgramAddress_ToolTip"),
            "icons/configure_toolbars");
   }

   @Override
   public void triggered(SelectionEvent event)
   {
      final PhysicalAddressProgrammer dlg = new PhysicalAddressProgrammer();
      dlg.open();
   }

}
