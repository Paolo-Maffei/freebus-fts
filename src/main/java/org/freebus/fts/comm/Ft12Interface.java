package org.freebus.fts.comm;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.freebus.fts.emi.EmiMessage;
import org.freebus.fts.emi.EmiMessageType;
import org.freebus.fts.emi.PEI_Switch;

/**
 * An EIB/KNX bus connection to a device that speaks the FT1.2 protocol.
 */
public abstract class Ft12Interface implements BusInterface
{
   protected final CopyOnWriteArrayList<BusListener> listeners = new CopyOnWriteArrayList<BusListener>();
   protected boolean connected = false;
   protected boolean debug = true;

   // Message counters
   protected int readMsgCount = 0;
   protected int writeMsgCount = 0;

   // FT1.2 reset message
   protected static final int[] resetMsg = { 0x10, 0x40, 0x40, 0x16 };

   // FT1.2 acknowledge message
   protected static final int[] ackMsg = { 0xe5 };

   /**
    * Add a listener that is informed when messages are received.
    */
   @Override
   public void addListener(BusListener listener)
   {
      listeners.add(listener);
   }

   /**
    * Remove a message listener.
    */
   @Override
   public void removeListener(BusListener listener)
   {
      listeners.add(listener);
   }

   /**
    * Notify all listeners about the given message.
    */
   public void notifyListeners(final EmiMessage message)
   {
      for (BusListener listener : listeners)
         listener.messageReceived(message);
   }

   /**
    * Connect to the device.
    * @throws IOException 
    */
   @Override
   public void open() throws IOException
   {
      if (debug) System.out.println("WRITE: FT1.2-Reset x3");
      write(resetMsg, resetMsg.length);
      write(resetMsg, resetMsg.length);
      write(resetMsg, resetMsg.length);

      if (debug) System.out.println("WRITE: PEI_Switch to link-layer mode");
      write(new PEI_Switch.req(PEI_Switch.Mode.LINK));
   }

   /**
    * Send a message using a FT1.2 frame with variable length.
    */
   @Override
   public void write(EmiMessage message) throws IOException
   {
      final int[] buffer = new int[32];

      buffer[0] = 0x68;
      // 1,2 contain the length
      buffer[3] = 0x68;
      buffer[4] = (++writeMsgCount & 1) == 1 ? 0x73 : 0x53;

      final int len = message.toRawData(buffer, 5);
      assert (buffer[5] == message.getType().id);
      assert (len >= 1 && len <= 16);

      buffer[1] = len + 1;
      buffer[2] = buffer[1];

      int checksum = 0;
      for (int i = -1; i < len; ++i)
         checksum += buffer[i + 5];

      buffer[len + 5] = checksum & 0xff;
      buffer[len + 6] = 0x16;

      if (debug)
      {
         System.out.print("WRITE: DATA");
         for (int i = 0; i < len + 7; ++i)
            System.out.printf(" %02x", buffer[i]);
         System.out.println();
      }

      write(buffer, len + 7);
   }

   /**
    * Called by the data transport methods when data is ready to be received.
    * 
    * @throws IOException
    */
   protected void dataAvailable() throws IOException
   {
      final int type = read();

      switch (type)
      {
         case 0xe5: // Acknowledge to the last frame we sent
            System.out.println("READ:  ACK [0xe5]");
            break;

         case 0x68: // Message with variable length
            readMessage();
            break;

         case 0x10: // Frame with fixed length
            final int cmd = read();
            final int checksum = read();
            final int end = read();
            if (cmd != checksum || end != 0x16) System.out.println(" Malformed!");
            else if (cmd == 0xC0)
            {
               System.out.print("READ:  Reset.ind");
               readMsgCount = 0;
               writeMsgCount = 0;
            }
            else System.out.println(" Unknown fixed-width frame 0x" + Integer.toHexString(cmd));
            break;

         default: // Unknown frame
            System.out.print("READ:  UNKNOWN [0x" + Integer.toHexString(type) + "]");
            try
            {
               Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }
            int val;
            while (isDataAvailable() && (val = read()) != 0x16)
               System.out.printf(" %02x", val);
            break;
      }

      // Send acknowledge
      if (type != 0xe5)
      {
//         if (debug) System.out.println("WRITE: ACK [0xe5]");
         write(ackMsg, ackMsg.length);
      }
   }

   /**
    * Read a FT1.2 data frame.
    * 
    * @throws IOException
    */
   protected void readMessage() throws IOException
   {
      final int dataLen = read() - 1;
      read(); // dataLen repeated
      System.out.print("READ:  DATA [0x68] (" + Integer.toString(dataLen) + " bytes): ");
      String err = "";

      ++readMsgCount;
      if (read() != 0x68) err = err + "|Invalid boundary marker";

      int controlByte = read();
      if (controlByte != 0xf3 && controlByte != 0xd3)
         err = err + "|Invalid control byte 0x" + Integer.toHexString(controlByte);

      int ftCheckSum = controlByte;
      final int[] data = new int[dataLen];
      for (int i = 0; i < dataLen; ++i)
      {
         data[i] = read();
         System.out.printf(" %02x", data[i]);
         ftCheckSum += data[i];
      }

      final int checksum = read();
      if (checksum != (ftCheckSum & 0xff)) err = err + "|FT checksum error";

      final int eofMarker = read();
      if (eofMarker != 0x16) err = err + "|Invalid eof-marker 0x" + Integer.toHexString(eofMarker);

      if (err.length() > 0) System.out.println(" ERROR:" + err.substring(1));
      else
      {
         System.out.println();

         try
         {
            final EmiMessageType msgType = EmiMessageType.valueOf(data[0]);

            final EmiMessage msg = msgType.newInstance();
            if (msg == null)
            {
               System.out.println("Ignoring unimplemented message " + msgType.toString());
            }
            else
            {
               msg.fromRawData(data, 0);
               notifyListeners(msg);
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * Send length bytes of the given data to the device.
    */
   protected abstract void write(int[] data, int length) throws IOException;

   /**
    * Read the next byte from the device. Bytes are in the range 0..255.
    */
   protected abstract int read() throws IOException;

   /**
    * @return true if at least one byte can be read.
    * @throws IOException
    */
   protected abstract boolean isDataAvailable() throws IOException;
}
