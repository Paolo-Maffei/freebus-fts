package org.freebus.fts.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.freebus.fts.MainWindow;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.pages.ProductsBrowser;
import org.freebus.fts.utils.VdxFileFilter;

/**
 * Browse the products in a VD_ file.
 */
public final class BrowseProductsVdxAction extends BasicAction
{
   private static final long serialVersionUID = -7305686940401579677L;

   /**
    * Create an action object.
    */
   BrowseProductsVdxAction()
   {
      super(I18n.getMessage("BrowseProductsVdxAction.Name"), I18n.getMessage("BrowseProductsVdxAction.ToolTip"), ImageCache.getIcon("icons/filefind"));
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
         String lastDir = cfg.getStringValue("vdxFile.lastDir");

         final JFileChooser dlg = new JFileChooser();
         dlg.setSelectedFile(new File(lastDir));
         dlg.addChoosableFileFilter(new VdxFileFilter(true, false));
         dlg.setDialogTitle(I18n.getMessage("BrowseProductsVdxAction.openFileTitle"));

         if (dlg.showOpenDialog(MainWindow.getInstance()) != JFileChooser.APPROVE_OPTION)
            return;

         final File file = dlg.getSelectedFile();
         if (file == null) return;

         cfg.put("vdxFile.lastDir", file.getAbsolutePath());
         cfg.save();

         MainWindow.getInstance().addPage(new ProductsBrowser(), file);
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, "Failed to open vdx org.freebus.fts.products browser");
      }
   }
}
