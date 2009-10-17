package org.freebus.fts.utils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Static class for translated messages 
 */
public final class I18n
{
   static private String language = null;
   static private String country = null;
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

      try
      {
         messages = ResourceBundle.getBundle("messages", locale);
      }
      catch (MissingResourceException e)
      {
         System.out.println("Failed to load messages for \"" + locale.getDisplayName() + "\", using fallback.");
         messages = ResourceBundle.getBundle("src/main/resources/messages", Locale.ENGLISH);
      }
   }
}
