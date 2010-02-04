package org.freebus.knxcomm.serial;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

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
   private Logger logger = Logger.getLogger(getClass());

   protected final CopyOnWriteArrayList<EmiFrameListener> listeners = new CopyOnWriteArrayList<EmiFrameListener>();
   protected boolean connected = false;
   protected boolean debug = true;

   // Message counters
   protected int readMsgCount = 0;
   protected int writeMsgCount = 0;
   protected int ackCount = 0;

   // FT1.2 reset message
   protected static final int[] resetMsg = { 0x10, 0x40, 0x40, 0x16 };

   // FT1.2 request status message
   protected static final int[] statusReqMsg = { 0x10, 0x49, 0x49, 0x16 };

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
    * Remove a bus listener.
    */
   @Override
   public void removeListener(EmiFrameListener listener)
   {
      listeners.add(listener);
   }

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
    * Connect to the device.
    * @throws IOException 
    */
   @Override
   public void open() throws IOException
   {
      logger.debug("WRITE: FT1.2-Reset");

      // Send resets until the device acknowledges
      final int startAckCount = ackCount;
      for (int i = 50; i > 0 && ackCount == startAckCount; --i)
      {
         logger.debug("WRITE: Reset");
         write(resetMsg, resetMsg.length);

         try
         {
            Thread.sleep(100);
         }
         catch (InterruptedException e)
         {
         }
      }

      if (startAckCount == ackCount)
         throw new KNXConnectException("Device not found");

      // Send a status request
      logger.debug("WRITE: Status request");
      write(statusReqMsg, statusReqMsg.length);
   }

   /**
    * Send a message using a FT1.2 frame with variable length.
    */
   @Override
   public void send(EmiFrame message) throws IOException
   {
      final int[] buffer = new int[32];
      StringBuffer sb = new StringBuffer();
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

      if (logger.isDebugEnabled())
      {
         sb.append("WRITE: DATA");
         for (int i = 0; i < len + 7; ++i)
            sb.append(' ').append(Integer.toHexString(buffer[i]));
         logger.debug(sb.toString());
      }

      //write(buffer, len + 7);
		final int startAckCount = ackCount;
		for (int i = 3; i > 0 && ackCount == startAckCount; --i) {
			if (logger.isDebugEnabled())
				logger.debug(sb.toString());
			write(buffer, len + 7);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		if (startAckCount == ackCount) {
			throw new KNXConnectException("Device not found");
		} else {
			notifyListenersSent(message);
		}
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
            logger.debug("READ: ACK [0xe5]");
            ++ackCount;
            break;

         case 0x68: // Message with variable length
            readMessage();
            break;

         case 0x10: // Frame with fixed length
            int cmd = read();
            final int checksum = read();
            final int end = read();
            if (cmd != checksum || end != 0x16)
            {
               logger.info(" Malformed!");
               break;
            }

            // Bits of the command:
            // 7: direction - 1: BAU to us, 0: we to BAU
            // 6: primary message - 0: message from secondary (responding) station
            // 5: reserved
            // 4: data flow control (not used)
            // 3..0: function code

            cmd &= 0xf;
            if (cmd == 0)
            {
               logger.debug("READ: Confirm ACK");
               readMsgCount = 0;  // Assume it was a Reset reply
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
            else logger.error(" Unknown fixed-width frame 0x" + Integer.toHexString(cmd));
            break;

         default: // Unknown frame
            final StringBuffer sb = new StringBuffer();
            sb.append("READ:  UNKNOWN [").append(Integer.toHexString(type)).append(']');
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
               sb.append(' ').append(Integer.toHexString(val));
            logger.error(sb.toString());
            break;
      }

      // Send acknowledge
      if (type != 0xe5)
      {
//         logger.debug("WRITE: ACK [0xe5]");
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

      String logMsg = "READ: DATA [0x68] (" + Integer.toString(dataLen) + " bytes): ";
      String err = "";

      ++readMsgCount;
      if (read() != 0x68) err += "|no boundary marker";

      int controlByte = read();
      if (controlByte != 0xf3 && controlByte != 0xd3)
         err += "|no control byte 0x" + Integer.toHexString(controlByte);

      int ftCheckSum = controlByte;
      final int[] data = new int[dataLen];
      for (int i = 0; i < dataLen; ++i)
      {
         data[i] = read();
         logMsg += ' ' + Integer.toHexString(data[i]);
         ftCheckSum += data[i];
      }

      final int checksum = read();
      if (checksum != (ftCheckSum & 0xff)) err += "|FT checksum error";

      final int eofMarker = read();
      if (eofMarker != 0x16) err += "|no eof-marker 0x" + Integer.toHexString(eofMarker);

      if (err.length() > 0)
      {
         logger.error(logMsg + err.substring(1));
      }
      else
      {
         logger.debug(logMsg);
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
