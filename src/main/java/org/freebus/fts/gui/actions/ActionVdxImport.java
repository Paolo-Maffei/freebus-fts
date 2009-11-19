package org.freebus.fts.gui.actions;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.freebus.fts.Config;
import org.freebus.fts.dialogs.ProgressDialog;
import org.freebus.fts.gui.MainWindow;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.vdx.VdxToDb;

/**
 * Let the user select a VDX file and import it into the products
 * database.
 */
public final class ActionVdxImport extends GenericAction
{
   ActionVdxImport()
   {
      super(I18n.getMessage("ActionVdxImport_Label"), I18n.getMessage("ActionVdxImport_ToolTip"), "icons/wizard");
   }

   @Override
   public void triggered(SelectionEvent e)
   {
      final FileDialog fileDialog = new FileDialog(MainWindow.getInstance().getShell(), SWT.SINGLE);
      fileDialog.setText(I18n.getMessage("VdxToDb_Open_File"));
      fileDialog.setFilterExtensions(new String[] { "*.vd_", "*" });
      fileDialog.setFilterNames(new String[] { I18n.getMessage("FileType_vd_"), I18n.getMessage("FileType_all") });
      final String vdxDir = Config.getInstance().getVdxDir();
      if (vdxDir != null) fileDialog.setFilterPath(vdxDir);

      final String fileName = fileDialog.open();
      if (fileName == null) return;

      final Config cfg = Config.getInstance();
      cfg.setVdxDir(new File(fileName).getParentFile().getPath());
      cfg.save();

      final VdxToDb conv = new VdxToDb(fileName);
      final ProgressDialog dlg = new ProgressDialog(I18n.getMessage("VdxToDb_Title"), I18n.getMessage(
            "VdxToDb_Description").replace("%1", fileName));
      dlg.run(conv);
   }
}
