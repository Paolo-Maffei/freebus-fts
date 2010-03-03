package org.freebus.knxcomm.netip.types;

import org.freebus.knxcomm.netip.blocks.HostProtAddrInfo;

/**
 * Protocol types of {@link HostProtAddrInfo}.
 */
public enum ProtocolType
{
   /**
    * UDP over IPv4
    */
   IPv4_UDP(0x01),

   /**
    * TCP over IPv4.
    */
   IPv4_TCP(0x02);


   /**
    * The service type code as used in the data frames.
    */
   public final int code;

   /**
    * @return the service type for the given value.
    * @throws IllegalArgumentException if no matching service type is found.
    */
   static public ProtocolType valueOf(int code)
   {
      for (ProtocolType v : values())
      {
         if (v.code == code)
            return v;
      }

      throw new IllegalArgumentException("Invalid KNXnet/IP host protocol type: " + code);
   }

   /*
    * Internal constructor.
    */
   private ProtocolType(int code)
   {
      this.code = code;
   }
}
