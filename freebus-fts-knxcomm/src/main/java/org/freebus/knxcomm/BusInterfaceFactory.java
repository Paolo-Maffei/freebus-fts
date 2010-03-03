package org.freebus.knxcomm;

import org.freebus.knxcomm.internal.BusInterfaceImpl;
import org.freebus.knxcomm.netip.KNXnetConnection;
import org.freebus.knxcomm.serial.SerialFt12Connection;
import org.freebus.knxcomm.serial.SerialPortUtil;

/**
 * Factory class for KNX/EIB bus interfaces.
 */
public final class BusInterfaceFactory
{
   /**
    * Create a new KNX/EIB bus interface that connects to a serial port.
    * 
    * @param portName - the name of the serial port, e.g. "COM1:" or
    *           "/dev/ttyS0"
    * @throws Exception
    */
   public static BusInterface newSerialInterface(String portName) throws Exception
   {
      SerialPortUtil.loadSerialPortLib();

      return new BusInterfaceImpl(new SerialFt12Connection(portName));
   }

   /**
    * Create a new KNXnet/IP bus interface that connects e.g. to an eibd.
    * 
    * @param host - the name or IP address of the host that is running the
    *           KNXnet/IP server.
    * @param port - the UDP port of the KNXnet/IP server on the host. Default:
    *           3671.
    */
   public static BusInterface newKNXnetInterface(String host, int port)
   {
      return new BusInterfaceImpl(new KNXnetConnection(host, port));
   }
}
