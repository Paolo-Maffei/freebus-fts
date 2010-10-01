package org.freebus.knxcomm;

import org.apache.log4j.Logger;
import org.freebus.fts.common.SimpleConfig;
import org.freebus.knxcomm.internal.BusInterfaceImpl;
import org.freebus.knxcomm.link.netip.KNXnetLink;
import org.freebus.knxcomm.link.serial.Ft12SerialLink;
import org.freebus.knxcomm.link.serial.SerialPortUtil;
import org.freebus.knxcomm.types.KNXConnectionType;
import org.freebus.knxcomm.types.LinkMode;

/**
 * Factory class for KNX/EIB bus interfaces.
 */
public final class BusInterfaceFactory
{
   private static BusInterface busInterface;
   private static LinkMode defaultLinkMode = LinkMode.LinkLayer;

   /**
    * Returns the default bus-interface. If no default bus-interface exists, one
    * is created. If the creation of the bus-interface fails, an error dialog is
    * shown and null is returned.
    *
    * @return The default {@link BusInterface} bus-interface, or null if no
    *         bus-interface could be created.
    */
   public synchronized static BusInterface getBusInterface()
   {
      if (busInterface == null)
      {
         try
         {
            createBusInterface();
         }
         catch (final Exception e)
         {
            // SwingUtilities.invokeLater(new Runnable()
            // {
            // @Override
            // public void run()
            // {
            // Dialogs.showExceptionDialog(e,
            // I18n.getMessage("BusInterfaceService.ErrCreateBusInterface"));
            // }
            // });
            Logger.getLogger(BusInterfaceFactory.class).error("Failed to create bus interface: " + e);

            return null;
         }
      }

      return busInterface;
   }

   /**
    * @return true if the bus interface exists, false if it would be created
    *         when calling {@link #getBusInterface}.
    */
   public synchronized static boolean busInterfaceOpened()
   {
      return busInterface != null;
   }

   /**
    * Create the default bus interface. Automatically called on demand by
    * {@link #getBusInterface()}.
    *
    * @throws Exception
    *
    * @see #getBusInterface()
    */
   private static void createBusInterface() throws Exception
   {
      final SimpleConfig cfg = SimpleConfig.getInstance();
      BusInterface newBusInterface = null;

      final KNXConnectionType type = KNXConnectionType.valueOf(cfg.getStringValue("knxConnectionType"));
      if (type == KNXConnectionType.KNXNET_IP)
         newBusInterface = newKNXnetInterface(cfg.getStringValue("knxConnectionKNXnet.host"), cfg
               .getIntValue("knxConnectionKNXnet.port"));
      else if (type == KNXConnectionType.SERIAL_FT12)
         newBusInterface = newSerialInterface(cfg.getStringValue("knxConnectionSerial.port"));
      else throw new Exception("No bus interface configured");

      newBusInterface.open(defaultLinkMode);

      // if open() fails an exception will be thrown.
      // if not, we keep the bus interface for later use.
      busInterface = newBusInterface;
   }

   /**
    * Close the default bus-interface. The bus-interface will be recreated upon
    * the next {@link #getBusInterface()} call.
    */
   public synchronized static void closeBusInterface()
   {
      if (busInterface != null)
      {
         busInterface.close();
         busInterface = null;
      }
   }

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

//      return new BusInterfaceImpl(new SerialFt12Link(portName));
      return new BusInterfaceImpl(new Ft12SerialLink(portName));
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
      return new BusInterfaceImpl(new KNXnetLink(host, port));
   }

   /**
    * Set the default {@link LinkMode link mode} that is used when a bus
    * interface is created. Does not change existing bus interfaces.
    */
   public static void setDefaultLinkMode(LinkMode mode)
   {
      defaultLinkMode = mode;
   }

   /**
    * @return the default {@link LinkMode link mode} that is used when a bus
    *         interface is created.
    */
   public static LinkMode getDefaultLinkMode()
   {
      return defaultLinkMode;
   }
}
