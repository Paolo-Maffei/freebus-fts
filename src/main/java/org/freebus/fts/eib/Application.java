package org.freebus.fts.eib;

/**
 * Application types. The A_ from the application types is omitted
 * (A_GroupValue_Read is {@link Application#GroupValue_Read} here).
 * See KNX Documentation 3/3/7 Application Layer.
 */
public enum Application
{
   GroupValue_Read(0x000, 0x3ff, 0, 16),
   GroupValue_Write(0x080, 0x3c0, 0, 14),
   GroupValue_Response(0x040, 0x3c0, -1, -1),

   IndividualAddress_Read(0x100, 0x3ff, 2, 2),
   IndividualAddress_Write(0x0c0, 0x3ff, 4, 4),
   IndividualAddress_Response(0x140, 0x3ff, -1, -1),

   ADC_Read(0x180, 0x3c0, 3, 3),
   ADC_Response(0x1c0, 0x3c0, -1, -1),

   Memory_Read(0x200, 0x3f0, 4, 4),
   Memory_Write(0x280, 0x3f0, 1, 16),
   Memory_Response(0x240, 0x3f0, -1, -1),

   UserMemory_Read(0x2c0, 0x3ff, -1, -1),
   UserMemory_Write(0x2c2, 0x3ff, -1, -1),
   UserMemory_Response(0x2c1, 0x3ff, -1, -1),

   UserMemoryBit_Write(0x2c4, 0x3ff, -1, -1),

   UserManufacturerInfo_Read(0x2c5, 0x3ff, -1, -1),
   UserManufacturerInfo_Response(0x2c6, 0x3ff, -1, -1),

   // apci 0x2c7 to 0x2f7 is reserved USERMSG (mask 0x3ff)
   // apci 0x2f8 to 0x2fe is manufacturer specific area for USERMSG

   DeviceDescriptor_Read(0x300, 0x3ff, -1, -1),
   DeviceDescriptor_Response(0x340, 0x3ff, -1, -1),

   Restart(0x380, 0x3ff, -1, -1),

   // Coupler specific services
   
   // TODO
   
   // Open media specific services

   // TODO

   GroupPropValue_InfoReport(0x3eb, 0x3ff, -1, -1);

   /**
    * The contents of the apci field.
    */
   public final int apci;

   /**
    * Bitmask for the apci field.
    */
   public final int mask;

   public final int min, max;
   
   /*
    * Internal constructor.
    */
   private Application(int apci, int mask, int min, int max)
   {
      this.apci = apci;
      this.mask = mask;
      this.min = min;
      this.max = max;
   }
}
