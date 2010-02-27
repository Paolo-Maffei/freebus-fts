package org.freebus.knxcomm.serial;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freebus.knxcomm.KNXConnectException;
import org.freebus.knxcomm.KNXConnection;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameListener;
import org.freebus.knxcomm.emi.EmiFrameType;

/**
 * An EIB/KNX bus connection to a device that speaks the FT1.2 protocol.
 */
public abstract class Ft12Connection implements KNXConnection
{
   private Logger logger = Logger.getLogger(Ft12Connection.class);

   protected final CopyOnWriteArrayList<EmiFrameListener> listeners = new CopyOnWriteArrayList<EmiFrameListener>();
   protected final Semaphore waitAckSemaphore = new Semaphore(0);
   protected boolean connected = false;
   protected boolean debug = true;

   // FT1.2 end-of-message byte.
   protected final int eofMarker = 0x16;

   // Message counters
   protected int readMsgCount = 0;
   protected int writeMsgCount = 0;
   protected int ackCount = 0;

   // FT1.2 acknowledge message
   protected static final int[] ackMsg = { 0xe5 };

   /**
    * Add a bus listener. Listeners get called when messages arrive.
    */
   @Override
   public void addListener(EmiFrameListener listener)
   {
      listeners.add(listener);
   }

   /**
    * Called by the data transport methods when data is ready to be received.
    * 
    * @throws IOException
    */
   protected void dataAvailable() throws IOException
   {
      final int typeCode = read();
      final Ft12MessageType type = Ft12MessageType.valueOf(typeCode);

      if (type == Ft12MessageType.ACK)
      {
         logger.debug("READ: ACK");
         waitAckSemaphore.release();
      }
      else if (type == Ft12MessageType.LONG)
      {
         readMessage();
      }
      else if (type == Ft12MessageType.SHORT)
      {
         readShortMessage();
      }
      else
      {
         final StringBuffer sb = new StringBuffer();
         sb.append(String.format("READ: UNKNOWN [%02x]", typeCode));
         try
         {
            Thread.sleep(500);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
         int val;
         while (isDataAvailable() && (val = read()) != eofMarker)
            sb.append(String.format(" %02x", val));
         logger.error(sb.toString());
      }

      // Send acknowledge
      if (type != Ft12MessageType.ACK)
      {
         // logger.debug("WRITE: ACK [0xe5]");
         write(ackMsg, ackMsg.length);
      }
   }

   /**
    * @return true if at least one byte can be read.
    * @throws IOException
    */
   protected abstract boolean isDataAvailable() throws IOException;

   /**
    * Notify all listeners that the given message was received.
    */
   public void notifyListenersReceived(final EmiFrame message)
   {
      for (EmiFrameListener listener : listeners)
         listener.frameReceived(message);
   }

   /**
    * Notify all listeners that the given message was sent.
    */
   public void notifyListenersSent(final EmiFrame message)
   {
      for (EmiFrameListener listener : listeners)
         listener.frameSent(message);
   }

   /**
    * Connect to the BCU.
    * 
    * @throws IOException
    */
   @Override
   public void open() throws IOException
   {
      try
      {
         send(Ft12ShortMessage.RESET, 30);
      }
      catch (IOException e)
      {
         throw new KNXConnectException("Failed to open a connection to the BCU", e);
      }
   }

   /**
    * Read the next byte from the BCU device. Bytes are in the range 0..255.
    */
   protected abstract int read() throws IOException;

   /**
    * Read a FT1.2 message.
    * 
    * @throws IOException
    */
   protected void readMessage() throws IOException
   {
      final int dataLen = read() - 1;
      read(); // dataLen repeated

      String err = "";
      final StringBuffer logMsg = new StringBuffer();
      logMsg.append("READ: DATA [0x68] (").append(dataLen).append(" bytes):");

      ++readMsgCount;
      if (read() != Ft12MessageType.LONG.code)
         err += "|no boundary marker";

      int controlByte = read();
      if (controlByte != 0xf3 && controlByte != 0xd3)
         err += "|no control byte 0x" + String.format("%02x", controlByte);

      int ftCheckSum = controlByte;
      final int[] data = new int[dataLen];
      for (int i = 0; i < dataLen; ++i)
      {
         data[i] = read();
         logMsg.append(' ').append(String.format("%02x", data[i]));
         ftCheckSum += data[i];
      }

      final int checksum = read();
      logMsg.append(String.format(" (checksum %02x)", checksum));
      if (checksum != (ftCheckSum & 0xff))
         err += "|FT checksum error";

      if (read() != eofMarker)
         err += "|no eof-marker";

      if (err.length() > 0)
      {
         logMsg.append(' ').append(err.substring(1));
         logger.error(logMsg.toString());
      }
      else
      {
         logger.debug(logMsg.toString());
         try
         {
            final EmiFrameType msgType = EmiFrameType.valueOf(data[0]);

            final EmiFrame msg = msgType.newInstance();
            if (msg == null)
            {
               System.out.println("Ignoring unimplemented message " + msgType.toString());
            }
            else
            {
               msg.fromRawData(data, 0);
               notifyListenersReceived(msg);
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * Read a short FT1.2 message.
    * 
    * @throws IOException
    */
   protected void readShortMessage() throws IOException
   {
      int cmd = read();
      final int checksum = read();
      final int end = read();
      if (cmd != checksum || end != 0x16)
      {
         logger.error("READ: Malformed short message");
         return;
      }

      // Bits of the command:
      // 7: direction - 1: BAU to us, 0: we to BAU
      // 6: primary message - 0: message from secondary (responding)
      // station
      // 5: reserved
      // 4: data flow control (not used)
      // 3..0: function code

      cmd &= 0xf;
      if (cmd == 0)
      {
         logger.debug("READ: Confirm ACK");
         readMsgCount = 0; // Assume it was a Reset reply
         writeMsgCount = 0;
      }
      else if (cmd == 1)
      {
         logger.debug("READ: Confirm NACK !");
      }
      else if (cmd == 0xb)
      {
         logger.debug("READ: Status response");
      }
      else logger.error(" Unknown fixed-width frame 0x" + String.format("%02x", cmd));
   }

   /**
    * Remove a bus listener.
    */
   @Override
   public void removeListener(EmiFrameListener listener)
   {
      listeners.add(listener);
   }

   /**
    * Send a message using a FT1.2 frame with variable length. Waits until the
    * BCU confirms the send.
    */
   @Override
   public void send(EmiFrame message) throws IOException
   {
      final int[] buffer = new int[32];
      StringBuffer sb = new StringBuffer();
      buffer[0] = Ft12MessageType.LONG.code;
      // bytes 1+2 contain the data length
      buffer[3] = buffer[0];
      buffer[4] = (++writeMsgCount & 1) == 1 ? 0x73 : 0x53;

      final int len = message.toRawData(buffer, 5);
      assert (buffer[5] == message.getType().id);
      assert (len >= 1 && len <= 16);

      buffer[1] = len + 1;
      buffer[2] = buffer[1];

      int checksum = 0;
      for (int i = -1; i < len; ++i)
         checksum += buffer[i + 5];
      checksum &= 0xff;

      buffer[len + 5] = checksum;
      buffer[len + 6] = eofMarker;

      if (logger.isDebugEnabled())
      {
         sb.append("WRITE: DATA [0x68] (").append(len).append(" bytes):");
         for (int i = 5; i < len + 5; ++i)
            sb.append(String.format(" %02x", buffer[i]));
         sb.append(String.format(" (checksum %02x)", checksum));
      }

      notifyListenersSent(message);

      if (logger.isDebugEnabled())
         logger.debug(sb.toString());

      writeConfirmed(buffer, len + 7, 3);
   }

   /**
    * Send a short FT1.2 message of the type {@link Ft12MessageType#SHORT}. The
    * message is sent 3 times before sending fails if not acknowledged.
    * 
    * @param type - the type of the short message, see {@link Ft12ShortMessage}.
    * 
    * @throws IOException
    */
   public void send(final Ft12ShortMessage type) throws IOException
   {
      send(type, 3);
   }

   /**
    * Send a short FT1.2 message of the type {@link Ft12MessageType#SHORT}.
    * 
    * @param type - the type of the short message, see {@link Ft12ShortMessage}.
    * @param tries - how many times the sending is repeated until it fails if
    *           not acknowledged.
    * 
    * @throws IOException
    */
   protected void send(final Ft12ShortMessage type, int tries) throws IOException
   {
      final int[] data = new int[4];
      data[0] = Ft12MessageType.SHORT.code;
      data[1] = type.code;
      data[2] = data[1];
      data[3] = eofMarker;

      logger.debug("WRITE: " + type.toString());
      writeConfirmed(data, data.length, tries);
   }

   /**
    * Send length bytes of the given data to the BCU device.
    */
   protected abstract void write(int[] data, int length) throws IOException;

   /**
    * Send length bytes of the given data to the BCU device and wait for an
    * acknowledge (ACK). Retries up to <code>tries</code> times if no
    * acknowledge is received.
    * 
    * @param data is the data to be sent.
    * @param len is the number of bytes of <code>data</code> that are sent.
    * @param tries is the maximum number of times the sending is retried.
    *           Usually 3.
    * 
    * @throw IOException if no acknowledge is received at all or the write fails
    */
   private synchronized final void writeConfirmed(final int[] data, int len, int tries) throws IOException
   {
      waitAckSemaphore.drainPermits();
      for (int turn = 0; turn < tries; ++turn)
      {
         write(data, len);

         try
         {
            if (waitAckSemaphore.tryAcquire(100, TimeUnit.MILLISECONDS))
               return;
         }
         catch (InterruptedException e)
         {
            logger.debug("wait for semaphore interrupted", e);
         }
      }

      throw new IOException("Failed to send data: no ACK from the BCU received (" + tries + " tries)");
   }
}
