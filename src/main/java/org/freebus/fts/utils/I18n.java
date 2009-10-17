package org.freebus.fts.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Static class for translated messages 
 */
public final class I18n
{
   static private String language = "";
   static private String country = "";
   static private Locale locale;
   static private ResourceBundle messages;
   
   /**
    * Returns the message string for the given message-id for the active
    * language.
    * 
    * @param msgid is the message id
    * @return the message string
    */
   static public String getMessage(final String msgid)
   {
      if (locale==null) update();
      try
      {
         return messages.getString(msgid);
      }
      catch (MissingResourceException e)
      {
         return msgid;
      }
   }

   /**
    * Update locale and messages resource-bundle.
    */
   private static synchronized void update()
   {
      if (language==null && country==null) locale = Locale.getDefault();
      else locale = new Locale(language, country);

      messages = ResourceBundle.getBundle("messages", locale);
      if (messages==null) messages = ResourceBundle.getBundle("messages", Locale.ENGLISH);
   }
}
