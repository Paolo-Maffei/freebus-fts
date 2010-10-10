package org.freebus.fts.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.freebus.fts.I18n;
import org.freebus.fts.MainWindow;
import org.freebus.fts.core.Config;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.filter.TrxFileFilter;
import org.freebus.fts.pages.BusTraceViewer;

/**
 * Ask the user for a bus trace file and open a {@link BusTraceViewer bus trace
 * viewer} page with the contents of the file.
 */
public final class BusTraceViewerAction extends BasicAction
{
   private static final long serialVersionUID = 1904540206973007653L;

   /**
    * Create an action object.
    */
   BusTraceViewerAction()
   {
      super(I18n.getMessage("BusTraceViewerAction.Name"), I18n.getMessage("BusTraceViewerAction.ToolTip"),
            ImageCache.getIcon("icons/filefind"));
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
         String lastDir = cfg.getStringValue("busTraces.lastDir");

         final JFileChooser dlg = new JFileChooser();
         dlg.setSelectedFile(new File(lastDir));
         final FileFilter fileFilter = new TrxFileFilter();
         dlg.addChoosableFileFilter(fileFilter);
         dlg.addChoosableFileFilter(dlg.getAcceptAllFileFilter());
         dlg.setFileFilter(fileFilter);
         dlg.setDialogTitle(I18n.getMessage("BusTraceViewerAction.OpenFileTitle"));

         if (dlg.showOpenDialog(MainWindow.getInstance()) != JFileChooser.APPROVE_OPTION)
            return;

         final File file = dlg.getSelectedFile();
         if (file == null) return;

         cfg.put("busTraces.lastDir", file.getAbsolutePath());
         cfg.save();

         MainWindow.getInstance().addPage(new BusTraceViewer(), file);
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, "Failed to open bus trace viewer");
      }
   }
}
