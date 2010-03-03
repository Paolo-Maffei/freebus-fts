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
   public int fromRawData(int[] rawData, int start) throws InvalidDataException
   {
      int pos = start;

      int len = rawData[pos++] - 2;
      protocolType = ProtocolType.valueOf(rawData[pos++]);

      if (len > 0)
         data = Arrays.copyOfRange(rawData, pos, pos + len);
      else data = null;

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      final int dataLen = data == null ? 0 : data.length;
      rawData[start++] = dataLen + 2;
      rawData[start++] = protocolType.code;

      for (int i = 0; i < dataLen; ++i)
         rawData[start++] = data[i];

      return dataLen + 2;
   }
}
