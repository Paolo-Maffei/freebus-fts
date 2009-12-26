package org.freebus.knxcomm;

import org.freebus.knxcomm.eibd.EibdConnection;
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
    * @throws Exception
    */
   public static BusInterface newSerialInterface(String portName) throws Exception
   {
      // loadSerialPortLibrary();
      return new BusInterfaceImpl(new SerialFt12Connection(portName));
   }

   /**
    * Create a new KNX/EIB bus interface that connects to an eibd.
    * 
    * @param host - the name or IP address of the host that is running eibd.
    * @param port - the TCP port of the eibd on the host. Default: 6720.
    */
   public static BusInterface newEibdInterface(String host, int port)
   {
      return new BusInterfaceImpl(new EibdConnection(host, port));
   }

   /**
    * @return the default KNX/EIB bus interface.
    * @see {@link #setDefault} - used to set the default bus interface.
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
