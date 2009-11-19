package org.freebus.fts.gui.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;
import org.freebus.fts.db.DatabaseProductDb;
import org.freebus.fts.gui.MainWindow;
import org.freebus.fts.gui.ProductsTab;
import org.freebus.fts.utils.I18n;

/**
 * Open a widget for browsing the products database.
 */
public final class ActionProductsBrowser extends GenericAction
{
   ActionProductsBrowser()
   {
      super(I18n.getMessage("ActionProductsBrowser_Label"), I18n.getMessage("ActionProductsBrowser_ToolTip"),
            "icons/contents");
   }

   @Override
   public void triggered(SelectionEvent event)
   {
      final MainWindow mainWin = MainWindow.getInstance();

      try
      {
         mainWin.showTabPage(ProductsTab.class, new DatabaseProductDb());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         MessageBox mbox = new MessageBox(mainWin.getShell(), SWT.ICON_ERROR | SWT.OK);
         mbox.setMessage(e.getLocalizedMessage());
         mbox.open();
         return;
      }
   }

}
