package org.freebus.fts.comm;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import org.freebus.fts.Config;

/**
 * Interface for accessing the EIB bus via a serial port interface.
 */
public class SerialBusInterface extends BusInterface implements SerialPortEventListener
{
   static CommPortIdentifier portIdent;
   InputStream inputStream;
   OutputStream outputStream;
   SerialPort serialPort;
   Thread readThread;

   /**
    * Create a new serial-port bus-interface.
    * 
    * @throws PortInUseException
    * @throws BusConnectException
    */
   public SerialBusInterface() throws BusConnectException
   {
      final String portName = Config.getConfig().getCommPort();
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

   /**
    * Read the data of the serial port.
    * @throws IOException
    */
   protected void readData() throws IOException
   {
      int dataLen = 0;

      final int type = inputStream.read();
      switch (type)
      {
         case 0xe5:
            System.out.println("ACK [0xe5]");
            return;
         case 0x68:
            dataLen = inputStream.read();
            inputStream.read(); // must be equal to dataLen
            System.out.print("DATA [0x68] (" + Integer.toString(dataLen) + " bytes): ");
            dataLen += 3;
            break;
         default:
            System.out.print("GOT 0x" + Integer.toHexString(type));
            dataLen = 16;
            break;
      }

      int[] data = new int[dataLen];
      for (int rlen = 0; rlen < dataLen; ++rlen)
         data[rlen] = inputStream.read();

      int start = 0;
      int len = dataLen;
      if (type==0x68)
      {
         int checksum = 0;
         for (int i=1; i<dataLen-2; ++i)
            checksum += data[i];
         checksum &= 255;

         if (data[0]==0x68 && data[dataLen-1]==0x16 && data[dataLen-2]==checksum)
         {
            start = 2;
            len = dataLen - 4;
         }
         else if (data[dataLen-2]!=checksum) System.out.print("[CHECKSUM-ERROR] ");
         else System.out.print("[CORRUPT] ");
      }

      final int dataType = data[start];
      if (dataType==0x29)
      {
         telegramReceived(data, start, len);
      }

      for (; len>0; ++start, --len)
         System.out.printf("%02x ", data[start]);
      System.out.println();
   }
}
