package org.freebus.fts.project.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n
{
//   private static final String bundleName = "org.freebus.fts.project.messages";
   private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("persistence-messages");

   private I18n()
   {
   }

   public static String getMessage(String key)
   {
      try
      {
         return BUNDLE.getString(key);
      }
      catch (MissingResourceException e)
      {
         return '!' + key + '!';
      }
   }
}
