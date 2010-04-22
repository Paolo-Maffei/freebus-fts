package org.freebus.fts.pages.busmonitor;

import java.util.HashSet;
import java.util.Set;

import org.freebus.fts.common.SimpleConfig;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

/**
 * A configurable filter for telegrams or frames.
 */
public class Filter
{
   private final Set<ApplicationType> appTypes = new HashSet<ApplicationType>();
   private final Set<Transport> transportTypes = new HashSet<Transport>();

   /**
    * Test if the filter matches the telegram.
    *
    * @param telegram - the telegram to match.
    *
    * @return true if the telegram matches the filter.
    */
   public boolean matches(final Telegram telegram)
   {
      final ApplicationType telegramAppType = telegram.getApplicationType();
      for (ApplicationType appType: appTypes)
      {
         if (appType == telegramAppType)
            return true;
      }

      final Transport telegramTrans = telegram.getTransport();
      for (Transport trans: transportTypes)
      {
         if (trans == telegramTrans)
            return true;
      }

      return false;
   }

   /**
    * @return the set of matching application types.
    */
   public Set<ApplicationType> getApplicationTypes()
   {
      return appTypes;
   }

   /**
    * @return the set of matching transport types.
    */
   public Set<Transport> getTransportTypes()
   {
      return transportTypes;
   }

   /**
    * Load the filter from a configuration object.
    *
    * @param config - the configuration object to load the filter from.
    * @param key - the base name for the configuration keys.
    */
   public void fromConfig(SimpleConfig config, String key)
   {
   }

   /**
    * Save the filter to a configuration object.
    *
    * @param config - the configuration object to save the filter to.
    * @param key - the base name for the configuration keys.
    */
   public void saveConfig(SimpleConfig config, String key)
   {
      StringBuilder sb;

      sb = new StringBuilder();
      for (ApplicationType appType: appTypes)
         sb.append(',').append(appType.toString());
      config.put(key + ".apps", sb.toString().substring(1));
   }
}
