package org.freebus.fts.comm;

import org.freebus.fts.Config;
import org.freebus.fts.comm.serial.SerialFt12Connection;

/**
 * Factory class for KNX/EIB bus connections.
 */
public final class KNXConnectionFactory
{
   private static KNXConnection defaultConnection;

   /**
    * Create a new KNX/EIB bus connection.
    */
   public static KNXConnection newConnection()
   {
      final Config cfg = Config.getInstance();
      return new SerialFt12Connection(cfg.getCommPort());
   }

   /**
    * @return the default KNX/EIB bus connection.
    */
   public static KNXConnection getDefaultConnection()
   {
      if (defaultConnection == null) defaultConnection = newConnection();
      return defaultConnection;
   }

   /**
    * Close and dispose the default KNX/EIB bus connection.
    */
   public static void disposeDefaultConnection()
   {
      if (defaultConnection != null)
      {
         defaultConnection.close();
         defaultConnection = null;
      }
   }
}
