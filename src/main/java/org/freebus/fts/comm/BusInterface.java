package org.freebus.fts.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Display;
import org.freebus.fts.Config;
import org.freebus.fts.eib.Telegram;

/**
 * Base class for bus interfaces.
 */
public class BusInterface
{
   static private BusInterface instance = null;
   protected final Set<BusListener> listeners = new HashSet<BusListener>();

   // The input stream. Must be initialized by the subclass.
   protected InputStream inputStream = null;

   // The output stream. Must be initialized by the subclass.
   protected OutputStream outputStream = null;

   protected int readFrameCount = 0;
   protected int writeFrameCount = 0;

   // Variables for transparent recording of the read data
   static private final int bufferMaxLen = 1024;
   private final int[] inputBuffer = new int[bufferMaxLen];
   private int inputBufferLen = 0;

   // Enable debug dump output
   protected boolean debug = true;

   /**
    * Returns the bus interface as selected in the configuration.
    */
   static public BusInterface getInstance() throws BusConnectException
   {
      if (instance==null)
      {
         final ConnectType conType = Config.getInstance().getCommType();
         try
         {
            instance = conType.busInterfaceClass.newInstance();
         }
         catch (Exception e)
         {
            throw new RuntimeException("Cannot create bus interface: "+conType.name, e);
         }
      }
      return instance;
   }

   /**
    * Remove the bus-interface.
    */
   static public void disposeInstance()
   {
      if (instance==null) return;
      instance.close();
      instance = null;
   }

   /**
    * Close all connections of the interface.
    */
   protected void close()
   {
   }

   /**
    * Create a bus-interface and sets the global instance.
    */
   protected BusInterface() throws BusConnectException
   {
      instance = this;
   }

   /**
    * Add a listener.
    */
   public void addListener(BusListener listener)
   {
      listeners.add(listener);
   }

   /**
    * Remove a listener.
    */
   public void removeListener(BusListener listener)
   {
      listeners.remove(listener);
   }

   /**
    * Notify all listeners about the given telegram.
    */
   public void notifyListeners(final Telegram telegram)
   {
      Display.getDefault().syncExec(new Runnable()
      {
         @Override
         public void run()
         {
            for (BusListener listener: listeners)
               listener.telegramReceived(telegram);
         }
      });
   }

   /**
    * Read one byte from the input stream.
    * The returned byte has a value in the range 0..255
    *
    * @throws IOException 
    */
   protected int read() throws IOException
   {
      int val = inputStream.read() & 255;
      if (inputBufferLen<bufferMaxLen) inputBuffer[inputBufferLen++] = val;
      return val;
   }

   /**
    * Write an array of bytes to the output stream.
    * The given integer values are converted to bytes. Only integer values
    * 0..255 are used.
    *
    * @throws IOException 
    */
   protected void write(int[] data) throws IOException
   {
      int val;

      if (debug) System.out.print("WRITE RAW:");
      for (int i=0; i<data.length; ++i)
      {
         val = data[i];
         if (debug) System.out.printf(" %02x", val);
         if (val<0 || val>255) throw new IllegalArgumentException("Cannot write value "+Integer.toString(val)+" as byte");
         outputStream.write(data[i]);
      }
      outputStream.flush();
      if (debug) System.out.println();
   }

   /**
    * Read a FT1.2 data frame.
    */
   protected void readDataFrame() throws IOException
   {
      final int dataLen = read() - 1;
      read(); // dataLen repeated
      System.out.print("DATA [0x68] (" + Integer.toString(dataLen) + " bytes): ");
      String err = "";

      ++readFrameCount;
      if (read()!=0x68) err = err+"|Invalid boundary marker";
      
      int controlByte = read();
      if (controlByte!=0xf3 && controlByte!=0xd3) err = err+"|Invalid control byte 0x"+Integer.toHexString(controlByte);

      int ftCheckSum = controlByte;
      final int[] data = new int[dataLen];
      for (int i=0; i<dataLen; ++i)
      {
         data[i] = read();
         System.out.printf(" %02x", data[i]);
         ftCheckSum += data[i];
      }

      final int checksum = read();
      if (checksum!=(ftCheckSum&0xff)) err = err+"|FT checksum error";

      final int eofMarker = read();
      if (eofMarker!=0x16) err = err+"|Invalid eof-marker 0x"+Integer.toHexString(eofMarker);

      if (err.length() > 0) System.out.println(" ERROR:"+err.substring(1));
      else
      {
         System.out.println();

         try
         {
            notifyListeners(new Telegram(data));
         }
         catch (IllegalArgumentException e)
         {
            System.out.println("Cannot create telegram: " + e.getMessage());
         }
      }
   }

   /**
    * Read the next FT1.2 frame from the bus-interface's input stream.
    * @throws IOException
    */
   protected void readData() throws IOException
   {
      inputBufferLen = 0;
      final int type = read();

      switch (type)
      {
         case 0xe5: // Acknowledge to the last frame we sent 
            System.out.println("ACK [0xe5]");
            break;

         case 0x68: // Frame with variable length
            readDataFrame();
            break;

         case 0x10: // Frame with fixed length
            final int cmd = read();
            final int checksum = read();
            final int end = read();
            if (cmd != checksum || end != 0x16) System.out.println(" Malformed!");
            else if (cmd==0xC0)
            {
               System.out.print("Reset.ind");
               readFrameCount = 0;
               writeFrameCount = 0;
            }
            else System.out.println(" Unknown fixed-width frame 0x"+Integer.toHexString(cmd));
            break;

         default:   // Unknown frame
            System.out.print("UNKNOWN [0x" + Integer.toHexString(type) + "]");
            try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
            int val;
            while (inputStream.available()>0 && (val = read()) != 0x16)
               System.out.printf(" %02x", val);
            break;
      }

      // Send acknowledge
      if (type != 0xe5)
      {
         outputStream.write((byte)0xe5);
         outputStream.flush();
      }

      // Dump the read raw data
      if (debug)
      {
         System.out.print("READ RAW:");
         for (int i=0; i<inputBufferLen; ++i)
            System.out.printf(" %02x", inputBuffer[i]);
         System.out.println();
      }
   }

   /**
    * Send a data frame to the communication device.
    * @throws IOException 
    */
   public void sendData(int request[]) throws IOException
   {
      final int[] cmd = new int[request.length+7];

      int idx = -1;
      cmd[++idx] = 0x68;
      cmd[++idx] = request.length + 1;
      cmd[++idx] = cmd[1];
      cmd[++idx] = 0x68;
      cmd[++idx] = (++writeFrameCount&1)==0 ? 0x73 : 0x53;
      int val, checksum = cmd[idx];
      for (int i=0; i<request.length; ++i)
      {
         val = request[i];
         cmd[++idx] = val;
         checksum += val;
      }
      cmd[++idx] = checksum & 0xff;
      cmd[++idx] = 0x16;

      write(cmd);
   }

   /**
    * Send a FT1.2 reset command to the communication device.
    * @throws IOException 
    */
   public void sendReset() throws IOException
   {
      final int[] cmd = { 0x10, 0x40, 0x40, 0x16 };
      write(cmd);
   }
}
