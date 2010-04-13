package org.freebus.knxcomm.emi;

/**
 * PEI_Switch.req message. Used to control which message types are sent
 * where.
 */
public class PEI_Switch_req extends EmiMessageBase
{
   private int systemStatus = 0;
   private final LayerType targets[] = new LayerType[10];

   /**
    * Create a new message with default values.
    */
   public PEI_Switch_req()
   {
      super(EmiFrameType.PEI_SWITCH_REQ);

      targets[0] = LayerType.LL;
      targets[1] = LayerType.NL;
      targets[2] = LayerType.TLG;
      targets[3] = LayerType.TLC;
      targets[4] = LayerType.TLL;
      targets[5] = LayerType.AL;
      targets[6] = LayerType.MAN;
      targets[7] = LayerType.PEI;
      targets[8] = LayerType.USR;
      targets[9] = LayerType._RESERVED;
   }

   /**
    * Create a new switch message from a predefined mode.
    */
   public PEI_Switch_req(PEISwitchMode mode)
   {
      super(EmiFrameType.PEI_SWITCH_REQ);

      systemStatus = mode.systemStatus;

      for (int i = 0; i < 9; ++i)
         targets[i] = LayerType.valueOf(mode.targets[i]);
      targets[9] = LayerType._RESERVED;
   }

   /**
    * Set the system-status. Default: 0x00 Bus-monitor mode: 0x90 Eibd also
    * sends 0x1e on initialization - who knows?
    */
   public void setSystemStatus(int systemStatus)
   {
      this.systemStatus = systemStatus;
   }

   /**
    * @return the system-status.
    */
   public int getSystemStatus()
   {
      return systemStatus;
   }

   /**
    * Set the target for a layer.
    */
   public void setTarget(LayerType layer, LayerType target)
   {
      targets[layer.id - 1] = target;
   }

   /**
    * @return the target of the given layer.
    */
   public LayerType getTarget(LayerType layer)
   {
      return targets[layer.id - 1];
   }

   /**
    * Set all layer targets in one call.
    */
   public void setTargets(LayerType ll, LayerType nl, LayerType tlg, LayerType tlc, LayerType tll, LayerType al, LayerType man, LayerType pei,
         LayerType usr)
   {
      targets[0] = ll;
      targets[1] = nl;
      targets[2] = tlg;
      targets[3] = tlc;
      targets[4] = tll;
      targets[5] = al;
      targets[6] = man;
      targets[7] = pei;
      targets[8] = usr;
      targets[9] = LayerType._RESERVED;
   }

   /**
    * Initialize the message from the given raw data, beginning at start. The
    * first byte is expected to be the message type.
    */
   public void fromRawData(int[] rawData, int start)
   {
      systemStatus = rawData[++start];

      for (int i = 0; i < 9; i += 2)
      {
         targets[i] = LayerType.valueOf(rawData[++start] >> 4);
         targets[i + 1] = LayerType.valueOf(rawData[start] & 0x0f);
      }
      targets[9] = LayerType._RESERVED;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      int pos = start;
      targets[9] = LayerType._RESERVED;

      rawData[pos++] = this.getType().code & 0xff;
      rawData[pos++] = systemStatus & 0xff;
      for (int i = 0; i < 9; i += 2)
         rawData[pos++] = (targets[i].id << 4) | targets[i + 1].id;

      return pos - start;
   }
}