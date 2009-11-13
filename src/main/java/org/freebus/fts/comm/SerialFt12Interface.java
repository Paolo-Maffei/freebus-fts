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

/**
 * A FT1.2 speaking {@link BusInterface} using the local serial port.
 */
public final class SerialFt12Interface extends Ft12Interface implements SerialPortEventListener
{
   protected final String portName;
   protected CommPortIdentifier portIdent;
   protected SerialPort serialPort = null;
   protected InputStream inputStream = null;
   protected OutputStream outputStream = null;

   /**
    * Create a connection for the serial port portName.
    */
   public SerialFt12Interface(String portName)
   {
      this.portName = portName;
   }

   /**
    * Connect to the serial port.
    * @throws IOException
    */
   @Override
   public void open() throws IOException
   {
      System.out.println("Opening serial port " + portName);
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

      super.open();
   }

   /**
    * Disconnect from the serial port.
    */
   @Override
   public void close()
   {
      System.out.println("Closing serial port " + portName);
      inputStream = null;
      outputStream = null;

      if (serialPort != null)
      {
         serialPort.removeEventListener();
         serialPort = null;
      }
   }

   /**
    * @return true if the connection to the serial port is opened.
    */
   @Override
   public boolean isOpen()
   {
      return inputStream != null && outputStream != null;
   }

   /**
    * Receive one byte from the serial port.
    * @throws IOException 
    */
   @Override
   protected int read() throws IOException
   {
      return inputStream.read();
   }

   /**
    * @return true if at least one byte can be read.
    * @throws IOException 
    */
   @Override
   protected boolean isDataAvailable() throws IOException
   {
      if (inputStream == null) return false;
      return inputStream.available() > 0;
   }

   /**
    * Send length bytes of data to the serial port.
    * @throws IOException 
    */
   @Override
   protected void write(int[] data, int length) throws IOException
   {
      for (int i = 0; i < length; ++i)
         outputStream.write(data[i]);

      outputStream.flush();
   }

   /**
    * An event on the serial port occurred.
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
                  dataAvailable();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
            break;
      }
   }
}
