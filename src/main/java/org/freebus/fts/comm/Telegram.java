package org.freebus.fts.comm;

/**
 * A communication data packet as it is transferred on the EIB bus.
 */
public class Telegram
{
   /**
    * The type of the telegram.
    */
   public final TelegramType type;

   /**
    * The numeric type of the telegram.
    */
   public final int typeId;

   /**
    * The control byte:
    * bit 5: 0 if the telegram is repeated
    */
   public int ctrl = 0;

   /**
    * The DRL byte
    * bit 7: 0 if the receiver is a physical address, 1 if it is a group address.
    */
   public int drl = 0;

   /**
    * The two-bytes physical address of the sender.
    */
   public int from = 0;

   /**
    * The two-bytes address of the receiver. This may be a physical- or
    * a group address, depending on the {@link #recvIsGroup} flag.
    */
   public int recv = 0;

   /**
    * Is the receiver a group-address (true) or a physical-address (false)
    */
   public boolean recvIsGroup = false;

   /**
    * The data bytes of the telegram.
    * Every byte is in the range 0..255.
    */
   public int[] data = null;

   /**
    * Create a telegram object.
    */
   public Telegram(TelegramType type, int typeId)
   {
      this.type = type;
      this.typeId = typeId;
   }

   /**
    * @return the physical address of the sender, as a 3-number array.
    */
   public int[] getFrom()
   {
      return new int[]{ from>>12, (from>>8)&15, from&255 };
   }

   /**
    * @return the physical address of the sender as string.
    */
   public String getFromStr()
   {
      return Integer.toString(from>>12)+'.'+Integer.toString((from>>8)&15)+'.'+Integer.toString(from&255);
   }

   /**
    * @return the address of the receiver, as a 3-number array.
    * This can either be a group-address or a physical-address, depending
    * on the {@link #recvIsGroup} flag.
    */
   public int[] getRecv()
   {
      if (recvIsGroup) return new int[]{ recv>>11, (recv>>7)&15, recv&127 };
      return new int[]{ recv>>12, (recv>>8)&15, recv&255 };
   }

   /**
    * @return the address of the receiver as string.
    * This can either be a group-address or a physical-address, depending
    * on the {@link #recvIsGroup} flag.
    */
   public String getRecvStr()
   {
      if (recvIsGroup) return Integer.toString(recv>>11)+'/'+Integer.toString((recv>>7)&15)+'/'+Integer.toString(recv&127);
      return Integer.toString(recv>>12)+'.'+Integer.toString((recv>>8)&15)+'.'+Integer.toString(recv&255);
   }

   /**
    * @return the telegram in a human readable representation
    */
   @Override
   public String toString()
   {
      final StringBuffer str = new StringBuffer();

      str.append("from ");
      str.append(getFromStr());
      str.append("  to ");
      if (recvIsGroup) str.append("group ");
      str.append(getRecvStr());
      str.append("  [ctrl b");
      str.append(Integer.toBinaryString(ctrl));
      str.append("  drl b");
      str.append(Integer.toBinaryString(drl));
      str.append("]  data");

      for (int i=0; i<data.length; ++i)
         str.append(" #"+Integer.toHexString(data[i]));

      return str.toString();
   }
}
