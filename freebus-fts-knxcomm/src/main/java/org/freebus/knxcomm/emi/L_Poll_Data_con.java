package org.freebus.knxcomm.emi;

/**
 * L_Poll_Data.L_Poll_Data_con - data polling confirmation
 * 
 * TODO not implemented
 */
public final class L_Poll_Data_con extends EmiMessageBase
{
   protected int dest = 0;
   protected int status = 0;

   /**
    * Create an empty object.
    */
   public L_Poll_Data_con()
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

      rawData[pos++] = type.code;
      rawData[pos++] = 0; // control field
      rawData[pos++] = 0;
      rawData[pos++] = 0;
      rawData[pos++] = 0;
      rawData[pos++] = status;

      return pos - start;
   }
}