package org.freebus.knxcomm.serial;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freebus.fts.common.HexString;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.KNXConnectException;
import org.freebus.knxcomm.KNXConnection;
import org.freebus.knxcomm.LinkMode;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameFactory;
import org.freebus.knxcomm.emi.PEI_Identify_con;
import org.freebus.knxcomm.emi.PEI_Identify_req;
import org.freebus.knxcomm.emi.PEI_Switch_req;
import org.freebus.knxcomm.emi.types.EmiFrameType;
import org.freebus.knxcomm.emi.types.PEISwitchMode;
import org.freebus.knxcomm.internal.ListenableConnection;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * An EIB/KNX bus connection to a device that speaks the FT1.2 protocol.
 */
public abstract class Ft12Connection extends ListenableConnection implements KNXConnection
{
   private Logger logger = Logger.getLogger(Ft12Connection.class);

   protected final Semaphore waitAckSemaphore = new Semaphore(0);
   private PhysicalAddress busAddr = PhysicalAddress.NULL;
   private LinkMode mode;
   protected boolean connected;
   protected int resetPending;

   // Enable to get FT1.2 frame data and ACK debug output
   private boolean debugFT12 = true;

   // Enable debug output for FT1.2 acknowledges
   private boolean debugFT12ack = true;

   // FT1.2 end-of-message byte.
   protected final int eofMarker = 0x16;

   // Message counters for frames with valid frame-count bit
   protected int readFcbCount;
   protected int writeFcbCount;

   // FT1.2 acknowledgment message
   protected static final byte[] ackMsg = { (byte) Ft12FrameFormat.ACK.code };

   /**
    * {@inheritDoc}
    */
   @Override
   public void open(LinkMode mode) throws IOException
   {
      try
      {
         readFcbCount = 0;
         writeFcbCount = 0;
         resetPending = 0;
         connected = false;
         busAddr = PhysicalAddress.NULL;

         send(Ft12Function.RESET, 30);
         Thread.sleep(100);

         // Identify the BCU
         send(new PEI_Identify_req());
         Thread.sleep(100);

         // Switch to the proper mode
         setLinkMode(mode);
         Thread.sleep(100);

         connected = true;
      }
      catch (IOException e)
      {
         throw new KNXConnectException("Failed to open a connection to the BAU", e);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * {@inheritDoc}
    */
   public void setLinkMode(LinkMode mode) throws IOException
   {
      PEISwitchMode switchMode;

      if (mode == LinkMode.LinkLayer)
         switchMode = PEISwitchMode.LINK;
      else if (mode == LinkMode.BusMonitor)
         switchMode = PEISwitchMode.BUSMON;
      else throw new IllegalArgumentException("invalid link mode " + mode);

      Logger.getLogger(getClass()).debug("Activating link mode " + mode);
      this.mode = mode;
      send(new PEI_Switch_req(switchMode));
   }

   /**
    * {@inheritDoc}
    */
   public LinkMode getLinkMode()
   {
      return mode;
   }

   /**
    * Called by the data transport methods when data is ready to be received.
    *
    * @throws IOException
    */
   protected void dataAvailable() throws IOException
   {
      final int formatCode = read();
      if (formatCode == -1) // EOF
         return;

      final Ft12FrameFormat format = Ft12FrameFormat.valueOf(formatCode);
      if (format == Ft12FrameFormat.ACK)
      {
         if (debugFT12ack)
            logger.debug("READ: ACK");
         waitAckSemaphore.release();
      }
      else if (format == Ft12FrameFormat.FIXED)
      {
         final byte[] data = new byte[4];
         data[0] = (byte) format.code;
         read(data, 1, 3);

         if (debugFT12 && logger.isDebugEnabled())
            logger.debug("READ FT1.2: " + HexString.toString(data));

         processFixedFrame(data);
      }
      else if (format == Ft12FrameFormat.VARIABLE)
      {
         final int len = read();
         final byte[] data = new byte[len + 6];
         data[0] = (byte) format.code;
         data[1] = (byte) len;
         read(data, 2, len + 4);

         if (debugFT12 && logger.isDebugEnabled())
            logger.debug("READ FT1.2: " + HexString.toString(data));

         if (!connected)
            return;

         try
         {
            processVariableFrame(data);
         }
         catch (IOException e)
         {
            if (!debugFT12 && logger.isDebugEnabled())
               logger.debug("READ FT1.2: " + HexString.toString(data));

            throw e;
         }
      }
      else
      {
         throw new InvalidDataException("Invalid FT1.2 frame format", formatCode);
      }
   }

   /**
    * Process a FT1.2 frame of the format {Ft12FrameFormat#FIXED}.
    *
    * @param data - the raw data of the frame
    *
    * @throws InvalidDataException if the function is unknown
    */
   protected void processFixedFrame(final byte[] data) throws InvalidDataException
   {
      final Ft12Function func = Ft12Function.valueOf(data[1] & 15);

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
         throw new InvalidDataException("Unknown fixed-width FT1.2 frame", data[1] & 255);
      }
   }

   /**
    * Process a FT1.2 frame of the format {Ft12FrameFormat#VARIABLE}.
    *
    * @param data - the raw data of the frame
    *
    * @throws IOException
    */
   protected void processVariableFrame(final byte[] data) throws IOException
   {
      // Format of the control byte:
      // bit 7: 1=BAU to us, 0=we to BAU
      // bit 6: 1=message from initiating station (i.e. request)
      // bit 5: frame count bit, 0/1 alternating
      // bit 4: frame count bit (bit 5) valid
      // bit 3..0: function code, see Ft12ReceiveFunc / Ft12SendFunc
      final int controlByte = data[4] & 255;

      final int dataLen = data[1] & 255;
      if (dataLen != (data[2] & 255))
         throw new InvalidDataException("invalid FT1.2 length checksum", data[2] & 255);

      final int formatByteRepeat = data[3] & 255;
      if (formatByteRepeat != Ft12FrameFormat.VARIABLE.code)
         throw new InvalidDataException("invalid FT1.2 repeated frame-format", formatByteRepeat);

      int checksumCalc = 0;
      for (int i = 0; i < dataLen; ++i)
         checksumCalc += data[i + 4];
      checksumCalc &= 255;

      final int checksum = data[data.length - 2] & 255;
      if (checksum != checksumCalc)
         throw new InvalidDataException("wrong FT1.2 frame checksum", checksum & 255);

      final int endByte = data[data.length - 1];
      if (endByte != eofMarker)
         throw new InvalidDataException("wrong FT1.2 frame end-marker", endByte);

      final byte[] frameData = Arrays.copyOfRange(data, 5, dataLen + 4);
      if (logger.isDebugEnabled())
         logger.debug("READ: " + HexString.toString(frameData) + String.format(" (checksum %02x)", checksum));

      // Send acknowledgment
      if (debugFT12ack)
         logger.debug("SEND: ACK");
      write(ackMsg, ackMsg.length);

      final Ft12Function func = Ft12Function.valueOf(controlByte & 15);
      if (func == Ft12Function.DATA)
      {
         final EmiFrame frame = EmiFrameFactory.createFrame(frameData);
         if (frame == null)
            throw new InvalidDataException("Unknown EMI frame type", data[0] & 255);

         processEmiFrame(frame);
      }
      else
      {
         throw new InvalidDataException("invalid FT1.2 variable-width function", func == null ? 0 : func.code);
      }
   }

   /**
    * Process an {@link EmiFrame EMI frame}.
    *
    * @param frame - the frame to process
    *
    * @throws IOException
    */
   protected void processEmiFrame(final EmiFrame frame) throws IOException
   {
      final EmiFrameType type = frame.getType();
      if (type == EmiFrameType.PEI_IDENTIFY_CON)
         busAddr = ((PEI_Identify_con) frame).getAddr();

      notifyListenersReceived(frame);
   }

   /**
    * @return true if at least one byte can be read.
    * @throws IOException
    */
   protected abstract boolean isDataAvailable() throws IOException;

   /**
    * Read the next byte from the BAU device. Bytes are in the range 0..255.
    */
   protected abstract int read() throws IOException;

   /**
    * Read <code>length</code> bytes into the data buffer, starting at
    * <code>pos</code>.
    *
    * @param data - the data buffer to read into
    * @param pos - the start index
    * @param length - the number of bytes to read
    *
    * @throws IOException
    * @throws EOFException
    */
   protected void read(byte[] data, int pos, int length) throws IOException
   {
      int ch;

      while (length > 0)
      {
         ch = read();
         if (ch < 0)
            throw new EOFException();

         data[pos++] = (byte) ch;
         --length;
      }
   }

   /**
    * Send a message using a FT1.2 frame with variable length. Waits until the
    * BAU confirms the send.
    */
   @Override
   public void send(EmiFrame frame) throws IOException
   {
      final byte[] data = new byte[280];
      data[0] = (byte) Ft12FrameFormat.VARIABLE.code;
      // bytes 1+2 contain the data length
      data[3] = data[0];

      int controlByte = 0x50 | Ft12Function.DATA.code;
      if ((writeFcbCount++ & 1) == 1)
         controlByte |= 0x20;
      data[4] = (byte) controlByte;

      final byte[] frameData = frame.toByteArray();
      final int len = frameData.length;

      for (int i = 0; i < len; ++i)
         data[i + 5] = frameData[i];

      data[1] = (byte) (len + 1);
      data[2] = data[1];

      int checksum = controlByte;
      for (int i = 0; i < len; ++i)
         checksum += data[i + 5];
      checksum &= 0xff;

      data[len + 5] = (byte) checksum;
      data[len + 6] = eofMarker;

      if (logger.isDebugEnabled())
      {
         logger.debug("SEND: " + HexString.toString(data, 5, len) + String.format(" (checksum %02x)", checksum));

         if (debugFT12)
            logger.debug("SEND FT1.2: " + HexString.toString(data, 0, len + 7));
      }

      notifyListenersSent(frame);

      writeConfirmed(data, len + 7, 3);
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
      final byte[] data = new byte[4];
      data[0] = (byte) Ft12FrameFormat.FIXED.code;
      data[1] = (byte) func.code;
      data[2] = data[1];
      data[3] = (byte) eofMarker;

      logger.debug("SEND: " + func.toString());
      writeConfirmed(data, data.length, tries);
   }

   /**
    * Send length bytes of the given data to the BAU.
    */
   protected abstract void write(byte[] data, int length) throws IOException;

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
   private synchronized final void writeConfirmed(final byte[] data, int len, int tries) throws IOException
   {
      for (int turn = 0; turn < tries; ++turn)
      {
         write(data, len);

         try
         {
            if (waitAckSemaphore.tryAcquire(250, TimeUnit.MILLISECONDS))
               return;
         }
         catch (InterruptedException e)
         {
            logger.debug("wait for semaphore interrupted", e);
         }
      }

      throw new IOException("Failed to send data: no ACK from the BAU received (" + tries + " tries)");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PhysicalAddress getPhysicalAddress()
   {
      return busAddr;
   }
}
