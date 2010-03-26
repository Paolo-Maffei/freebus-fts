package org.freebus.fts.core;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class I18n
{
   private static final String bundleName = "fts-messages";
   private static final ResourceBundle bundle = ResourceBundle.getBundle(bundleName);

   private I18n()
   {
   }

   public static String getMessage(String key)
   {
      try
      {
         return bundle.getString(key);
      }
      catch (MissingResourceException e)
      {
         return '!' + key + '!';
      }
   }

   /**
    * Returns the message string for the given message-id for the active
    * language. The variables {0}, {1}, ... are replaced with the arguments given in args.
    *
    * @see {@link MessageFormat} for more information on formatting the arguments.
    *
    * @param msgid - the message id
    * @param args - an array of arguments.
    * @return the message string
    */
   public static String formatMessage(final String msgid, Object[] args)
   {
      final StringBuffer sb = new StringBuffer();
      (new MessageFormat(getMessage(msgid))).format(args, sb, null);
      return sb.toString();
   }
}
