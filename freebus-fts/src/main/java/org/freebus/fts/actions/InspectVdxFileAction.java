package org.freebus.fts.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.freebus.fts.MainWindow;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.pages.InspectVdxFile;
import org.freebus.fts.utils.VdxFileFilter;

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
         dlg.addChoosableFileFilter(new VdxFileFilter(true, true));
         dlg.setDialogTitle(I18n.getMessage("InspectVdxFileAction.openFileTitle"));

         if (dlg.showOpenDialog(MainWindow.getInstance()) != JFileChooser.APPROVE_OPTION)
            return;

         final File file = dlg.getSelectedFile();
         if (file == null) return;

         cfg.put("VdxFile.lastDir", file.getAbsolutePath());
         cfg.save();

         MainWindow.getInstance().addPage(new InspectVdxFile(), file);
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, "Failed to open vd_ file inspector");
      }
   }
}
