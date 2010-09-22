package org.freebus.knxcomm.application.devicedescriptor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

/**
 * Device descriptor type 2. This device descriptor contains several fields.
 */
public class DeviceDescriptor2 implements DeviceDescriptor
{
   private int manufacturer;
   private int type;
   private int version;
   private int misc;
   private int logicalTags;
   private int[] channelInfos = new int[4];

   /**
    * Create an empty device descriptor type 2 object.
    */
   public DeviceDescriptor2()
   {
   }

   /**
    * @return the numerical descriptor type: 2.
    */
   public int getTypeCode()
   {
      return 2;
   }

   /**
    * @return the manufacturer
    */
   public int getManufacturer()
   {
      return manufacturer;
   }

   /**
    * Set the manufacturer.
    * 
    * @param manufacturer - the manufacturer to set
    */
   public void setManufacturer(int manufacturer)
   {
      this.manufacturer = manufacturer;
   }

   /**
    * @return the device type.
    */
   public int getType()
   {
      return type;
   }

   /**
    * Set the device type.
    * 
    * @param type - the device type to set
    */
   public void setType(int type)
   {
      this.type = type;
   }

   /**
    * @return the version
    */
   public int getVersion()
   {
      return version;
   }

   /**
    * Set the version.
    * 
    * @param version - the version to set
    */
   public void setVersion(int version)
   {
      this.version = version;
   }

   /**
    * @return the misc bits.
    */
   public int getMisc()
   {
      return misc;
   }

   /**
    * Set the misc bits.
    * 
    * @param misc - the misc to set
    */
   public void setMisc(int misc)
   {
      this.misc = misc;
   }

   /**
    * @return the logical tags.
    */
   public int getLogicalTags()
   {
      return logicalTags;
   }

   /**
    * Set the base value for local tags, derived from the local selector value.
    * 0x3f means no selector active.
    * 
    * @param logicalTags - the logical tags to set
    */
   public void setLogicalTags(int logicalTags)
   {
      this.logicalTags = logicalTags;
   }

   /**
    * Get the channel code of a channel.
    * 
    * @param idx - the index in the channel info table (0..3)
    * 
    * @return the channel code. Zero if unused.
    */
   public int getChannelCode(int idx)
   {
      return channelInfos[idx] & 0x1fff;
   }

   /**
    * Set the channel code of a channel.
    * 
    * @param idx - the index in the channel info table (0..3)
    * @param code - the channel code (0..8191)
    */
   public void setChannelCode(int idx, int code)
   {
      channelInfos[idx] = (channelInfos[idx] & 0xe000) | (code & 0x1fff);
   }

   /**
    * Get the number of channels that the channel info with the given index has.
    * 
    * @param idx - the index in the channel info table (0..3)
    * 
    * @return the number of channels (0..7)
    */
   public int getChannelCount(int idx)
   {
      return (channelInfos[idx] >> 13) & 7;
   }

   /**
    * Get the number of channels that the channel info with the given index has.
    * 
    * @param idx - the index in the channel info table (0..3)
    * @param count - the number of channels (0..7)
    */
   public void setChannelCount(int idx, int count)
   {
      channelInfos[idx] = (channelInfos[idx] & 0x1fff) | ((count & 7) << 13);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void readData(DataInput in, int length) throws IOException
   {
      manufacturer = in.readUnsignedShort();
      type = in.readUnsignedShort();
      version = in.readUnsignedByte();

      final int mlt = in.readUnsignedByte();
      misc = (mlt >> 6) & 3;
      logicalTags = mlt & 63;

      for (int i = 0; i < channelInfos.length; ++i)
         channelInfos[i] = in.readUnsignedShort();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeData(DataOutput out) throws IOException
   {
      out.writeShort(manufacturer);
      out.writeShort(type);
      out.writeByte(version);
      out.writeByte((misc << 6) | (logicalTags & 63));

      for (int i = 0; i < channelInfos.length; ++i)
         out.writeShort(channelInfos[i]);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return (manufacturer << 16) | (type << 12) | (version << 8) | (misc << 4) | logicalTags;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof DeviceDescriptor2))
         return false;

      final DeviceDescriptor2 oo = (DeviceDescriptor2) o;

      return manufacturer == oo.manufacturer && type == oo.type && version == oo.version && misc == oo.misc
            && logicalTags == oo.logicalTags && Arrays.equals(channelInfos, oo.channelInfos);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      final StringBuffer sb = new StringBuffer();

      sb.append("manufacturer ").append(manufacturer);
      sb.append(" type ").append(type);
      sb.append(" version ").append(version);

      return sb.toString();
   }
}
