package org.freebus.fts.comm;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.TooManyListenersException;

import org.freebus.fts.Config;

/**
 * Interface for accessing the EIB bus via a serial port interface, using
 * the FT1.2 protocol. 
 */
class SerialBusInterface extends BusInterface implements SerialPortEventListener
{
   static CommPortIdentifier portIdent;
   protected SerialPort serialPort;

   /**
    * Create a new serial-port bus-interface.
    * 
    * @throws PortInUseException
    * @throws BusConnectException
    */
   public SerialBusInterface() throws BusConnectException
   {
      final String portName = Config.getInstance().getCommPort();
      try
      {
         portIdent = CommPortIdentifier.getPortIdentifier(portName);
         serialPort = (SerialPort) portIdent.open("SerialBusInterface", 2000);

         // RS-Interface: 115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
         // SerialPort.PARITY_NONE
         //
         // FT-1.2: 19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
         // SerialPort.PARITY_EVEN
         serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);

         inputStream = serialPort.getInputStream();
         outputStream = serialPort.getOutputStream();

         serialPort.addEventListener(this);
         serialPort.notifyOnDataAvailable(true);
      }
      catch (NoSuchPortException e)
      {
         throw new BusConnectException("Cannot connect to serial port " + portName, e);
      }
      catch (PortInUseException e)
      {
         throw new BusConnectException("Serial port " + portName + " is in use", e);
      }
      catch (UnsupportedCommOperationException e)
      {
         throw new BusConnectException("Failed to set serial port parameters for " + portName, e);
      }
      catch (IOException e)
      {
         throw new BusConnectException("Failed to open I/O streams for serial port " + portName, e);
      }
      catch (TooManyListenersException e)
      {
         throw new BusConnectException("Failed to initialize internal listener", e);
      }

      try
      {
         sendReset();
         sendData(new int[]{ 0x81, 0x03, 0x01, 0x03, 0x01, 0x10 });
      }
      catch (IOException e)
      {
         throw new BusConnectException("Failed to send reset for serial port " + portName, e);
      }
   }

   /**
    * Close all connections of the interface.
    */
   @Override
   protected void close()
   {
      try
      {
         if (inputStream!=null) inputStream.close();
         if (outputStream!=null) outputStream.close();
         if (serialPort!=null) serialPort.close();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * A serial port event occured.
    */
   @Override
   public void serialEvent(SerialPortEvent event)
   {
      switch (event.getEventType())
      {

         case SerialPortEvent.BI:
         case SerialPortEvent.OE:
         case SerialPortEvent.FE:
         case SerialPortEvent.PE:
         case SerialPortEvent.CD:
         case SerialPortEvent.CTS:
         case SerialPortEvent.DSR:
         case SerialPortEvent.RI:
         case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            break;

         case SerialPortEvent.DATA_AVAILABLE:
            try
            {
               while (inputStream.available() > 0)
                  readData();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
            break;
      }
   }
}
