package org.freebus.knxcomm.serial;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freebus.knxcomm.KNXConnectException;
import org.freebus.knxcomm.KNXConnection;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameType;
import org.freebus.knxcomm.internal.ListenableConnection;

/**
 * An EIB/KNX bus connection to a device that speaks the FT1.2 protocol.
 */
public abstract class Ft12Connection extends ListenableConnection implements KNXConnection
{
   private Logger logger = Logger.getLogger(Ft12Connection.class);

   protected final Semaphore waitAckSemaphore = new Semaphore(0);
   protected boolean connected = false;
   protected int resetPending = 0;

   // FT1.2 end-of-message byte.
   protected final int eofMarker = 0x16;

   // Message counters for frames with valid frame-count bit
   protected int readFcbCount = 0;
   protected int writeFcbCount = 0;

   // FT1.2 acknowledgment message
   protected static final int[] ackMsg = { 0xe5 };

   /**
    * Called by the data transport methods when data is ready to be received.
    *
    * @throws IOException
    */
   protected void dataAvailable() throws IOException
   {
      final int formatByte = read();
      final Ft12FrameFormat format = Ft12FrameFormat.valueOf(formatByte);

      if (format == Ft12FrameFormat.ACK)
      {
         logger.debug("READ: ACK");
         waitAckSemaphore.release();
         return;
      }

      int dataLen = 0;
      int formatByteRepeat = formatByte;

      if (format == Ft12FrameFormat.VARIABLE)
      {
         dataLen = read() - 1;
         read(); // repeated dataLen
         formatByteRepeat = read();
      }

      // Format of the control byte:
      // bit 7: 1=BAU to us, 0=we to BAU
      // bit 6: 1=message from initiating station (i.e. request)
      // bit 5: frame count bit, 0/1 alternating
      // bit 4: frame count bit (bit 5) valid
      // bit 3..0: function code, see Ft12ReceiveFunc / Ft12SendFunc
      final int controlByte = read();

      int checksumCalc = controlByte;
      final int[] data = dataLen > 0 ? new int[dataLen] : null;
      for (int i = 0; i < dataLen; ++i)
      {
         data[i] = read();
         checksumCalc += data[i];
      }

      checksumCalc &= 0xff;
      final int checksum = read();

      final int eofByte = read();
      String errMsg = null;

      if (format == null)
      {
         errMsg = String.format("invalid FT1.2 frame-format 0x%02x", formatByte);
      }
      else if (formatByte != formatByteRepeat)
      {
         errMsg = String.format("invalid FT1.2 repeated frame-format 0x%02x, expected 0x%02x", formatByteRepeat,
               formatByte);
      }
      else if (checksum != checksumCalc)
      {
         errMsg = String.format("wrong FT1.2 frame checksum 0x%02x, expected 0x%02x", checksum, checksumCalc);
      }
      else if (eofByte != eofMarker)
      {
         errMsg = String.format("wrong FT1.2 frame end-marker 0x%02x, expected 0x%02x", eofMarker, eofByte);
      }

      final Ft12Function func = Ft12Function.valueOf(controlByte & 0x0f);

      if (logger.isDebugEnabled() || errMsg != null)
      {
         final StringBuffer sb = new StringBuffer();
         sb.append("READ: ");

         if (errMsg != null)
            sb.append(errMsg).append(": ");

         sb.append(format).append(' ').append(func);
         for (int i = 0; i < dataLen; ++i)
            sb.append(String.format(" %02x", data[i]));

         if (errMsg == null)
            logger.debug(sb);
         else logger.error(errMsg);
      }

      // Send acknowledgment
      if (format == Ft12FrameFormat.VARIABLE)
      {
         logger.debug("WRITE: ACK");
         write(ackMsg, ackMsg.length);
      }

      try
      {
         processFrame(format, func, data);
      }
      catch (Exception e)
      {
         logger.error("failed to process FT1.2 frame", e);
         e.printStackTrace();
      }

   }

   /**
    * Process the FT1.2 frame that {@link #dataAvailable()} has read. This is an
    * internal method that gets called by {@link #dataAvailable()}.
    *
    * @throws IOException
    */
   public void processFrame(final Ft12FrameFormat format, final Ft12Function func, final int[] data) throws IOException
   {
      if (format == Ft12FrameFormat.FIXED)
         processFixedFrame(func);
      else if (format == Ft12FrameFormat.VARIABLE)
         processVariableFrame(func, data);
      else throw new IllegalArgumentException("Unknown FT1.2 frame format: " + format);
   }

   /**
    * Process a FT1.2 frame of the format {Ft12FrameFormat#FIXED}.
    *
    * @param func - the frame function
    *
    * @throws IllegalArgumentException if the function is invalid
    */
   protected void processFixedFrame(final Ft12Function func)
   {
      if (func == Ft12Function.ACK)
      {
         logger.debug("READ: Confirm-ACK");
         if (resetPending > 0)
         {
            --resetPending;
            readFcbCount = 0;
            writeFcbCount = 0;
         }
      }
      else if (func == Ft12Function.NACK)
      {
         logger.debug("READ: Confirm-NACK");
      }
      else if (func == Ft12Function.STATUS_RESP)
      {
         logger.debug("READ: Status response");
      }
      else
      {
         throw new IllegalArgumentException("Unknown fixed-width FT1.2 frame: " + func);
      }
   }

   /**
    * Process a FT1.2 frame of the format {Ft12FrameFormat#VARIABLE}.
    *
    * @throws IOException
    */
   protected void processVariableFrame(final Ft12Function func, final int[] data) throws IOException
   {
      final EmiFrameType msgType = EmiFrameType.valueOf(data[0]);

      final EmiFrame msg = msgType.newInstance();
      if (msg == null)
      {
         throw new IllegalArgumentException("Unknown EMI message: " + msgType);
      }

      msg.fromRawData(data, 0);
      notifyListenersReceived(msg);
   }

   /**
    * @return true if at least one byte can be read.
    * @throws IOException
    */
   protected abstract boolean isDataAvailable() throws IOException;

   /**
    * Connect to the BAU.
    *
    * @throws IOException
    */
   @Override
   public void open() throws IOException
   {
      try
      {
         readFcbCount = 0;
         writeFcbCount = 0;
         resetPending = 0;

         send(Ft12Function.RESET, 30);
      }
      catch (IOException e)
      {
         throw new KNXConnectException("Failed to open a connection to the BAU", e);
      }
   }

   /**
    * Read the next byte from the BAU device. Bytes are in the range 0..255.
    */
   protected abstract int read() throws IOException;

   /**
    * Send a message using a FT1.2 frame with variable length. Waits until the
    * BAU confirms the send.
    */
   @Override
   public void send(EmiFrame message) throws IOException
   {
      final int[] buffer = new int[32];
      StringBuffer sb = new StringBuffer();
      buffer[0] = Ft12FrameFormat.VARIABLE.code;
      // bytes 1+2 contain the data length
      buffer[3] = buffer[0];

      int controlByte = 0x50 | Ft12Function.DATA.code;
      if ((writeFcbCount++ & 1) == 1)
         controlByte |= 0x20;
      buffer[4] = controlByte;

      final int len = message.toRawData(buffer, 5);
      assert (buffer[5] == message.getType().id);
      assert (len >= 1 && len <= 16);

      buffer[1] = len + 1;
      buffer[2] = buffer[1];

      int checksum = controlByte;
      for (int i = 0; i < len; ++i)
         checksum += buffer[i + 5];
      checksum &= 0xff;

      buffer[len + 5] = checksum;
      buffer[len + 6] = eofMarker;

      if (logger.isDebugEnabled())
      {
         sb.append("WRITE: DATA (").append(len).append(" bytes):");
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
    * Send a FT1.2 frame of the type {@link Ft12MessageType#FIXED}. The message
    * is sent 3 times before sending fails if it is not acknowledged.
    *
    * @param func - the function of the frame, see {@link Ft12Function}.
    *
    * @throws IOException
    */
   public void send(final Ft12Function func) throws IOException
   {
      if (func == Ft12Function.RESET)
         ++resetPending;

      send(func, 3);
   }

   /**
    * Send a FT1.2 frame of the type {@link Ft12MessageType#FIXED}.
    *
    * @param func - the function of the frame, see {@link Ft12Function}.
    * @param tries - how many times the sending is repeated until it fails if
    *           not acknowledged.
    *
    * @throws IOException
    */
   public void send(final Ft12Function func, int tries) throws IOException
   {
      final int[] data = new int[4];
      data[0] = Ft12FrameFormat.FIXED.code;
      data[1] = func.code;
      data[2] = data[1];
      data[3] = eofMarker;

      logger.debug("WRITE: " + func.toString());
      writeConfirmed(data, data.length, tries);
   }

   /**
    * Send length bytes of the given data to the BAU.
    */
   protected abstract void write(int[] data, int length) throws IOException;

   /**
    * Send length bytes of the given data to the BAU and wait for an acknowledge
    * (ACK). Retries up to <code>tries</code> times if no acknowledge is
    * received.
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

      throw new IOException("Failed to send data: no ACK from the BAU received (" + tries + " tries)");
   }
}
