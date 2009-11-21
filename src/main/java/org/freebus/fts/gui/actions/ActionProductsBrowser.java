package org.freebus.fts.gui.actions;

import org.eclipse.swt.events.SelectionEvent;
import org.freebus.fts.dialogs.ExceptionDialog;
import org.freebus.fts.gui.MainWindow;
import org.freebus.fts.gui.ProductsTab;
import org.freebus.fts.products.Products;
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
         mainWin.showTabPage(ProductsTab.class, Products.getDAOFactory());
      }
      catch (Exception e)
      {
         new ExceptionDialog(e);
         return;
      }
   }

}
