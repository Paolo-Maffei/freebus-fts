package org.freebus.fts.client.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.freebus.fts.client.MainWindow;
import org.freebus.fts.client.core.Config;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.filter.VdxFileFilter;
import org.freebus.fts.client.pages.productsbrowser.ProductsImportBrowser;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.services.ImageCache;

/**
 * Import org.freebus.fts.products from of a VD_ file into the FTS database.
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
         final Config cfg = Config.getInstance();
         String lastDir = cfg.getStringValue("VdxFile.lastDir");

         final JFileChooser dlg = new JFileChooser();
         dlg.setSelectedFile(new File(lastDir));
         final FileFilter fileFilter = new VdxFileFilter(true, false);
         dlg.addChoosableFileFilter(fileFilter);
         dlg.addChoosableFileFilter(dlg.getAcceptAllFileFilter());
         dlg.setFileFilter(fileFilter);
         dlg.setDialogTitle(I18n.getMessage("ImportProductsAction.openFileTitle"));

         if (dlg.showOpenDialog(MainWindow.getInstance()) != JFileChooser.APPROVE_OPTION)
            return;

         final File file = dlg.getSelectedFile();
         if (file == null) return;

         cfg.put("VdxFile.lastDir", file.getAbsolutePath());
         cfg.save();

         MainWindow.getInstance().addPage(new ProductsImportBrowser(), file);
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, "Failed to open org.freebus.fts.products browser");
      }
   }
}
