package org.freebus.knxcomm.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n
{
//   private static final String bundleName = "org.freebus.fts.project.messages";
   private static final ResourceBundle bundle = ResourceBundle.getBundle("project-messages");

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
}
