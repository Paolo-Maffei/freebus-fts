package org.freebus.knxcomm.serial;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import org.freebus.knxcomm.KNXConnection;

/**
 * A FT1.2 speaking {@link KNXConnection} using the local serial port.
 */
public final class SerialFt12Connection extends Ft12Connection implements SerialPortEventListener
{
   protected final SerialPortWrapper port = new SerialPortWrapper();
   private OutputStream outputStream;
   private InputStream inputStream;
   protected final String portName;

   /**
    * Create a connection for the serial port portName.
    */
   public SerialFt12Connection(String portName)
   {
      this.portName = portName;
   }

   /**
    * Connect to the serial port.
    * 
    * @throws IOException
    */
   @Override
   public void open() throws IOException
   {
      // Freebus RS-Interface: 115200, SerialPort.DATABITS_8,
      // SerialPort.STOPBITS_1, SerialPort.PARITY_NONE.
      //
      // FT-1.2: 19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
      // SerialPort.PARITY_EVEN.
      port.open(portName, 19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);

      inputStream = port.getInputStream();
      outputStream = port.getOutputStream();

      try
      {
         port.addEventListener(this);
      }
      catch (TooManyListenersException e)
      {
         throw new IOException(e);
      }

      port.notifyOnDataAvailable(true);
      super.open();
   }

   /**
    * Disconnect from the serial port.
    */
   @Override
   public void close()
   {
      port.removeEventListener();

      inputStream = null;
      outputStream = null;
      port.close();
   }

   /**
    * @return true if the connection to the serial port is opened.
    */
   @Override
   public boolean isConnected()
   {
      return inputStream != null && outputStream != null;
   }

   /**
    * Receive one byte from the serial port.
    * 
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
    * 
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
