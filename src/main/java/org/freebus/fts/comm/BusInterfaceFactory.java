package org.freebus.fts.comm;

import java.io.IOException;

import org.freebus.fts.comm.internal.BusInterfaceImpl;


/**
 * Factory class for KNX/EIB bus interfaces.
 */
public final class BusInterfaceFactory
{
   private static BusInterface defaultInterface;

   /**
    * Create a new, private, KNX/EIB bus interface.
    */
   public static BusInterface newInterface()
   {
      return new BusInterfaceImpl();
   }

   /**
    * @return the default KNX/EIB bus interface. The interface is created if
    *         it does not exist.
    * @throws IOException 
    */
   public static BusInterface getDefault() throws IOException
   {
      if (defaultInterface == null)
      {
         final BusInterface bus = newInterface();
         bus.open();
         defaultInterface = bus;
      }
      return defaultInterface;
   }

   /**
    * Close and dispose the default KNX/EIB bus interface.
    */
   public static void disposeDefaultInterface()
   {
      if (defaultInterface != null)
      {
         defaultInterface.close();
         defaultInterface = null;
      }
   }
}
