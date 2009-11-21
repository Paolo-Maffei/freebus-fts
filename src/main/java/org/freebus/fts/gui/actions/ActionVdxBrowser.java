package org.freebus.fts.gui.actions;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.freebus.fts.Config;
import org.freebus.fts.dialogs.ExceptionDialog;
import org.freebus.fts.gui.MainWindow;
import org.freebus.fts.gui.ProductsTab;
import org.freebus.fts.products.Products;
import org.freebus.fts.utils.I18n;

/**
 * Open a widget for browsing a VDX products database file.
 */
public final class ActionVdxBrowser extends GenericAction
{
   ActionVdxBrowser()
   {
      super(I18n.getMessage("ActionVdxBrowser_Label"), I18n.getMessage("ActionVdxBrowser_ToolTip"),
            "icons/contents");
   }

   @Override
   public void triggered(SelectionEvent event)
   {
      final FileDialog fileDialog = new FileDialog(MainWindow.getInstance().getShell(), SWT.SINGLE);
      fileDialog.setText(I18n.getMessage("ActionVdxBrowser_Open_File"));
      fileDialog.setFilterExtensions(new String[] { "*.vd_", "*" });
      fileDialog.setFilterNames(new String[] { I18n.getMessage("FileType_vd_"), I18n.getMessage("FileType_all") });
      final String vdxDir = Config.getInstance().getVdxDir();
      if (vdxDir != null) fileDialog.setFilterPath(vdxDir);

      final String fileName = fileDialog.open();
      if (fileName == null) return;

      final Config cfg = Config.getInstance();
      cfg.setVdxDir(new File(fileName).getParentFile().getPath());
      cfg.save();

      final MainWindow mainWin = MainWindow.getInstance();

      try
      {
         final File file = new File(fileName);
         mainWin.showTabPage(ProductsTab.class, Products.getDAOFactory(fileName)).setTitle(file.getName());
      }
      catch (Exception e)
      {
         new ExceptionDialog(e);
         return;
      }
   }

}
