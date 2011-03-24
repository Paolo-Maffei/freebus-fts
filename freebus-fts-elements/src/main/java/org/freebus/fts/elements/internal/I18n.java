package org.freebus.fts.elements.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message catalog access methods.
 */
public class I18n
{
   private static final String BUNDLE_NAME = "elements-messages";
   private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

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
         return RESOURCE_BUNDLE.getString(msgid);
      }
      catch (MissingResourceException e)
      {
         return '!' + msgid + '!';
      }
   }

   /**
    * Disabled constructor.
    */
   private I18n()
   {
   }
}
