package org.freebus.fts.elements.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n
{
   private static final String BUNDLE_NAME = "elements-messages";

   private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

   private I18n()
   {
   }

   public static String getMessage(String key)
   {
      try
      {
         return RESOURCE_BUNDLE.getString(key);
      }
      catch (MissingResourceException e)
      {
         return '!' + key + '!';
      }
   }
}
