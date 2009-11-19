package org.freebus.fts.comm.emi;

/**
 * Transport Layer EMI
 */
public final class T_Connect
{
   /**
    * T_Connect.req - transport layer connect request
    */
   public final static class req extends EmiMessageBase
   {
      protected int dest = 0;

      /**
       * Create an empty connect-request object.
       */
      public req()
      {
         super(EmiFrameType.T_CONNECT_REQ);
      }

      /**
       * Create a connect-request object.
       */
      public req(int dest)
      {
         super(EmiFrameType.T_CONNECT_REQ);
         this.dest = dest;
      }

      /**
       * Set the destination address.
       */
      public void setDest(int dest)
      {
         this.dest = dest;
      }

      /**
       * @return he destination address.
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
      }

      /**
       * Fill the raw data of the message into the array rawData.
       */
      @Override
      public int toRawData(int[] rawData, int start)
      {
         int pos = start;

         rawData[pos++] = type.id;
         rawData[pos++] = 0; // control field
         rawData[pos++] = 0;
         rawData[pos++] = 0;
         rawData[pos++] = dest >> 8;
         rawData[pos++] = dest & 0xff;

         return pos - start;
      }
   }

   /**
    * T_Connect.con - transport layer connect confirmation
    */
   public final static class con extends EmiMessageBase
   {
      protected int status = 0;

      /**
       * Create an empty object.
       */
      public con()
      {
         super(EmiFrameType.T_CONNECT_CON);
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
