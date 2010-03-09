package org.freebus.knxcomm.netip.blocks;

import java.net.InetAddress;
import java.util.Arrays;

import org.freebus.knxcomm.netip.types.ProtocolType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Host protocol address information (HPAI). This is a part of a KNXnet/IP frame
 * body.
 */
public final class HostProtAddrInfo implements Block
{
   private ProtocolType protocolType;
   private int[] data;

   /**
    * Create an empty host protocol address information object.
    */
   public HostProtAddrInfo()
   {
   }

   /**
    * Create a host protocol address information object.
    * 
    * @see {@link #setData(int[])
    */
   public HostProtAddrInfo(ProtocolType protocolType, int[] data)
   {
      this.protocolType = protocolType;
      this.data = data;
   }

   /**
    * Create a host protocol address information object with
    * the IP address and port from <code>addr</code> as data.
    * 
    * @param protocol - the protocol type.
    * @param addr - the address of the sender.
    * @param port - the port of the sender
    */
   public HostProtAddrInfo(ProtocolType protocolType, InetAddress addr, int port)
   {
      this.protocolType = protocolType;
      final byte[] addrBytes = addr.getAddress();

      data = new int[addrBytes.length + 2];

      for (int i = 0; i < addrBytes.length; ++i)
         data[i] = addrBytes[i];

      data[addrBytes.length] = port >> 8;
      data[addrBytes.length + 1] = port & 0xff;
   }

   /**
    * Set the protocol type.
    */
   public void setProtocolType(ProtocolType protocolType)
   {
      this.protocolType = protocolType;
   }

   /**
    * @return the protocol type
    */
   public ProtocolType getProtocolType()
   {
      return protocolType;
   }

   /**
    * Set the protocol specific data. E.g. 4 byte IP address and 2 byte port for
    * IPv4.
    */
   public void setData(int[] data)
   {
      this.data = data;
   }

   /**
    * @return the protocol specific data. E.g. 4 byte IP address and 2 byte port
    *         for IPv4.
    */
   public int[] getData()
   {
      return data;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int fromData(int[] data, int start) throws InvalidDataException
   {
      int pos = start;

      int len = data[pos++] - 2;
      protocolType = ProtocolType.valueOf(data[pos++]);

      if (len > 0)
         this.data = Arrays.copyOfRange(data, pos, pos + len);
      else this.data = null;

      return pos - start + len;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toData(int[] data, int start)
   {
      final int dataLen = this.data == null ? 0 : this.data.length;
      data[start++] = dataLen + 2;
      data[start++] = protocolType.code;

      for (int i = 0; i < dataLen; ++i)
         data[start++] = this.data[i];

      return dataLen + 2;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      int code = protocolType == null ? 0 : protocolType.code;
      if (data != null)
      {
         for (int i = 0; i < data.length; ++i)
            code = (code << 7) | data[i];
      }
      return code;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(final Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof HostProtAddrInfo))
         return false;

      final HostProtAddrInfo oo = (HostProtAddrInfo) o;

      if (protocolType != oo.protocolType)
         return false;

      if (data == null && oo.data != null)
         return false;

      return data == oo.data || Arrays.equals(data, oo.data);
   }
}
