package org.freebus.fts.gui.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.freebus.fts.Config;
import org.freebus.fts.gui.MainWindow;
import org.freebus.fts.gui.VdxBrowser;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.vdx.VdxFileReader;

/**
 * Let the user choose a vd_/VDX file and open a browser widget that shows the
 * contents of the selected file.
 */
public final class ActionVdxBrowser extends GenericAction
{
   ActionVdxBrowser()
   {
      super(I18n.getMessage("ActionVdxBrowser_Label"), I18n.getMessage("ActionVdxBrowser_ToolTip"), "icons/filefind");
   }

   @Override
   public void triggered(SelectionEvent event)
   {
      final MainWindow mainWin = MainWindow.getInstance();

      final FileDialog fileDialog = new FileDialog(mainWin.getShell(), SWT.SINGLE);
      fileDialog.setText(I18n.getMessage("Products_Browser_Open_File"));
      fileDialog.setFilterExtensions(new String[] { "*.vd*", "*" });
      fileDialog.setFilterNames(new String[] { "VDX Files", "Any" });
      final String vdxDir = Config.getInstance().getVdxDir();
      if (vdxDir != null) fileDialog.setFilterPath(vdxDir);

      final String fileName = fileDialog.open();
      if (fileName == null) return;

      final Config cfg = Config.getInstance();
      cfg.setVdxDir(new File(fileName).getParentFile().getPath());
      cfg.save();

      VdxFileReader reader = null;
      try
      {
         reader = new VdxFileReader(fileName);
      }
      catch (IOException e)
      {
         e.printStackTrace();
         MessageBox mbox = new MessageBox(mainWin.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
         mbox.setMessage(e.getMessage());
         mbox.open();
         return;
      }

      mainWin.showTabPage(VdxBrowser.class, reader);
   }

}
