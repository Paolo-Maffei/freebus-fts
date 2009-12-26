package org.freebus.fts.dialogs;

import java.awt.Cursor;

import javax.swing.JOptionPane;

import org.freebus.fts.MainWindow;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;

/**
 * Helper methods for showing custom dialogs.
 */
public final class Dialogs
{
   /**
    * Show an exception dialog.
    * 
    * @param e - the exception.
    * @param message - some human readable message (not
    *           {@link Exception#getMessage}!)
    */
   static public void showExceptionDialog(Exception e, String message)
   {
      final MainWindow mainWin = MainWindow.getInstance();

      e.printStackTrace();

      message = "\n" + I18n.getMessage("Dialogs.Exception_Caption") + ":\n\n" + message + "\n\n" + e.getMessage()
            + "\n\n";

      mainWin.setCursor(Cursor.getDefaultCursor());

      JOptionPane.showMessageDialog(mainWin, message, I18n.getMessage("Dialogs.Exception_Title"),
            JOptionPane.ERROR_MESSAGE, ImageCache.getIcon("icons-large/error-dialog"));
   }
}
