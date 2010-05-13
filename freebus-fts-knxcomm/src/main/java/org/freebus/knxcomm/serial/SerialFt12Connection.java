package org.freebus.knxcomm.serial;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;
import org.freebus.knxcomm.KNXConnection;
import org.freebus.knxcomm.types.LinkMode;

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
   public void open(LinkMode mode) throws IOException
   {
      // Freebus RS-Interface: 115200, SerialPort.DATABITS_8,
      // SerialPort.STOPBITS_1, SerialPort.PARITY_NONE.
      //
      // FT-1.2: 19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
      // SerialPort.PARITY_EVEN.
      port.open(portName, 19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);

      inputStream = new BufferedInputStream(port.getInputStream());
      outputStream = port.getOutputStream();

      // Skip all pending data
      while (inputStream.available() > 0)
         inputStream.read();

      try
      {
         port.addEventListener(this);
      }
      catch (TooManyListenersException e)
      {
         throw new IOException(e);
      }

      port.notifyOnDataAvailable(true);
      super.open(mode);
   }

   /**
    * Disconnect from the serial port.
    */
   @Override
   public void close()
   {
      port.removeEventListener();
      port.close();

      inputStream = null;
      outputStream = null;
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
      for (int tries = 0; tries < 10; ++tries)
      {
         int b = inputStream.read();
         if (b >= 0)
            return b;

         try
         {
            inputStream.wait(10);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
            break;
         }
      }

      return -1;
   }

   /**
    * @return true if at least one byte can be read.
    * @throws IOException
    */
   @Override
   protected boolean isDataAvailable() throws IOException
   {
      if (inputStream == null)
         return false;
      return inputStream.available() > 0;
   }

   /**
    * Send length bytes of data to the serial port.
    *
    * @throws IOException
    */
   @Override
   protected void write(byte[] data, int length) throws IOException
   {
      if (outputStream == null)
         throw new IOException("connection is closed");

      outputStream.write(data, 0, length);
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
            // Logger.getLogger(getClass()).info("+++++ Serial port event: " +
            // event.getEventType());
            break;

         case SerialPortEvent.DATA_AVAILABLE:
            try
            {
               port.notifyOnDataAvailable(false);
               // Logger.getLogger(getClass()).debug("***** BEGIN dataAvailable *****");

               while (inputStream.available() > 0)
                  dataAvailable();
            }
            catch (IOException e)
            {
               Logger.getLogger(getClass()).error(e);

               // Error recovery: skip all bytes until there is silence
               try
               {
                  while (isConnected() && read() > 0)
                  {
                  }
               }
               catch (IOException e1)
               {
                  e1.printStackTrace();
               }
            }
            finally
            {
               // Logger.getLogger(getClass()).debug("***** END dataAvailable *****");
               port.notifyOnDataAvailable(true);
            }
            break;
      }
   }

   /**
    * Create the listener thread.
    */
   protected Thread createListenerThread()
   {
      return new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            Logger.getLogger(getClass()).debug("Starting FT1.2 listener thread");

            while (port.isOpened())
            {
            }
         }
      });
   }
}
