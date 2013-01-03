package org.freebus.fts.client.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.freebus.fts.client.application.MainWindow;
import org.freebus.fts.client.core.Config;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.editors.inspectvdxfile.InspectVdxFile;
import org.freebus.fts.client.filters.VdxFileFilter;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.services.ImageCache;

/**
 * Inspect the contents of a VD_ file.
 */
public final class InspectVdxFileAction extends BasicAction
{
   private static final long serialVersionUID = -7311738068495580033L;

   /**
    * Create an action object.
    */
   InspectVdxFileAction()
   {
      super(I18n.getMessage("InspectVdxFileAction.Name"), I18n.getMessage("InspectVdxFileAction.ToolTip"), ImageCache.getIcon("icons/filefind"));
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
         final FileFilter fileFilter = new VdxFileFilter(true, true);
         dlg.addChoosableFileFilter(fileFilter);
         dlg.addChoosableFileFilter(dlg.getAcceptAllFileFilter());
         dlg.setFileFilter(fileFilter);
         dlg.setDialogTitle(I18n.getMessage("InspectVdxFileAction.openFileTitle"));

         if (dlg.showOpenDialog(MainWindow.getInstance()) != JFileChooser.APPROVE_OPTION)
            return;

         final File file = dlg.getSelectedFile();
         if (file == null) return;

         cfg.put("VdxFile.lastDir", file.getAbsolutePath());
         cfg.save();

         MainWindow.getInstance().showEditor(InspectVdxFile.class, file);
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, "Failed to open vd_ file inspector");
      }
   }
}
