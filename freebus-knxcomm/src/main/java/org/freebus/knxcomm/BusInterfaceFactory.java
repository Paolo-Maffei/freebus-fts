package org.freebus.knxcomm;

import org.freebus.knxcomm.internal.BusInterfaceImpl;
import org.freebus.knxcomm.serial.SerialFt12Connection;


/**
 * Factory class for KNX/EIB bus interfaces.
 */
public final class BusInterfaceFactory
{
   private static BusInterface defaultInterface;

   /**
    * Create a new KNX/EIB bus interface that connects to a serial port.
    * 
    * @param portName - the name of the serial port, e.g. "COM1:" or "/dev/ttyS0"
    */
   public static BusInterface newSerialInterface(String portName)
   {
      return new BusInterfaceImpl(new SerialFt12Connection(portName));
   }

   /**
    * @return the default KNX/EIB bus interface.
    * @see setDefault
    */
   public static BusInterface getDefault()
   {
      return defaultInterface;
   }

   /**
    * Set the default KNX/EIB bus interface.
    */
   public static void setDefault(BusInterface busInterface)
   {
      defaultInterface = busInterface;
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
