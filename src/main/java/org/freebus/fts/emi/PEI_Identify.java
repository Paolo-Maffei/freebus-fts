package org.freebus.fts.emi;

/**
 * PEI_Identify messages
 */
public final class PEI_Identify
{
   /**
    * PEI_Identify.req - Physical external interface (PEI) identify request
    */
   public final static class req extends EmiMessageBase
   {
      /**
       * Create a PEI-identify request.
       */
      public req()
      {
         super(EmiMessageType.PEI_IDENTIFY_REQ);
      }

      /**
       * Initialize the message from the given raw data, beginning at start.
       */
      @Override
      public void fromRawData(int[] rawData, int start)
      {
      }

      /**
       * Fill the raw data of the message into the array rawData.
       */
      @Override
      public int toRawData(int[] rawData, int start)
      {
         rawData[start] = type.id;
         return 1;
      }
   }

   /**
    * PEI_Identify.con - Physical external interface (PEI) confirmation
    * 
    * TODO not implemented
    */
   public final static class con extends EmiMessageBase
   {
      protected int addr = 0;
      protected int serialHigh = 0, serialMid = 0, serialLow = 0;

      /**
       * Create an empty object.
       */
      public con()
      {
         super(EmiMessageType.PEI_IDENTIFY_CON);
      }

      /**
       * Set the physical address.
       */
      public void setAddr(int addr)
      {
         this.addr = addr;
      }

      /**
       * @return the physical address
       */
      public int getAddr()
      {
         return addr;
      }

      /**
       * Set the serial number. High, mid and low are all 16-bit numbers.
       */
      public void setSerial(int high, int mid, int low)
      {
         serialHigh = high;
         serialMid = mid;
         serialLow = low;
      }

      /**
       * @return the high number of the serial.
       */
      public int getSerialHigh()
      {
         return serialHigh;
      }

      /**
       * @return the mid number of the serial.
       */
      public int getSerialMid()
      {
         return serialMid;
      }

      /**
       * @return the low number of the serial.
       */
      public int getSerialLow()
      {
         return serialLow;
      }

      @Override
      public void fromRawData(int[] rawData, int start)
      {
         ++start;
         addr = (rawData[start++] << 8) | rawData[start++];
         serialHigh = (rawData[start++] << 8) | rawData[start++];
         serialMid = (rawData[start++] << 8) | rawData[start++];
         serialLow = (rawData[start++] << 8) | rawData[start++];
      }

      @Override
      public int toRawData(int[] rawData, int start)
      {
         int pos = start;

         rawData[pos++] = type.id;
         rawData[pos++] = addr >> 8;
         rawData[pos++] = addr & 0xff;
         rawData[pos++] = serialHigh >> 8;
         rawData[pos++] = serialHigh & 0xff;
         rawData[pos++] = serialMid >> 8;
         rawData[pos++] = serialMid & 0xff;
         rawData[pos++] = serialLow >> 8;
         rawData[pos++] = serialLow & 0xff;

         return pos - start;
      }

      /**
       * @return the object in human readable form.
       */
      @Override
      public String toString()
      {
         return getTypeString() + ' ' + addrToString(addr, false) + String.format(" version %d.%d.%d", serialHigh, serialMid, serialLow);
      }
   }
}
