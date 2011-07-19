package org.freebus.knxcomm.emi;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.freebus.knxcomm.emi.types.EmiFrameType;
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
   // 3: lost flag (at least one frame or frame piece is lost by the bus
   // monitor)
   // 2..0: sequence number
   private int status;

   // Time stamp in BAU timer units
   private int timestamp;

   /**
    * Create a L_Busmon.ind message with the given encapsulated telegram.
    * 
    * @param telegram - the contained telegram.
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
    * 
    * @param status - the status byte.
    *
    * @see #getStatus()
    */
   public void setStatus(int status)
   {
      this.status = status;
   }

   /**
    * Returns the status. The status bit indicate various error conditions:
    *
    * <ul>
    * <li>bit 7: frame error (in the frame bits)
    * <li>bit 6: bit error (invalid bit in the frame characters)
    * <li>bit 5: parity error (in the frame bits)
    * <li>bit 4: overflow
    * <li>bit 3: lost flag (at least one frame or frame piece is lost by the bus monitor)
    * <li>bits 2..0: sequence number
    * </ul>
    *
    * @return the message status byte.
    */
   public int getStatus()
   {
      return status;
   }

   /**
    * Set the time stamp, in seconds since 1970-01-01 00:00:00.
    *
    * @param timestamp - the time stamp to set
    *
    * @see #getTimestamp()
    */
   public void setTimestamp(int timestamp)
   {
      this.timestamp = timestamp;
   }

   /**
    * Returns the time stamp. This is a 16-bit value that contains the time
    * taken when the frame's control field is completely received. The timer is
    * an internal counter of the BAU - the time unit depends on the clock rate
    * of the BAU micro controller.
    *
    * @return the time-stamp.
    */
   public int getTimestamp()
   {
      return timestamp;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void readData(DataInput in) throws IOException
   {
      setStatus(in.readUnsignedByte());
      setTimestamp(in.readUnsignedShort());
      super.readData(in);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeData(DataOutput out) throws IOException
   {
      out.write(getStatus());
      out.writeShort(getTimestamp());
      super.writeData(out);
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
