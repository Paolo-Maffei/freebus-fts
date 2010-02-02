package org.freebus.fts.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.freebus.fts.MainWindow;
import org.freebus.fts.common.SimpleConfig;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.pages.ProductsImportBrowser;
import org.freebus.fts.utils.VdxFileFilter;

/**
 * Import products from of a VD_ file into the FTS database.
 */
public final class ImportProductsAction extends BasicAction
{
   private static final long serialVersionUID = 9028068427873919014L;

   /**
    * Create an action object.
    */
   ImportProductsAction()
   {
      super(I18n.getMessage("ImportProductsAction.Name"), I18n.getMessage("ImportProductsAction.ToolTip"), ImageCache.getIcon("icons/filefind"));
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent event)
   {
      try
      {
         final SimpleConfig cfg = Config.getInstance();
         String lastDir = cfg.getStringValue("ImportProductsAction.lastDir");
   
         final JFileChooser dlg = new JFileChooser();
         dlg.setSelectedFile(new File(lastDir));
         dlg.addChoosableFileFilter(new VdxFileFilter());

         if (dlg.showOpenDialog(MainWindow.getInstance()) != JFileChooser.APPROVE_OPTION)
            return;
   
         final File file = dlg.getSelectedFile();
         if (file == null) return;
   
         cfg.put("ImportProductsAction.lastDir", file.getAbsolutePath());
         cfg.save();

         MainWindow.getInstance().addPage(new ProductsImportBrowser(), file);
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, "Failed to open products browser");
      }
   }
}
