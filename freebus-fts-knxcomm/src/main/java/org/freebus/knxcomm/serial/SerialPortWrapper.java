package org.freebus.knxcomm.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

/**
 * A wrapper class that does the initialization and open of a serial port.
 */
public class SerialPortWrapper
{
   /**
    * Constants for the number of data bits.
    */
   public static final int DATABITS_5 = SerialPort.DATABITS_5;
   public static final int DATABITS_6 = SerialPort.DATABITS_6;
   public static final int DATABITS_7 = SerialPort.DATABITS_7;
   public static final int DATABITS_8 = SerialPort.DATABITS_8;
   
   /**
    * Constants for the number of stop bits.
    */
   public static final int STOPBITS_1 = SerialPort.STOPBITS_1;
   public static final int STOPBITS_2 = SerialPort.STOPBITS_2;
   public static final int STOPBITS_1_5 = SerialPort.STOPBITS_1_5;

   /**
    * Constants for the parity.
    */
   public static final int PARITY_NONE = SerialPort.PARITY_NONE;
   public static final int PARITY_ODD = SerialPort.PARITY_ODD;
   public static final int PARITY_EVEN = SerialPort.PARITY_EVEN;
   public static final int PARITY_MARK = SerialPort.PARITY_MARK;
   public static final int PARITY_SPACE = SerialPort.PARITY_SPACE;


   private SerialPort serialPort;
   private InputStream inputStream;
   private OutputStream outputStream;
   private String portName;

   /**
    * Ensure that the native serial port library is loaded.
    */
   static
   {
      SerialPortUtil.loadSerialPortLib();
   }

   /**
    * Connect to the serial port.
    * 
    * @param portName - the name of the port.
    * @param baudRate - the baud rate, e.g. 19200
    * @param dataBits - the number of data bits, e.g. 8
    * @param stopBits - the number of stop bits, e.g. 1
    * @param parity - the parity, e.g. {@link SerialPortWrapper#PARITY_EVEN}.
    * 
    * @throws IOException if the port could not be opened.
    */
   public void open(String portName, int baudRate, int dataBits, int stopBits, int parity) throws IOException
   {
      try
      {
         Logger.getLogger(getClass()).info("Opening serial port " + portName);

         inputStream = null;
         outputStream = null;

         final CommPortIdentifier portIdent = CommPortIdentifier.getPortIdentifier(portName);
         serialPort = (SerialPort) portIdent.open(portName, 2000);
   
         serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
   
         inputStream = serialPort.getInputStream();
         outputStream = serialPort.getOutputStream();
         this.portName = portName;
      }
      catch (NoSuchPortException e)
      {
         throw new IOException("cannot open port: " + portName, e);
      }
      catch (UnsupportedCommOperationException e)
      {
         throw new IOException(e);
      }
      catch (PortInUseException e)
      {
         throw new IOException("port is in use: " + portName, e);
      }
   }

   /**
    * Close the serial port.
    */
   public void close()
   {
      Logger.getLogger(getClass()).info("Closing serial port " + portName);

      try
      {
         if (inputStream != null)
            inputStream.close();
      }
      catch (IOException e)
      {
      }
      try
      {
         if (outputStream != null)
            outputStream.close();
      }
      catch (IOException e)
      {
      }

      if (serialPort != null)
      {
         serialPort.removeEventListener();
         serialPort = null;
      }

      inputStream = null;
      outputStream = null;
      portName = null;
   }

   /**
    * @return the input stream
    */
   public InputStream getInputStream()
   {
      return inputStream;
   }

   /**
    * @return the output stream
    */
   public OutputStream getOutputStream()
   {
      return outputStream;
   }

   /**
    * Add a port listener to the serial port.
    * 
    * @param listener
    * @throws TooManyListenersException
    */
   public void addEventListener(SerialPortEventListener listener) throws TooManyListenersException
   {
      serialPort.addEventListener(listener);
   }

   /**
    * Notify the listeners when data is available on the serial port.
    * @param enable
    */
   public void notifyOnDataAvailable(boolean enable)
   {
      serialPort.notifyOnDataAvailable(enable);
   }
}
