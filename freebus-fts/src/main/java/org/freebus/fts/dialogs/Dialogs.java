package org.freebus.fts.dialogs;

import java.awt.Cursor;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
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
   public static void showExceptionDialog(Exception e, String message)
   {
      Logger.getRootLogger().warn(message, e);
//      e.printStackTrace();

      message = "\n" + I18n.getMessage("Dialogs.Exception_Caption") + ":\n\n" + message + "\n\n" + e.getMessage()
            + "\n\n";

      showErrorDialog(I18n.getMessage("Dialogs.Exception_Title"), message);
   }

   /**
    * Show an error dialog.
    * 
    * @param title - The title of the dialog
    * @param message - Some human readable message (not
    *           {@link Exception#getMessage}!)
    */
   public static void showErrorDialog(String title, String message)
   {
      final MainWindow mainWin = MainWindow.getInstance();

      mainWin.setCursor(Cursor.getDefaultCursor());

      JOptionPane.showMessageDialog(mainWin, message, title, JOptionPane.ERROR_MESSAGE, ImageCache
            .getIcon("icons-large/error-dialog"));
   }

   /**
    * Show an error dialog with the default error title.
    * 
    * @param message - Some human readable message (not
    *           {@link Exception#getMessage}!)
    */
   public static void showErrorDialog(String message)
   {
      showErrorDialog(I18n.getMessage("Dialogs.Error_Title"), message);
   }
}
