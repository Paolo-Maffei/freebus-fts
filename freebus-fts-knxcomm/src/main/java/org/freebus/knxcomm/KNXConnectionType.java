package org.freebus.knxcomm;

/**
 * Types for KNX/EIB bus connections.
 */
public enum KNXConnectionType
{
   /**
    * No bus connection.
    */
   NONE("No connection"),

   /**
    * Serial port connection, using FT1.2
    */
   SERIAL_FT12("Serial Port FT1.2"),

   /**
    * TCP/IP connection to eibd. 
    */
   EIBD("TCP/IP via eibd");

   /**
    * A human readable, English label.
    */
   public final String label;

   /*
    * Internal constructor.
    */
   private KNXConnectionType(String label)
   {
      this.label = label;
   }
}
