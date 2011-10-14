package org.freebus.fts.elements.components;

import java.awt.Cursor;

import javax.swing.JOptionPane;

import org.freebus.fts.elements.AbstractApplicationWindow;
import org.freebus.fts.elements.audio.AudioClip;
import org.freebus.fts.elements.internal.I18n;
import org.freebus.fts.elements.services.ImageCache;
import org.slf4j.LoggerFactory;

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
      LoggerFactory.getLogger(Dialogs.class).warn(message, e);

      final String fmtMessage = formatExceptionMessage(e, message);
      showErrorDialog(I18n.getMessage("Dialogs.Exception_Title"), fmtMessage);
   }

   /**
    * Show an error dialog.
    *
    * @param title - The title of the dialog window
    * @param message - Some human readable message (not
    *           {@link Exception#getMessage}!)
    */
   public static void showErrorDialog(String title, String message)
   {
      final AbstractApplicationWindow appWin = AbstractApplicationWindow.getInstance();

      if (appWin != null)
         appWin.setCursor(Cursor.getDefaultCursor());

      if (!message.startsWith("<html>"))
         message = "<html><body width=\"400px\">" + message.replace("\n", "<br>") + "</body></html>";

      final AudioClip clip = new AudioClip("fake_monkey_chatter_short");
      try
      {
         clip.play();
      }
      catch (Exception e)
      {
      }

      JOptionPane.showMessageDialog(appWin, message, title, JOptionPane.ERROR_MESSAGE,
            ImageCache.getIcon("error-dialog"));
   }

   /**
    * Show an error dialog with the default error-dialog window title.
    *
    * @param message - Some human readable message (not
    *           {@link Exception#getMessage()}!)
    *
    * @see #showExceptionDialog(Exception, String)
    */
   public static void showErrorDialog(String message)
   {
      showErrorDialog(I18n.getMessage("Dialogs.Error_Title"), message);
   }

   /**
    * Format the exception and the message as it is used in
    * {@link #showExceptionDialog(Exception, String)}.
    *
    * @param e - the exception.
    * @param message - some human readable message (not
    *           {@link Exception#getMessage}!)
    *
    * @return The formatted exception.
    */
   public static String formatExceptionMessage(Exception e, String message)
   {
      final StringBuffer sb = new StringBuffer();

      sb.append("<html><body width=\"500px\"><h2>").append(I18n.getMessage("Dialogs.Exception_Caption"));
      sb.append("</h2><b>").append(message.replace("\n", "<br />"));

      if (message.isEmpty())
         sb.append(e.getClass().getName());
      else if (!message.endsWith(".") && !message.endsWith("?") && !message.endsWith("!"))
         sb.append('.');

      sb.append("</b><br /><br /><hr /><br />");

      for (Throwable ee = e; ee != null; ee = ee.getCause())
      {
         String msg = ee.getMessage();
         if (msg == null)
            continue;

         sb.append("<b>").append(ee.getClass().getSimpleName()).append("</b><br />");

         for (String line : msg.split("\r*\n"))
         {
            line = line.trim();

            if (line.isEmpty())
               continue;

            if (line.length() > 1000)
               line = line.substring(0, 1000) + " ...";

            line = line.replace('`', '\'').replace("<", "&lt;");
            sb.append(line).append("<br /><br />");
         }
      }

      sb.append("</body></html>");
      return sb.toString();
   }
}
