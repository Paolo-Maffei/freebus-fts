package org.freebus.fts.eib;


/**
 * A communication data packet as it is used on the EIB bus.
 */
public class Telegram
{
   private PhysicalAddress from = null;
   private Address dest = null;
   private Type type = Type.Multicast;
   private Priority priority = Priority.NORMAL;
   private boolean repeated = false;
   private int[] data = null;

   /**
    * Create an empty telegram object.
    */
   public Telegram()
   {
   }

   /**
    * @return the sender address.
    */
   public PhysicalAddress getFrom()
   {
      return from;
   }

   /**
    * Set the sender address.
    */
   public void setFrom(PhysicalAddress from)
   {
      this.from = from;
   }

   /**
    * Returns the destination address. This can either be a
    * {@link PhysicalAddress} physical address, or a {@link GroupAddress} group
    * address.
    * 
    * @return the destination address.
    */
   public Address getDest()
   {
      return dest;
   }

   /**
    * Set the destination address. This can either be a {@link PhysicalAddress}
    * physical address, or a {@link GroupAddress} group address.
    */
   public void setDest(Address dest)
   {
      this.dest = dest;
   }

   /**
    * Set the telegram type.
    */
   public void setType(Type type)
   {
      this.type = type;
   }

   /**
    * @return the telegram type.
    */
   public Type getType()
   {
      return type;
   }

   /**
    * @return the priority.
    */
   public Priority getPriority()
   {
      return priority;
   }

   /**
    * Set the priority. The default priority is {@link Priority#NORMAL}.
    */
   public void setPriority(Priority priority)
   {
      this.priority = priority;
   }

   /**
    * @return the repeated flag.
    */
   public boolean isRepeated()
   {
      return repeated;
   }

   /**
    * Set the repeated flag.
    */
   public void setRepeated(boolean repeated)
   {
      this.repeated = repeated;
   }

   /**
    * Set the data.
    */
   public void setData(int[] data)
   {
      this.data = data;
   }

   /**
    * @return the data
    */
   public int[] getData()
   {
      return data;
   }

   // /*
   // * The control byte.
   // * Explanation taken from KNX standard, 3.2.2, section 2.1.1
   // *
   // * bit 7: frame length indicator: 0=long frame, 1=short frame. Always 1.
   // * bit 6: frame type: 0=data telegram, 1=poll-data telegram
   // * bit 5: repeated flag: 0=if the telegram is repeated, 1=not repeated
   // * bit 4: 1=for data telegram and poll-data telegram, 0=for acknowledge
   // telegram
   // * bit 3+2: Priority: 0=system, 1=alarm, 2=high priority, 3=normal priority
   // * bit 1: 0
   // * bit 0: 0
   // */
   // private int ctrl = 0;
   //
   // /*
   // * The DRL byte (DRL means: destination, routing, length)
   // * bit 7: destination address flag: 0=physical address, 1=group address.
   // * bit 6..4: routing counter: 0=never route, 7=route always. Default: 6
   // * bit 3..0: length of the data part minus one
   // */
   // private int drl = 0;
   //
   // /**
   // * Create a telegram from the received byte-stream.
   // */
   // public Telegram(int[] rawData)
   // {
   // int idx = -1;
   // setMessageCode(MessageCode.valueOf(rawData[++idx]));
   //
   // ctrl = rawData[++idx];
   //
   // priority = Priority.valueOf((ctrl>>2) & 0x03);
   // repeated = (ctrl & 0x10) == 0;
   //
   // from = (rawData[++idx]<<8) | rawData[++idx];
   // dest = (rawData[++idx]<<8) | rawData[++idx];
   //
   // drl = rawData[++idx];
   // isDestGroup = (drl & (1<<7)) != 0;
   // final int dataLen = (drl & 15) + 1;
   //
   // transportType = TransportType.valueOf(rawData[++idx]);
   //
   // this.data = new int[dataLen];
   // for (int i=0; i<dataLen; ++i)
   // this.data[i] = rawData[idx+i];
   //
   // apci = ((rawData[idx] & 0x03)<<8) | (rawData[++idx] & 0xc0);
   // }
   //   
   // /**
   // * Create an empty telegram object.
   // */
   // public Telegram()
   // {
   // }
   //
   // /**
   // * Set the message code.
   // */
   // public void setMessageCode(MessageCode messageCode)
   // {
   // this.messageCode = messageCode;
   // }
   //
   // /**
   // * @return the message code.
   // */
   // public MessageCode getMessageCode()
   // {
   // return messageCode;
   // }
   //
   // /**
   // * Set the physical address of the sender, as a two-byte integer.
   // */
   // public void setFrom(int from)
   // {
   // if (from<0 || from>65535) throw new
   // IllegalArgumentException("illegal physical address");
   // this.from = from & 0xffff;
   // }
   //
   // /**
   // * Set the physical address of the sender.
   // *
   // * @param zone is the zone address: 0..15
   // * @param zone is the line address: 0..15
   // * @param zone is the node address: 0..255
   // */
   // public void setFrom(int zone, int line, int node)
   // {
   // if (zone<0 || zone>15 || line<0 || line>15 || node<0 || node>255)
   // throw new IllegalArgumentException("illegal physical address");
   //
   // this.from = (zone<<12) | (line<<8) | node;
   // }
   //
   // /**
   // * @return the physical address of the sender, as a two-byte integer.
   // */
   // public int getFrom()
   // {
   // return from;
   // }
   //
   // /**
   // * @return the physical address of the sender, as a 3-number array.
   // */
   // public int[] getFromAddr()
   // {
   // return new int[]{ from>>12, (from>>8)&15, from&255 };
   // }
   //
   // /**
   // * @return the physical address of the sender as string.
   // */
   // public String getFromStr()
   // {
   // return
   // Integer.toString(from>>12)+'.'+Integer.toString((from>>8)&15)+'.'+Integer.toString(from&255);
   // }
   //
   // /**
   // * Set the physical address of the receiver, as a two-byte integer.
   // * Sets the group-indicator flag to false.
   // */
   // public void setDest(int dest)
   // {
   // if (dest<0 || dest>65535) throw new
   // IllegalArgumentException("illegal physical address");
   // this.dest = dest & 0xffff;
   // this.isDestGroup = false;
   // }
   //
   // /**
   // * Set the physical address of the receiver.
   // * Sets the group-indicator flag to false.
   // *
   // * @param zone is the zone address: 0..15
   // * @param zone is the line address: 0..15
   // * @param zone is the node address: 0..255
   // */
   // public void setDest(int zone, int line, int node)
   // {
   // if (zone<0 || zone>15 || line<0 || line>15 || node<0 || node>255)
   // throw new IllegalArgumentException("illegal physical address");
   //
   // this.dest = (zone<<12) | (line<<8) | node;
   // this.isDestGroup = false;
   // }
   //
   // /**
   // * Set the group address of the receiver, as a two-byte integer.
   // * Sets the group-indicator flag to true.
   // */
   // public void setGroupDest(int dest)
   // {
   // if (dest<0 || dest>32767) throw new
   // IllegalArgumentException("illegal group address");
   // this.dest = dest & 0xffff;
   // this.isDestGroup = true;
   // }
   //
   // /**
   // * Set the group address of the receiver.
   // * Sets the group-indicator flag to true.
   // *
   // * @param main is the main-group address: 0..15
   // * @param zone is the sub-group address: 0..15
   // * @param zone is the group address: 0..127
   // */
   // public void setGroupDest(int main, int sub, int group)
   // {
   // if (main<0 || main>15 || sub<0 || sub>15 || group<0 || group>127)
   // throw new IllegalArgumentException("illegal group address");
   //
   // this.dest = (main<<11) | (sub<<7) | group;
   // this.isDestGroup = true;
   // }
   //
   // /**
   // * Mark the destination to be a broadcast.
   // * A broadcast is a telegram with destination group-address 0/0/0.
   // */
   // public void setBroadcastDest()
   // {
   // this.dest = 0;
   // this.isDestGroup = true;
   // }
   //
   // /**
   // * @return the address of the receiver, as a 3-number array.
   // * This can either be a group-address or a physical-address, depending
   // * on the {@link #isDestGroup} flag.
   // */
   // public int getDest()
   // {
   // return dest;
   // }
   //
   // /**
   // * @return the address of the receiver, as a 3-number array.
   // * This can either be a group-address or a physical-address, depending
   // * on the {@link #isDestGroup} flag.
   // */
   // public int[] getDestAddr()
   // {
   // if (isDestGroup) return new int[]{ dest>>11, (dest>>7)&15, dest&127 };
   // return new int[]{ dest>>12, (dest>>8)&15, dest&255 };
   // }
   //
   // /**
   // * @return the address of the receiver as string.
   // * This can either be a group-address or a physical-address, depending
   // * on the {@link #isDestGroup} flag.
   // */
   // public String getDestStr()
   // {
   // if (isDestGroup) return
   // Integer.toString(dest>>11)+'/'+Integer.toString((dest>>7)&15)+'/'+Integer.toString(dest&127);
   // return
   // Integer.toString(dest>>12)+'.'+Integer.toString((dest>>8)&15)+'.'+Integer.toString(dest&255);
   // }
   //
   // /**
   // * @return true if the destination address is a group-address.
   // */
   // public boolean isDestGroup()
   // {
   // return isDestGroup;
   // }
   //
   // /**
   // * @return the telegram in a human readable representation
   // */
   // @Override
   // public String toString()
   // {
   // final StringBuffer str = new StringBuffer();
   //
   // str.append(transportType.toString());
   // str.append(" from ");
   // str.append(getFromStr());
   // str.append("  to ");
   // if (isDestGroup) str.append("group ");
   // str.append(getDestStr());
   // str.append("  [ctrl b");
   // str.append(Integer.toBinaryString(ctrl));
   // str.append("  drl b");
   // str.append(Integer.toBinaryString(drl));
   // str.append("]  data");
   //
   // for (int i=0; i<data.length; ++i)
   // str.append(" #"+Integer.toHexString(data[i]));
   //
   // return str.toString();
   // }
   //
   // /**
   // * Set the priority of the telegram.
   // */
   // public void setPriority(Priority priority)
   // {
   // this.priority = priority;
   // }
   //
   // /**
   // * @return the priority of the telegram.
   // */
   // public Priority getPriority()
   // {
   // return priority;
   // }
   //
   // /**
   // * Set the repeated flag.
   // */
   // public void setRepeated(boolean repeated)
   // {
   // this.repeated = repeated;
   // }
   //
   // /**
   // * @return the repeated flag.
   // */
   // public boolean isRepeated()
   // {
   // return repeated;
   // }
   //
   // /**
   // * Set the poll-data indicator. Set to true for a poll-data request.
   // */
   // public void setPoll(boolean poll)
   // {
   // this.poll = poll;
   // }
   //
   // /**
   // * @return the poll-data indicator. Is true for a poll-data request.
   // */
   // public boolean isPoll()
   // {
   // return poll;
   // }
   //
   // /**
   // * Set the transport type. Default: {@link TransportType#DATA}.
   // */
   // public void setTransportType(TransportType transportType)
   // {
   // this.transportType = transportType;
   // }
   //
   // /**
   // * @return the transport type.
   // */
   // public TransportType getTransportType()
   // {
   // return transportType;
   // }
   //
   // /**
   // * Set the application control id.
   // */
   // public void setApci(int apci)
   // {
   // this.apci = apci;
   // }
   //
   // /**
   // * @return the application control id.
   // */
   // public int getApci()
   // {
   // return apci;
   // }
   //
   // /**
   // * @return the data of the telegram.
   // */
   // public int[] getData()
   // {
   // return data;
   // }
   //
   // /**
   // * Set the data of the telegram. 1-16 bytes are allowed.
   // */
   // public void setData(int[] data)
   // {
   // if (data.length<1 || data.length>16) throw new
   // IllegalArgumentException("Invalid data length");
   // this.data = data;
   // }
   //
   // /**
   // * @return the raw byte-stream for the telegram.
   // */
   // public int[] toRawData()
   // {
   // int len = 6;
   // if (data!=null) len += data.length;
   // int[] rawData = new int[len];
   //
   // if (true) return rawData;
   //
   // // FIXME BROKEN!
   //      
   // /* The control byte.
   // * bit 7: frame length indicator: 0=long frame, 1=short frame. Always 1.
   // * bit 6: frame type: 0=data telegram, 1=poll-data telegram.
   // * bit 5: repeated flag: 0=if the telegram is repeated, 1=not repeated.
   // * bit 4: 1=for data telegram and poll-data telegram, 0=for acknowledge
   // telegram.
   // * bit 3+2: Priority: 0=system, 1=alarm, 2=high priority, 3=normal
   // priority.
   // * bit 1: acknowledge-request: 0=no acknowledge, 1=acknowledge requested.
   // * bit 0: always 0.
   // */
   // int ctrl = 0x40;
   // if (poll) ctrl |= 0x80;
   // if (repeated) ctrl |= 0x20;
   //
   // rawData[0] = ctrl & 0xff;
   //
   // /*
   // * The DRL byte (DRL means: destination, routing, length)
   // * bit 7: destination address flag: 0=physical address, 1=group address.
   // * bit 6..4: routing counter: 0=never route, 7=route always. Default: 6
   // * bit 3..0: length of the data part minus one
   // */
   //
   // priority = Priority.valueOf((ctrl>>2) & 0x03);
   // setRepeated((ctrl & 0x10) == 0);
   //
   // from = (rawData[1]<<8) | rawData[2];
   // dest = (rawData[3]<<8) | rawData[4];
   //
   // drl = rawData[5];
   // isDestGroup = (drl & (1<<7)) != 0;
   // final int dataLen = (drl & 15) + 1;
   //
   // transportType = TransportType.valueOf(rawData[6]);
   //
   // this.data = new int[dataLen];
   // for (int i=0; i<dataLen; ++i)
   // this.data[i] = rawData[i+6];
   //
   // if (dataLen>1) setApci(((rawData[6] & 0x03)<<8) | (rawData[7] & 0xc0));
   // else setApci((rawData[6] & 0x03)<<8);
   //
   //
   // return rawData;
   // }
}
