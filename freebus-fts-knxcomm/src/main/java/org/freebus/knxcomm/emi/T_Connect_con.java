package org.freebus.knxcomm.emi;

/**
 * T_Connect.con - transport layer connect confirmation
 */
public final class T_Connect_con extends EmiMessageBase
{
   protected int status = 0;

   /**
    * Create an empty object.
    */
   public T_Connect_con()
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

      rawData[pos++] = type.code;
      rawData[pos++] = 0; // control field
      rawData[pos++] = 0;
      rawData[pos++] = 0;
      rawData[pos++] = 0;
      rawData[pos++] = status;

      return pos - start;
   }
}