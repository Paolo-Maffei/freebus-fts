package org.freebus.fts.service.internal;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class I18n
{
   private static final String bundleName = "service-messages";
   private static final ResourceBundle bundle = ResourceBundle.getBundle(bundleName);

   private I18n()
   {
   }

   /**
    * Returns the message string for the given message-id for the active
    * language.
    *
    * @param msgid - the message id.
    * @return the message string.
    */
   public static String getMessage(String msgid)
   {
      try
      {
         if (bundle != null)
            return bundle.getString(msgid);
      }
      catch (MissingResourceException e)
      {
      }

      return '!' + msgid + '!';
   }

   /**
    * Returns the message string for the given message-id for the active
    * language. The variables {0}, {1}, ... are replaced with the arguments
    * given in <code>args</code>.
    *
    * The class {@link MessageFormat} contains more further information on
    * formatting the arguments.
    *
    * @param msgid - the message id
    * @param args - an array of arguments.
    * @return the message string.
    */
   public static String formatMessage(final String msgid, Object... args)
   {
      final StringBuffer sb = new StringBuffer();
      final MessageFormat fmt = new MessageFormat(getMessage(msgid));
      fmt.format(args, sb, null);
      return sb.toString();
   }
}
