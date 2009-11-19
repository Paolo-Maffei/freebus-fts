package org.freebus.fts.comm.emi;

/**
 * Data polling
 */
public final class L_Poll_Data
{
   /**
    * L_Poll_Data.req - data polling request
    */
   public final static class req extends EmiMessageBase
   {
      protected int dest = 0;
      protected int numSlots = 15;

      /**
       * Create an empty poll-data-request object.
       */
      public req()
      {
         super(EmiFrameType.L_POLL_DATA_REQ);
      }

      /**
       * Create a poll-data-request object.
       *
       * @param dest the destination group address.
       * @param numSlots the number of polling slots (1..15)
       */
      public req(int dest, int numSlots)
      {
         super(EmiFrameType.L_POLL_DATA_REQ);
         this.dest = dest;
      }

      /**
       * Set the destination group address.
       */
      public void setDest(int dest)
      {
         this.dest = dest;
      }

      /**
       * @return the destination group address.
       */
      public int getDest()
      {
         return dest;
      }

      /**
       * Initialize the message from the given raw data, beginning at start.
       */
      @Override
      public void fromRawData(int[] rawData, int start)
      {
         dest = (rawData[start + 4] << 8) | rawData[start + 5];
         numSlots = rawData[start + 6] & 0x0f;
      }

      /**
       * Fill the raw data of the message into the array rawData.
       */
      @Override
      public int toRawData(int[] rawData, int start)
      {
         int pos = start;

         rawData[pos++] = type.id;
         rawData[pos++] = 0xf0; // control field
         rawData[pos++] = 0;
         rawData[pos++] = 0;
         rawData[pos++] = dest >> 8;
         rawData[pos++] = dest & 0xff;
         rawData[pos++] = numSlots & 0x0f;

         return pos - start;
      }
   }

   /**
    * L_Poll_Data.con - data polling confirmation
    * 
    * TODO not implemented
    */
   public final static class con extends EmiMessageBase
   {
      protected int dest = 0;
      protected int status = 0;

      /**
       * Create an empty object.
       */
      public con()
      {
         super(EmiFrameType.L_POLL_DATA_CON);
      }

      /**
       * Set the confirmation status: 0=ok
       */
      public void setStatus(int status)
      {
         this.status = status;
      }

      /**
       * @return the confirmation status: 0=ok
       */
      public int getStatus()
      {
         return status;
      }

      @Override
      public void fromRawData(int[] rawData, int start)
      {
         status = rawData[start + 6];
      }

      @Override
      public int toRawData(int[] rawData, int start)
      {
         int pos = start;

         rawData[pos++] = type.id;
         rawData[pos++] = 0; // control field
         rawData[pos++] = 0;
         rawData[pos++] = 0;
         rawData[pos++] = 0;
         rawData[pos++] = status;

         return pos - start;
      }
   }
}
