package org.freebus.knxcomm.emi;

import org.freebus.knxcomm.telegram.InvalidDataException;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * A Bus-monitor message. This is the type of message that encapsulates
 * telegrams from the KNX/EIB bus in bus-monitor mode.
 */
public class L_Busmon_ind extends EmiTelegramFrame
{
   // Status
   // 7: frame error (in the frame bits)
   // 6: bit error (invalid bit in the frame characters)
   // 5: parity error (in the frame bits)
   // 4: overflow
   // 3: lost flag (at least one frame or frame piece is lost by the bus monitor)
   // 2..0: sequence number
   private int status;

   // Time stamp in BAU timer units
   private int timestamp;

   /**
    * Create a L_Busmon.ind message with the given encapsulated telegram.
    */
   protected L_Busmon_ind(Telegram telegram)
   {
      super(EmiFrameType.L_BUSMON_IND, telegram);
   }

   /**
    * Create an empty L_Busmon.ind message.
    */
   protected L_Busmon_ind()
   {
      this(new Telegram());
   }

   /**
    * Set the message status byte.
    */
   public void setStatus(int status)
   {
      this.status = status;
   }

   /**
    * @return the message status byte.
    */
   public int getStatus()
   {
      return status;
   }

   /**
    * Set the time-stamp, in seconds since 1970-01-01 00:00:00.
    *
    * @param timestamp - the timestamp to set
    *
    * @see #getTimestamp()
    */
   public void setTimestamp(int timestamp)
   {
      this.timestamp = timestamp;
   }

   /**
    * Returns the time-stamp. This is a 16bit value that contains the time taken
    * when the frame's control field is completely received. The timer is an
    * internal counter of the BAU - the time unit depends on the clock rate of the
    * BAU micro controller.
    *
    * @return the time-stamp.
    */
   public int getTimestamp()
   {
      return timestamp;
   }

   /**
    * Initialize the message from the given raw data, beginning at start. The
    * first byte is expected to be the EMI message type.
    *
    * @throws InvalidDataException
    */
   @Override
   public void fromRawData(int[] rawData, int start) throws InvalidDataException
   {
      ++start; // skip the message type
      setStatus(rawData[start++]);
      setTimestamp((rawData[start++] << 8) | rawData[start++]);
      telegram.fromRawData(rawData, start);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      rawData[start++] = type.code;
      rawData[start++] = status & 0xff;
      rawData[start++] = (timestamp >> 8) & 0xff;
      rawData[start++] = timestamp & 0xff;
      return telegram.toRawData(rawData, start) + 4;
   }

   /**
    * @return the message in a human readable form.
    */
   @Override
   public String toString()
   {
      return getTypeString() + telegram.toString();
   }
}