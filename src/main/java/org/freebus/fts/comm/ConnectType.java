package org.freebus.fts.comm;



/**
 * Types of the connection to the EIB bus-interface.
 */
public enum ConnectType
{
   /**
    * A serial port connection.
    */
   SERIAL(SerialBusInterface.class),

   /**
    * An unknown connection.
    */
   UNKNOWN(null);

   /**
    * Class for the bus-interface connection
    */
   public final Class<? extends BusInterface> busInterfaceClass;

   /**
    * Test if sectionId is a known section-id.
    */
   static public boolean isValid(String name)
   {
      for (ConnectType e: values())
         if (e.toString().equals(name)) return true;
      return false;
   }

   /*
    * Internal constructor.
    */
   private ConnectType(Class<? extends BusInterface> busInterfaceClass)
   {
      this.busInterfaceClass = busInterfaceClass;
   }
}
