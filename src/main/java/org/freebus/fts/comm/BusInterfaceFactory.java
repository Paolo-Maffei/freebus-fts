package org.freebus.fts.comm;

import org.freebus.fts.Config;


/**
 * Factory class for bus interfaces
 */
public final class BusInterfaceFactory
{
   private static BusInterface defaultInstance = null;

   /**
    * Create a new bus-interface instance
    */
   public static BusInterface newInstance()
   {
      final Config cfg = Config.getInstance();
      return new SerialFt12Interface(cfg.getCommPort());
   }

   /**
    * @return the default bus interface.
    */
   public static BusInterface getDefaultInstance()
   {
      if (defaultInstance == null) defaultInstance = newInstance();
      return defaultInstance;
   }

   /**
    * Dispose the default bus interface.
    */
   public static void disposeDefaultInstance()
   {
      if (defaultInstance != null)
      {
         defaultInstance.close();
         defaultInstance = null;
      }
   }
}
