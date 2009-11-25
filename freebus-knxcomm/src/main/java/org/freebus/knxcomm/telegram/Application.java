package org.freebus.knxcomm.telegram;

/**
 * Types of telegram applications. The types are stored in the APCI field
 * of a telegram.
 */
public enum Application
{
   GroupValue_Read(0x000, 10, 0, 16),
   GroupValue_Write(0x080, 4, 0, 14),
   GroupValue_Response(0x040, 4, -1, -1),

   /**
    * Read the physical address of all devices that are in programming mode. Broadcast.
    * One data byte, its value is returned as data in {@link #IndividualAddress_Response}.
    */
   IndividualAddress_Read(0x100, 10, 2, 2),

   /**
    * Set the physical address of all devices that are in programming mode. Broadcast.
    * Two data bytes: the new physical address.
    */
   IndividualAddress_Write(0x0c0, 10, 4, 4),

   /**
    * The response to {@link #IndividualAddress_Read}. Broadcast.
    * One data byte: The data byte that was sent as data by {@link #IndividualAddress_Read}.
    */
   IndividualAddress_Response(0x140, 10, 1, 1),

   IndividualAddressSerialNumber_Read(0x3dc, 10, 6, 6),
   IndividualAddressSerialNumber_Write(0x3de, 10, 8, 8),
   IndividualAddressSerialNumber_Response(0x3dd, 10, 8, 8),

   ADC_Read(0x180, 4, 3, 3),
   ADC_Response(0x1c0, 4, -1, -1),

   /**
    * Read application memory.
    * 3 data bytes: number of bytes to read (1..63), 2-byte address.
    * Response: {@link #Memory_Response}.
    */
   Memory_Read(0x200, 6, 3, 3),

   /**
    * Response to {@link #Memory_Read}.
    * 3+ data bytes: number of bytes to read (1..63), 2-byte address, memory contents.
    */
   Memory_Response(0x240, 6, 3, 15),

   /**
    * Write application memory.
    * 3+ data bytes: number of bytes to read (1..63), 2-byte address, memory contents.
    */
   Memory_Write(0x280, 6, 3, 15),

   /**
    * Read user-data memory.
    * 3 data bytes: 4-bit address extension + 4-bit number of bytes, 2-byte address.
    * Response: {@link #UserMemory_Response}.
    */
   UserMemory_Read(0x2c0, 10, 3, 3),

   /**
    * Response to {@link #UserMemory_Read}.
    * 3+ data bytes: 4-bit address extension + 4-bit number of bytes, 2-byte address, memory contents.
    */
   UserMemory_Response(0x2c1, 10, 3, 15),

   /**
    * Write user-data memory.
    * 3+ data bytes: 4-bit address extension + 4-bit number of bytes, 2-byte address, memory contents.
    */
   UserMemory_Write(0x2c2, 10, 3, 15),

   UserMemoryBit_Write(0x2c4, 10, -1, -1),

   UserManufacturerInfo_Read(0x2c5, 10, -1, -1),
   UserManufacturerInfo_Response(0x2c6, 10, -1, -1),

   // APCI 0x2c7 to 0x2f7 is reserved USERMSG (mask 0x3ff)
   // APCI 0x2f8 to 0x2fe is manufacturer specific area for USERMSG

   /**
    * Read a device descriptor.
    * One data byte: the descriptor type to read.
    * Response is {@link #DeviceDescriptor_Response}. 
    */
   DeviceDescriptor_Read(0x300, 10, 1, 1),

   /**
    * Response to {@link #DeviceDescriptor_Read}.
    */
   DeviceDescriptor_Response(0x340, 10, 1, 15),

   


   Restart(0x380, 10, -1, -1),

   // Coupler specific services
   
   // TODO
   
   // Open media specific services

   // TODO

   /**
    * FTS-internal value, to indicate an invalid or empty application type.
    */
   None(0xffff, 10, -1, -1);

   /**
    * The contents of the APCI field.
    */
   public final int apci;

   /**
    * Number of bits that the APCI type requires (1..10).
    */
   public final int bits;

   /**
    * Minimum length for data in the telegram. -1 if unspecified.
    */
   public final int minData;
   
   /**
    * Maximum length for data in the telegram. -1 if unspecified.
    */
   public final int maxData;

   // The bit-masks for the APCI-bits values
   private static final int[] apciMasks = new int[] { 0, 0x200, 0x300, 0x380, 0x3c0, 0x3e0, 0x3f0, 0x3f8, 0x3fc, 0x3fe, 0x3ff };

   /**
    * @return the bit-mask for the APCI field.
    */
   public int getMask()
   {
      return apciMasks[bits];
   }

   /**
    * @return the bit-mask for the data area in the APCI bytes, or zero if no data
    *         can be stored.
    */
   public int getDataMask()
   {
      return 0x3ff & ~apciMasks[bits];
   }

   /**
    * @return the application type in human readable form.
    */
   @Override
   public String toString()
   {
      return name() + String.format(" [%02x]", apci);
   }

   /**
    * @return the application type for the given APCI field.
    * @throws InvalidDataException 
    */
   public static Application valueOf(int apci) throws InvalidDataException
   {
      for (Application a: values())
         if ((apci & apciMasks[a.bits]) == a.apci) return a;

      throw new InvalidDataException("None APCI field contents", apci);
   }

   /*
    * Internal constructor.
    */
   private Application(int apci, int apciBits, int minData, int maxData)
   {
      this.apci = apci;
      this.bits = apciBits;
      this.minData = minData;
      this.maxData = maxData;
   }
}
