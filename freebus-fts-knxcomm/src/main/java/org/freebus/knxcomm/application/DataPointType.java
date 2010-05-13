package org.freebus.knxcomm.application;

/**
 * Data point types for data exchange, e.g. with
 *
 * {@link ApplicationType#GroupValue_Read} or
 * {@link ApplicationType#GroupValue_Write} telegrams.
 *
 * TODO Not complete!
 */
public enum DataPointType
{
   /**
    * No data point type. FTS internal use.
    */
   None(0),

   /**
    * 1 bit data point type. Used for switch, boolean, enable, open/close, alarm,
    * ...
    * <p>
    * 0 - off 1 - on
    */
   Bool(1),

   /**
    * 2 bit data point type. This is a {@link #Bool boolean} type with an additional
    * control bit. Used for switch control, step control, ...
    * <p>
    * 00 - no control 01 - no control 10 - control, off 11 - control, on
    */
   BoolControl(2),

   /**
    * 4 bit data point type: dimming, control blinds.
    * <p>
    * Bit 3 - direction: 0 decrease, 1 increase. <br>
    * Bit 0..2 - step code: 0 is break, >0 is 2^(value-1) intervals.
    */
   Dimming(4),

   /**
    * Character (1 byte). 7 bit ASCII or 8 bit ISO-8859-1
    */
   Char(8),

   /**
    * 8-bit unsigned value. For scaling, angle, percent, etc.
    */
   UnsignedByte(8),

   /**
    * 8-bit signed, relative, value. For percent, value difference.
    */
   SignedByte(8),

   /**
    * 2 byte unsigned value. For counter, time period, length, etc.
    */
   UnsignedShort(16),

   /**
    * 2 byte signed value. For counter, percent, delta time, rotation angle,
    * etc.
    */
   SignedShort(16),

   /**
    * 2 byte float value. For temperature, humidity, lux, etc.
    * <p>
    * Bit 15 - sign <br>
    * Bit 14..11 - exponent <br>
    * Bit 10..0 - mantissa, in two's complement notation
    * <p>
    * float_value = 0.01 * mantissa * 2 ^ exponent
    */
   FloatShort(16),

   /**
    * Time (3 byte).
    * <p>
    * Byte 0: 3 bit day, 5 bit hour <br>
    * Byte 1: minutes <br>
    * Byte 2: seconds
    */
   Time(24),

   /**
    * Date (3 byte).
    * <p>
    * Byte 0: day, byte 1: month, byte 2: year (0..99)
    */
   Date(24),

   /**
    * Unsigned long value (4 bytes)
    */
   UnsignedLong(32),

   /**
    * Signed long value (4 bytes)
    */
   SignedLong(32),

   /**
    * Float value (4 bytes). Encoded in the IEEE 754 floating point format.
    */
   Float(32),

   /**
    * Access data (4 bytes).
    */
   Access(32),

   /**
    * String. 14 bytes, unused bytes are zero.
    */
   String(14 * 8),

   /**
    * Scene number (1 byte)
    * <p>
    * 6 bit scene number, the 2 high bits are reserved.
    */
   SceneNumber(8),

   /**
    * Scene control (1 byte)
    * <p>
    * Bit 7: control - 0 to activate the scene, 1 - to learn the scene <br>
    * Bit 6: reserved <br>
    * Bit 5..0: scene number
    */
   SceneControl(8),

   /**
    * Date time (8 bytes)
    *
    * Byte 0: year <br>
    * Byte 1: month <br>
    * Byte 2: day of month <br>
    * Byte 3: 3 bit day of week + 5 bit hour <br>
    * Byte 4: minutes <br>
    * Byte 5: seconds <br>
    * Byte 6: status <br>
    * Byte 7: bit 7: clock quality, bits 6..0 reserved
    */
   DateTime(64),

   /**
    * Enum (1 byte). Used for various enumerations.
    */
   Enum(8),

   /**
    * General status (1 byte)
    * <p>
    * Bit 7..5: reserved, <br>
    * bit 4: datapoint alarm status not acknowledged, <br>
    * bit 3: datapoint is in alarm, <br>
    * bit 2: datapoint main value is overridden, <br>
    * bit 1: datapoint main value is corrupted due to failure, <br>
    * bit 0: datapoint value is out of service.
    */
   Status(8),

   /**
    * Device control (1 byte)
    * <p>
    * Bit 7..3: reserved, <br>
    * bit 2: verify mode enabled, <br>
    * bit 1: datagram with the own address as source has been received, <br>
    * bit 0: user application is stopped.
    */
   DeviceControl(8),

   /**
    * 2 bit enum
    */
   ShortEnum(2),

   /**
    * String of variable length. The string is zero terminated. No length
    * information is contained in the telegram.
    */
   VarString(-1),

   /**
    * Scene info (1 byte)
    * <p>
    * Bit 7: reserved <br>
    * Bit 6: scene is active flag <br>
    * Bit 5..0: scene number
    */
   SceneInfo(8),

   /**
    * UTF-8 String of variable length. The string is zero terminated. No length
    * information is contained in the telegram.
    */
   Utf8String(-1),

   /**
    * Combined on/off info (4 bytes).
    * <p>
    * 16 bit on/off for output 1..16, <br>
    * 16 bit mask bit for on/off output 1..16.
    */
   CombinedInfoOnOff(32),

   //
   // TODO not complete
   //

   /**
    * No data point type. FTS internal use.
    */
   _END(0);

   private final int bits;

   /**
    * @return the number of bits that the data point type requires. -1 if
    *         variable.
    */
   public int getBits()
   {
      return bits;
   }

   /*
    * Internal constructor.
    */
   private DataPointType(int bits)
   {
      this.bits = bits;
   }
}
