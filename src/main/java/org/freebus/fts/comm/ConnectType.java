package org.freebus.fts.comm;


/**
 * Types of the connection to the EIB bus-interface.
 */
public enum ConnectType
{
   /**
    * Serial port.
    */
   SERIAL("SERIAL", SerialBusInterface.class);

   /**
    * Name of the connection.
    */
   public final String name;

   /**
    * Class for the bus-interface connection
    */
   public final Class<? extends BusInterface> busInterfaceClass;

//   /**
//    * @return the constant for the given name.
//    */
//   static public ConnectTypes valueOf(String name)
//   {
//      for (ConnectTypes e: values())
//         if (e.name.equals(name)) return e;
//
//      throw new IllegalArgumentException("Invalid connection type: "+name);
//   }

   /**
    * Test if sectionId is a known section-id.
    */
   static public boolean isValid(String name)
   {
      for (ConnectType e: values())
         if (e.name.equals(name)) return true;
      return false;
   }

   /*
    * Internal constructor.
    */
   private ConnectType(String name, Class<? extends BusInterface> busInterfaceClass)
   {
      this.name = name;
      this.busInterfaceClass = busInterfaceClass;
   }
}
