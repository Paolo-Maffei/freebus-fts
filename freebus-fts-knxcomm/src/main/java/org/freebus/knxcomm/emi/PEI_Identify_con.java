package org.freebus.knxcomm.emi;

/**
 * Physical external interface (PEI) identify confirmation.
 */
public final class PEI_Identify_con extends EmiMessageBase
{
   protected int addr = 0;
   protected int serialHigh = 0, serialMid = 0, serialLow = 0;

   /**
    * Create an empty object.
    */
   public PEI_Identify_con()
   {
      super(EmiFrameType.PEI_IDENTIFY_CON);
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

      rawData[pos++] = type.code;
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