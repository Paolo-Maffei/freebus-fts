package org.freebus.fts.comm.emi;

import org.freebus.fts.eib.InvalidDataException;
import org.freebus.fts.eib.Telegram;

/**
 * Link-data message types. This is the type of message that encapsulates
 * EIB bus telegrams, for sending and receiving telegrams.
 */
public final class L_Data
{
   /**
    * Base class for link-data messages.
    */
   public static abstract class base extends EmiMessageBase
   {
      // The encapsulated telegram
      protected final Telegram telegram;

      /**
       * Create a L_Data message with the given telegram.
       */
      protected base(EmiFrameType type, Telegram telegram)
      {
         super(type);
         this.telegram = telegram; 
      }

      /**
       * Create an empty L_Data message.
       */
      protected base(EmiFrameType type)
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

   /**
    * Link data request. This message type is used to send telegrams to the EIB
    * bus.
    */
   public static class req extends base
   {
      /**
       * Create a link data request with the given telegram.
       */
      public req(Telegram telegram)
      {
         super(EmiFrameType.L_DATA_REQ, telegram);
      }

      /**
       * Create an empty link data request message.
       */
      public req()
      {
         super(EmiFrameType.L_DATA_REQ);
      }
   }

   /**
    * Link data request confirmation.
    */
   public static class con extends base
   {
      /**
       * Create an empty link data confirmation message.
       */
      public con()
      {
         super(EmiFrameType.L_DATA_CON);
      }
   }
   
   /**
    * Link data indicator. This message contains a telegram that
    * was received from the EIB bus by the bus-interface.
    */
   public static class ind extends base
   {
      /**
       * Create an empty link data confirmation message.
       */
      public ind()
      {
         super(EmiFrameType.L_DATA_IND);
      }
   }
}
