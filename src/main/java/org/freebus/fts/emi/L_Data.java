package org.freebus.fts.emi;

/**
 * Link-data message types.
 */
public final class L_Data
{
   /**
    * Base class for link-data messages.
    */
   public static abstract class base extends EmiMessageBase
   {
      // Control field
      protected int ctrl = 0;

      // 16 bit source address
      protected int from = 0;

      // 16 bit destination address
      protected int dest = 0;

      // Data control field
      protected int dataCtrl = 0;

      // Data
      protected int data[] = null;

      /**
       * Create an empty L_Data message.
       */
      protected base(EmiMessageType type)
      {
         super(type);
      }

      /**
       * Set the sender address.
       */
      public void setFrom(int from)
      {
         this.from = from;
      }

      /**
       * @return the sender address.
       */
      public int getFrom()
      {
         return from;
      }

      /**
       * @return the sender address as human readable string.
       */
      public String getFromString()
      {
         return addrToString(from, false);
      }

      /**
       * @return true if the destination address is a group address, false if it
       *         is a physical address.
       */
      public boolean isDestGroup()
      {
         return (dataCtrl & 0x80) != 0;
      }

      /**
       * Set the destination as a physical address.
       */
      public void setDest(int dest)
      {
         this.dest = dest;
         this.dataCtrl &= ~0x80;
      }

      /**
       * Set the destination as a group address.
       */
      public void setDestGroup(int dest)
      {
         this.dest = dest;
         this.dataCtrl |= 0x80;
      }

      /**
       * @return the destination address.
       */
      public int getDest()
      {
         return dest;
      }

      /**
       * @return the destination address as human readable string.
       */
      public String getDestString()
      {
         return addrToString(dest, isDestGroup());
      }

      /**
       * @return the message priority (0..3)
       */
      public int getPriority()
      {
         return (ctrl >> 2) & 0x3;
      }

      /**
       * Set the message priority (0..3)
       */
      public void setPriority(int priority)
      {
         assert (priority >= 0 && priority <= 3);
         ctrl = (ctrl & 0xf3) | (priority << 2);
      }

      /**
       * Set the network-layer (NPDU) data. Allowed length is 1..16 bytes.
       */
      public void setData(int[] data)
      {
         this.data = data;
         assert (data != null && data.length <= 16);
         dataCtrl = (dataCtrl & 0xf0) | (data.length - 1);
      }

      /**
       * @return the network-layer (NPDU) data.
       */
      public int[] getData()
      {
         return data;
      }

      /**
       * Initialize the message from the given raw data, beginning at start. The
       * first byte is expected to be the message type.
       */
      public void fromRawData(int[] rawData, int start)
      {
         ctrl = rawData[++start];
         from = (rawData[++start] << 8) | rawData[++start];
         dest = (rawData[++start] << 8) | rawData[++start];
         dataCtrl = rawData[++start];

         final int len = (dataCtrl & 0xf) + 1;
         data = new int[len];

         for (int i = 0; i < len; ++i)
            data[i] = rawData[++start];
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int toRawData(int[] rawData, int start)
      {
         int pos = start;

         rawData[pos++] = type.id;
         rawData[pos++] = ctrl;
         rawData[pos++] = (from >> 8) & 0xff;
         rawData[pos++] = from & 0xff;
         rawData[pos++] = (dest >> 8) & 0xff;
         rawData[pos++] = dest & 0xff;
         rawData[pos++] = dataCtrl;

         return start - pos;
      }

      /**
       * @return the message in a human readable form.
       */
      @Override
      public String toString()
      {
         return getTypeString() + " from " + getFromString() + " to " + getDestString();
      }
   }

   /**
    * Link data request. This message type is used to send telegrams to the EIB
    * bus.
    */
   public static class req extends base
   {
      /**
       * Create an empty link data request message.
       */
      public req()
      {
         super(EmiMessageType.L_DATA_REQ);
      }

      /**
       * Set the acknowledge-requested flag
       */
      public void setAcknowledge(boolean acknowledge)
      {
         ctrl = (ctrl & ~0x02) | (acknowledge ? 0x02 : 0);
      }

      /**
       * @return true if an acknowledge is requested.
       */
      public boolean getAcknowledge()
      {
         return (ctrl & 0x02) != 0;
      }
   }

   /**
    * Link data request confirmation.
    */
   public static class con extends base
   {
      /**
       * Create an empty link data confirmation message.
       */
      public con()
      {
         super(EmiMessageType.L_DATA_CON);
      }

      /**
       * Set the error-indication flag.
       */
      public void setError(boolean error)
      {
         ctrl = (ctrl & ~0x01) | (error ? 0x01 : 0);
      }

      /**
       * @return the error-indication flag.
       */
      public boolean getError()
      {
         return (ctrl & 0x01) != 0;
      }

      /**
       * Set the repeat flag.
       */
      public void setRepeat(boolean repeat)
      {
         ctrl = (ctrl & ~0x20) | (repeat ? 0x20 : 0);
      }

      /**
       * @return the repeat flag.
       */
      public boolean getRepeat()
      {
         return (ctrl & 0x20) != 0;
      }
   }
   
   /**
    * Link data indicator. This message contains a telegram that
    * was received from the EIB bus by the bus-interface.
    */
   public static class ind extends base
   {
      /**
       * Create an empty link data confirmation message.
       */
      public ind()
      {
         super(EmiMessageType.L_DATA_IND);
      }

      /**
       * @return the object's contents in human readable form.
       */
      @Override
      public String toString()
      {
         final StringBuilder str = new StringBuilder();

         if (data == null)
         {
            str.append(" (null)");
         }
         else
         {
            for (int i = 0; i < data.length; ++i)
               str.append(" " + Integer.toHexString(data[i]));
         }

         return super.toString() + "  DATA" + str.toString();
      }
   }
}
