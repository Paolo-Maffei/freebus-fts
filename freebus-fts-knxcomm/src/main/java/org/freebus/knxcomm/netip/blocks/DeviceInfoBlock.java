package org.freebus.knxcomm.netip.blocks;

import org.freebus.knxcomm.MediumType;
import org.freebus.knxcomm.netip.types.DescriptionInfoType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Device information block. Contains description about the device.
 */
public final class DeviceInfoBlock implements DescriptionInfoBlock
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
   public int fromData(int[] data, int start) throws InvalidDataException
   {
      int pos = start;

      final int blockLength = data[pos++];

      final int typeCode = data[pos++];
      final DescriptionInfoType type = DescriptionInfoType.valueOf(typeCode);
      if (type != DescriptionInfoType.DEVICE_INFO)
         throw new InvalidDataException("Invalid type " + type + ", expected " + DescriptionInfoType.DEVICE_INFO, typeCode);
      
      medium = MediumType.valueOf(data[pos++]);
      status = data[pos++];
      busAddress = (data[pos++] << 8) | data[pos++];
      projectId = (data[pos++] << 8) | data[pos++];

      for (int i = 0; i < 6; ++i)
         serial[i] = data[pos++];

      for (int i = 0; i < 4; ++i)
         routingAddr[i] = (byte) data[pos++];

      for (int i = 0; i < 6; ++i)
         macAddr[i] = data[pos++];

      final StringBuffer sb = new StringBuffer();
      for (int i = 0; i < 30; ++i)
      {
         char ch = (char) data[pos + i];
         if (ch == 0) break;
         sb.append(ch);
      }
      pos += 30;
      name = sb.toString();

      if (pos - start != blockLength)
         throw new InvalidDataException("Invalid block length: expected " + blockLength + " read " + (pos - start), blockLength);

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toData(int[] data, int start)
   {
      int pos = start;

      ++pos; // length is written at end

      data[pos++] = DescriptionInfoType.DEVICE_INFO.code;
      data[pos++] = medium.code;
      data[pos++] = status;
      data[pos++] = (busAddress >> 8) & 0xff;
      data[pos++] = busAddress & 0xff;
      data[pos++] = (projectId >> 8) & 0xff;
      data[pos++] = projectId & 0xff;

      for (int i = 0; i < 6; ++i)
         data[pos++] = serial[i];

      for (int i = 0; i < 4; ++i)
         data[pos++] = routingAddr[i];

      for (int i = 0; i < 6; ++i)
         data[pos++] = macAddr[i];

      for (int i = 0; i < 30; ++i)
         data[pos++] = name.length() <= i ? 0 : name.charAt(i);

      return pos - start;
   }
}
