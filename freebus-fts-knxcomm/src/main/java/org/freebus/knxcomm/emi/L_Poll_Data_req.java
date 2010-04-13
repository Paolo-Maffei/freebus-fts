package org.freebus.knxcomm.emi;

/**
 * L_Poll_Data.req - data polling request
 */
public final class L_Poll_Data_req extends EmiMessageBase
{
   protected int dest = 0;
   protected int numSlots = 15;

   /**
    * Create an empty poll-data-request object.
    */
   public L_Poll_Data_req()
   {
      super(EmiFrameType.L_POLL_DATA_REQ);
   }

   /**
    * Create a poll-data-request object.
    *
    * @param dest the destination group address.
    * @param numSlots the number of polling slots (1..15)
    */
   public L_Poll_Data_req(int dest, int numSlots)
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

      rawData[pos++] = type.code;
      rawData[pos++] = 0xf0; // control field
      rawData[pos++] = 0;
      rawData[pos++] = 0;
      rawData[pos++] = dest >> 8;
      rawData[pos++] = dest & 0xff;
      rawData[pos++] = numSlots & 0x0f;

      return pos - start;
   }
}