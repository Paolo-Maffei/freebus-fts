package org.freebus.knxcomm.emi;

import org.freebus.knxcomm.telegram.InvalidDataException;
import org.freebus.knxcomm.telegram.Telegram;

/**
 *  Base class for EMI frames that contain a {@link Telegram} telegram.
 *  Used for e.g. {@link L_Data_ind},  {@link L_Busmon_ind}.
 */
public abstract class EmiTelegramFrame extends EmiMessageBase
{
   // The encapsulated telegram
   protected final Telegram telegram;

   /**
    * Create a frame object with the given telegram.
    */
   protected EmiTelegramFrame(EmiFrameType type, Telegram telegram)
   {
      super(type);
      this.telegram = telegram; 
   }

   /**
    * Create an empty frame object.
    */
   protected EmiTelegramFrame(EmiFrameType type)
   {
      this(type, new Telegram());
   }

   /**
    * @return the contained telegram.
    */
   public Telegram getTelegram()
   {
      return telegram;
   }

   /**
    * Initialize the message from the given raw data, beginning at start. The
    * first byte is expected to be the EMI message type.
    * 
    * @throws InvalidDataException 
    */
   public void fromRawData(int[] rawData, int start) throws InvalidDataException
   {
      telegram.fromRawData(rawData, start + 1);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      rawData[start] = type.id;
      final int length = telegram.toRawData(rawData, start + 1);
      return length + 1;
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
