package example;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

public class SerialPortWrite
{
   static Enumeration<?> portList;
   static CommPortIdentifier portId;
   static String messageString = "Hello, world!";
   static SerialPort serialPort;
   static OutputStream outputStream;
   static boolean outputBufferEmptyFlag = false;

   /**
    * Method declaration
    * 
    * @param args
    * @see
    */
   public static void main(String[] args)
   {
      boolean portFound = false;
      String defaultPort = "/dev/ttyS0";

      if (args.length > 0)
      {
         defaultPort = args[0];
      }

      portList = CommPortIdentifier.getPortIdentifiers();

      while (portList.hasMoreElements())
      {
         portId = (CommPortIdentifier) portList.nextElement();

         if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
         {

            if (portId.getName().equals(defaultPort))
            {
               System.out.println("Found port " + defaultPort);

               portFound = true;

               try
               {
                  serialPort = (SerialPort) portId.open("SimpleWrite", 2000);
               }
               catch (PortInUseException e)
               {
                  System.out.println("Port in use.");

                  continue;
               }

               try
               {
                  outputStream = serialPort.getOutputStream();
               }
               catch (IOException e)
               {
               }

               try
               {
                  serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
               }
               catch (UnsupportedCommOperationException e)
               {
               }

               try
               {
                  serialPort.notifyOnOutputEmpty(true);
               }
               catch (Exception e)
               {
                  System.out.println("Error setting event notification");
                  System.out.println(e.toString());
                  System.exit(-1);
               }

               System.out.println("Writing \"" + messageString + "\" to " + serialPort.getName());

               try
               {
                  outputStream.write(messageString.getBytes());
               }
               catch (IOException e)
               {
               }

               try
               {
                  Thread.sleep(2000); // Be sure data is xferred before closing
               }
               catch (Exception e)
               {
               }
               serialPort.close();
               System.exit(1);
            }
         }
      }

      if (!portFound)
      {
         System.out.println("port " + defaultPort + " not found.");
      }
   }
}
