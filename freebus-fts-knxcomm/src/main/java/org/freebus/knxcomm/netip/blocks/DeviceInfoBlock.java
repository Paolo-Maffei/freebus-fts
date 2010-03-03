package org.freebus.knxcomm.netip.blocks;

import org.freebus.knxcomm.MediumType;
import org.freebus.knxcomm.netip.types.DeviceDescriptionType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Device information block. Contains description about the device.
 */
public final class DeviceInfoBlock implements Block
{
   private MediumType medium;
   private int status;
   private int busAddress;
   private int projectId;
   private final int[] serial = new int[6];
   private byte[] routingAddr = new byte[4];
   private final int[] macAddr = new int[6];
   private String name;

   /**
    * Create an empty device-info block.
    */
   public DeviceInfoBlock()
   {
      this(MediumType.TWISTED_PAIR);
   }

   /**
    * Create a device-info block.
    */
   public DeviceInfoBlock(MediumType medium)
   {
      this.medium = medium;
   }

   /**
    * @return the medium type.
    */
   public MediumType getMedium()
   {
      return medium;
   }

   /**
    * Set the medium type.
    */
   public void setMedium(MediumType medium)
   {
      this.medium = medium;
   }

   /**
    * Returns the device status. bits 7..1 are reserved, bit 0: programming
    * mode.
    * 
    * @return the device status.
    */
   public int getStatus()
   {
      return status;
   }

   /**
    * Set the device status. See {@link #getDeviceStatus()}.
    */
   public void setStatus(int status)
   {
      this.status = status;
   }

   /**
    * @return the 2-byte KNX/EIB bus address.
    */
   public int getBusAddress()
   {
      return busAddress;
   }

   /**
    * Set the 2-byte KNX/EIB bus address.
    */
   public void setBusAddress(int busAddress)
   {
      this.busAddress = busAddress;
   }

   /**
    * @return the 2-byte project installation identifier. Bit 0..3: installation
    *         number, bit 4..15: project number.
    */
   public int getProjectId()
   {
      return projectId;
   }

   /**
    * Set the 2-byte project installation identifier. Bit 0..3: installation
    * number, bit 4..15: project number.
    */
   public void setProjectId(int projectId)
   {
      this.projectId = projectId;
   }

   /**
    * @return the name of the device.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the device. Encoding of the string is ISO 8859-1, maximum
    * length is 30 characters.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the 6-byte KNX serial number of the device.
    */
   public int[] getSerial()
   {
      return serial;
   }

   /**
    * @return the 4-byte KNXnet/IP routing multicast address. Devices that do
    *         not implement KNXnet/IP routing shall set this value to 0.0.0.0
    */
   public byte[] getRoutingAddr()
   {
      return routingAddr;
   }

   /**
    * Set the 4-byte KNXnet/IP routing multicast address. Devices that do not
    * implement KNXnet/IP routing shall set this value to 0.0.0.0
    */
   public void setRoutingAddr(byte[] routingAddr)
   {
      this.routingAddr = routingAddr;
   }

   /**
    * Return the 6-byte MAC address of the device.
    */
   public int[] getMacAddr()
   {
      return macAddr;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int fromRawData(int[] rawData, int start) throws InvalidDataException
   {
      int pos = start + 1;

      medium = MediumType.valueOf(rawData[++pos]);
      status = rawData[++pos];
      busAddress = (rawData[++pos] << 8) | rawData[++pos];
      projectId = (rawData[++pos] << 8) | rawData[++pos];

      for (int i = 0; i < 6; ++i)
         serial[i] = rawData[++pos];

      for (int i = 0; i < 4; ++i)
         routingAddr[i] = (byte) rawData[++pos];

      for (int i = 0; i < 6; ++i)
         macAddr[i] = rawData[++pos];

      final StringBuffer sb = new StringBuffer();
      for (int i = 0; i < 30; ++i)
      {
         char ch = (char) rawData[++pos];
         if (ch == 0) break;
         sb.append(ch);
      }
      name = sb.toString();

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      int pos = start;

      // rawData[pos] = length  ... done at end
      rawData[++pos] = DeviceDescriptionType.DEVICE_INFO.code;
      rawData[++pos] = medium.code;
      rawData[++pos] = status;
      rawData[++pos] = busAddress;
      rawData[++pos] = projectId;

      for (int i = 0; i < 6; ++i)
         rawData[++pos] = serial[i];

      for (int i = 0; i < 4; ++i)
         rawData[++pos] = routingAddr[i];

      for (int i = 0; i < 6; ++i)
         rawData[++pos] = macAddr[i];

      for (int i = 0; i < 30; ++i)
         rawData[++pos] = name.length() <= i ? 0 : name.charAt(i);

      return pos - start;
   }
}
