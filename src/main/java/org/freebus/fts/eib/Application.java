package org.freebus.fts.eib;

/**
 * Types of telegram applications. The types are stored in the APCI field
 * of a telegram.
 * 
 * The "A_" from the application type names are omitted
 * (A_GroupValue_Read is {@link Application#GroupValue_Read} here).
 */
public enum Application
{
   GroupValue_Read(0x000, 10, 0, 16),
   GroupValue_Write(0x080, 4, 0, 14),
   GroupValue_Response(0x040, 4, -1, -1),

   IndividualAddress_Read(0x100, 10, 2, 2),
   IndividualAddress_Write(0x0c0, 10, 4, 4),
   IndividualAddress_Response(0x140, 10, -1, -1),

   IndividualAddressSerialNumber_Read(0x3dc, 10, 6, 6),
   IndividualAddressSerialNumber_Write(0x3de, 10, 8, 8),
   IndividualAddressSerialNumber_Response(0x3dd, 10, 8, 8),

   ADC_Read(0x180, 4, 3, 3),
   ADC_Response(0x1c0, 4, -1, -1),

   Memory_Read(0x200, 6, 4, 4),
   Memory_Write(0x280, 6, 1, 16),
   Memory_Response(0x240, 6, -1, -1),

   UserMemory_Read(0x2c0, 10, -1, -1),
   UserMemory_Write(0x2c2, 10, -1, -1),
   UserMemory_Response(0x2c1, 10, -1, -1),

   UserMemoryBit_Write(0x2c4, 10, -1, -1),

   UserManufacturerInfo_Read(0x2c5, 10, -1, -1),
   UserManufacturerInfo_Response(0x2c6, 10, -1, -1),

   // APCI 0x2c7 to 0x2f7 is reserved USERMSG (mask 0x3ff)
   // APCI 0x2f8 to 0x2fe is manufacturer specific area for USERMSG

   DeviceDescriptor_Read(0x300, 10, -1, -1),
   DeviceDescriptor_Response(0x340, 10, -1, -1),

   


   Restart(0x380, 10, -1, -1),

   // Coupler specific services
   
   // TODO
   
   // Open media specific services

   // TODO

   /**
    * FTS-internal value, to indicate an invalid application type.
    */
   Invalid(0xffff, 10, -1, -1);

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

      throw new InvalidDataException("Invalid APCI field contents", apci);
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
