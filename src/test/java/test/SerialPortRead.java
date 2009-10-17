package test;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

public final class SerialPortRead implements Runnable, SerialPortEventListener
{
   static CommPortIdentifier portId;
   static Enumeration<?> portList;
   InputStream inputStream;
   OutputStream outputStream;
   SerialPort serialPort;
   Thread readThread;

   /**
    * Constructor
    */
   public SerialPortRead()
   {
      try
      {
         serialPort = (SerialPort) portId.open("SerialPortRead", 2000);
      }
      catch (PortInUseException e)
      {
         e.printStackTrace();
      }

      try
      {
         inputStream = serialPort.getInputStream();
         outputStream = serialPort.getOutputStream();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      try
      {
         serialPort.addEventListener(this);
      }
      catch (TooManyListenersException e)
      {
         e.printStackTrace();
      }

      serialPort.notifyOnDataAvailable(true);

      try
      {
         // RS-Interface: 115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
         // SerialPort.PARITY_NONE
         // FT-1.2: 19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
         // SerialPort.PARITY_EVEN
         serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
      }
      catch (UnsupportedCommOperationException e)
      {
         e.printStackTrace();
      }

      readThread = new Thread(this);
      readThread.start();

//      try
//      {
//         byte[] resetReq = new byte[4];
//         resetReq[0] = 0x10;
//         resetReq[1] = 0x40;
//         resetReq[2] = 0x40;
//         resetReq[3] = 0x16;
//
//         outputStream.write(resetReq);
//      }
//      catch (IOException e)
//      {
//         e.printStackTrace();
//      }
   }

   /**
    * Method declaration
    * 
    * @see
    */
   public void run()
   {
      try
      {
         Thread.sleep(20000);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Method declaration
    * 
    * @param event
    * @see
    */
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
            inputStream.read(); // must be == dataLen
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
         // Received EIB telegram
         // E.g.: 1/0/10 = 0|1
         System.out.print("Telegram ");
         final int ctrl = data[start+1];
         if ((ctrl & (1<<5)) == 0) System.out.print("(repeated) ");
         final int priority = (ctrl>>2) & 3;
         System.out.printf("prio %d ", priority);
         
         System.out.printf("from %d.%d.%d ", data[start+2]>>4, data[start+2]&15, data[start+3]);

         final int recv = (data[start+4]<<8) + data[start+5];
         final int drl = data[start+6];

         if ((drl & (1<<7))==0) System.out.printf("to %d.%d.%d: ", recv>>12, (recv>>8)&15, recv&255);
         else System.out.printf("to %d/%d/%d: ", recv>>11, (recv>>7)&15, recv&127);

         int bytes = (drl&15) + 1;
         for (int i=start+7; bytes>0; ++i, --bytes)
            System.out.printf("%02x ", data[i]);

         System.out.print("... raw: ");
      }

      for (; len>0; ++start, --len)
         System.out.printf("%02x ", data[start]);

      System.out.println();
   }

   /**
    * @param args
    */
   public static void main(String[] args)
   {
      boolean portFound = false;
      String defaultPort = "/dev/ttyS0";

      if (args.length > 0) defaultPort = args[0];

      portList = CommPortIdentifier.getPortIdentifiers();

      while (portList.hasMoreElements())
      {
         portId = (CommPortIdentifier) portList.nextElement();
         if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
         {
            if (portId.getName().equals(defaultPort))
            {
               System.out.println("Found port: " + defaultPort);
               portFound = true;
               new SerialPortRead();
            }
         }
      }
      if (!portFound)
      {
         System.out.println("port " + defaultPort + " not found.");
      }
   }

}
