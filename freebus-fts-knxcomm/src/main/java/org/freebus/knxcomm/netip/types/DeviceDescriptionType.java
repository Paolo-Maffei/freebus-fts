package org.freebus.knxcomm.netip.types;

import org.freebus.knxcomm.netip.blocks.DeviceInfoBlock;

/**
 * KNXnet/IP description type codes of {@link DeviceInfoBlock} device
 * information blocks.
 */
public enum DeviceDescriptionType
{
   /**
    * Device information.
    */
   DEVICE_INFO(0x01),

   /**
    * Service families that the device supports.
    */
   SERVICE_FAMILIES(0x02),

   /**
    * Reserved for future use.
    * 
    * Type values from 0x03 to 0xfd are reserved for future use.
    * {@link #valueOf(int)} will return this constant for all of these values.
    */
   RESERVED(0xfd),

   /**
    * Further data defined by the device manufacturer.
    */
   MFR_DATA(0xfe),

   /**
    * Not used.
    */
   UNUSED(0xff);

   /**
    * The byte code of the type.
    */
   public final int code;

   /**
    * @return the device-info type for the given value.
    * @throws IllegalArgumentException if no matching device-info type is found.
    */
   static public DeviceDescriptionType valueOf(int code)
   {
      for (DeviceDescriptionType v : values())
      {
         if (v.code == code)
            return v;
      }

      if (code >= 0x03 && code <= 0xfd)
         return DeviceDescriptionType.RESERVED;

      throw new IllegalArgumentException("Invalid KNXnet/IP device-info type: " + code);
   }

   /*
    * Internal constructor
    */
   private DeviceDescriptionType(int code)
   {
      this.code = code;
   }
}
