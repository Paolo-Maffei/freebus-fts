package org.freebus.knxcomm.emi;

/**
 * Physical external interface (PEI) identify request.
 */
public final class PEI_Identify_req extends EmiMessageBase
{
   /**
    * Create a PEI-identify request.
    */
   public PEI_Identify_req()
   {
      super(EmiFrameType.PEI_IDENTIFY_REQ);
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
      rawData[start] = type.code;
      return 1;
   }
}