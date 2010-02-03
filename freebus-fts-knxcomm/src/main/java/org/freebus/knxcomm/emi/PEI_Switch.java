package org.freebus.knxcomm.emi;

/**
 * PEI switch message types.
 */
public final class PEI_Switch
{
   /**
    * Layer types
    */
   public enum Layer
   {
      NONE(0), // Unused / discard
      LL(1), // Link Layer
      NL(2), // NL - Network Layer
      TLG(3), // TLG - Transport Layer Group Oriented
      TLC(4), // TLC - Transport Layer Connection Oriented
      TLL(5), // TLL - Transport Layer Local
      AL(6), // AL - Application Layer
      // ALG(6), // ALG - Group oriented part of the application layer
      MAN(7), // MAN - Management part of the application layer
      PEI(8), // PEI - Physical External Interface
      USR(9), // USR - Application running in the BAU. If the User is not
      // running, the messages are directed to the PEI.
      _RESERVED(10); // Reserved, do not use.

      public final int id;

      private Layer(int id)
      {
         this.id = id;
      }

      static public Layer valueOf(int id)
      {
         for (Layer e : values())
            if (e.id == id) return e;

         throw new IllegalArgumentException("None layer 0x" + Integer.toHexString(id));
      }
   }

   /**
    * Modes that can be switched to.
    */
   public enum Mode
   {
      /**
       * Normal mode.
       */
      NORMAL(0x00, new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 }),
      
      /**
       * Application layer.
       */
      APP_LAYER(0x00, new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 8 }),
      
      /**
       * Remote transport layer
       */
     TRANS_REMOTE(0x00, new int[]{ 1, 2, 3, 4, 4, 8, 8, 8, 0 }),
      
      /**
       * Local transport layer.
       */
      TRANS_LOCAL(0x00, new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 0 }),
      
      /**
       * Data link layer.
       */
      LINK(0x00, new int[]{ 1, 8, 3, 4, 5, 6, 7, 8, 0 }),
      
      /**
       * Data link layer "Bus monitor" mode.
       */
      BUSMON(0x90, new int[]{ 1, 8, 3, 4, 5, 6, 7, 8, 0 }),
      
      /**
       * Initialization that eibd sends.
       */
      INIT(0x1e, new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 0 });

      public final int systemStatus;
      public final int[] targets;

      private Mode(int systemStatus, int[] targets)
      {
         this.systemStatus = systemStatus;
         this.targets = targets;
      }
   }

   /**
    * PEI_Switch.req message. Used to control which message types are sent
    * where.
    */
   public static class req extends EmiMessageBase
   {
      private int systemStatus = 0;
      private final Layer targets[] = new Layer[10];

      /**
       * Create a new message with default values.
       */
      public req()
      {
         super(EmiFrameType.PEI_SWITCH_REQ);

         targets[0] = Layer.LL;
         targets[1] = Layer.NL;
         targets[2] = Layer.TLG;
         targets[3] = Layer.TLC;
         targets[4] = Layer.TLL;
         targets[5] = Layer.AL;
         targets[6] = Layer.MAN;
         targets[7] = Layer.PEI;
         targets[8] = Layer.USR;
         targets[9] = Layer._RESERVED;
      }

      /**
       * Create a new switch message from a predefined mode.
       */
      public req(Mode mode)
      {
         super(EmiFrameType.PEI_SWITCH_REQ);

         systemStatus = mode.systemStatus;

         for (int i = 0; i < 9; ++i)
            targets[i] = Layer.valueOf(mode.targets[i]);
         targets[9] = Layer._RESERVED;
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
      public void setTarget(Layer layer, Layer target)
      {
         targets[layer.id - 1] = target;
      }

      /**
       * @return the target of the given layer.
       */
      public Layer getTarget(Layer layer)
      {
         return targets[layer.id - 1];
      }

      /**
       * Set all layer targets in one call.
       */
      public void setTargets(Layer ll, Layer nl, Layer tlg, Layer tlc, Layer tll, Layer al, Layer man, Layer pei,
            Layer usr)
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
         targets[9] = Layer._RESERVED;
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
            targets[i] = Layer.valueOf(rawData[++start] >> 4);
            targets[i + 1] = Layer.valueOf(rawData[start] & 0x0f);
         }
         targets[9] = Layer._RESERVED;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int toRawData(int[] rawData, int start)
      {
         int pos = start;
         targets[9] = Layer._RESERVED;

         rawData[pos++] = this.getType().id & 0xff;
         rawData[pos++] = systemStatus & 0xff;
         for (int i = 0; i < 9; i += 2)
            rawData[pos++] = (targets[i].id << 4) | targets[i + 1].id;

         return pos - start;
      }
   }
}
